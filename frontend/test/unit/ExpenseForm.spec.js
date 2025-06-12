
const { mount } = require('@vue/test-utils');
const ExpenseForm = require('../../src/components/ExpenseForm.vue')
const axios = require('axios');


jest.mock('axios')

describe('ExpenseForm.vue', () => {
  let wrapper

  const mockCategories = [
    { id: 1, name: 'Food' },
    { id: 2, name: 'Travel' }
  ]

  beforeEach(async () => {
    axios.get.mockResolvedValue({ data: mockCategories })
    wrapper = mount(ExpenseForm, {
      props: { expense: null },
      global: {
        mocks: {
          localStorage: {
            getItem: jest.fn().mockReturnValue('dXNlcjpwYXNz') // mock Basic auth token
          }
        }
      }
    })
    await flushPromises()
  })

  afterEach(() => {
    jest.clearAllMocks()
  })

  it('fetches categories on mount', () => {
    expect(axios.get).toHaveBeenCalledWith(
      'http://localhost:8081/api/categories',
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: 'Basic dXNlcjpwYXNz'
        })
      })
    )
    expect(wrapper.vm.categories).toEqual(mockCategories)
  })

  it('fills in form when expense prop is passed', async () => {
    const testExpense = {
      id: 5,
      description: 'Groceries',
      amount: 42.5,
      date: '2025-06-01',
      category: { id: 1 }
    }

    await wrapper.setProps({ expense: testExpense })

    expect(wrapper.vm.description).toBe('Groceries')
    expect(wrapper.vm.amount).toBe(42.5)
    expect(wrapper.vm.date).toBe('2025-06-01')
    expect(wrapper.vm.categoryId).toBe(1)
  })

  it('emits "expense-added" on successful submit (POST)', async () => {
    axios.post.mockResolvedValue({})

    // fill in form fields
    await wrapper.setData({
      description: 'Test Expense',
      amount: 100,
      date: '2025-06-05',
      categoryId: 2
    })

    await wrapper.vm.handleSubmit()

    expect(axios.post).toHaveBeenCalled()
    expect(wrapper.emitted('expense-added')).toBeTruthy()
  })

  it('emits "expense-updated" on successful submit (PUT)', async () => {
    const testExpense = {
      id: 3,
      description: 'Old',
      amount: 10,
      date: '2025-01-01',
      category: { id: 1 }
    }

    await wrapper.setProps({ expense: testExpense })

    axios.put.mockResolvedValue({})

    await wrapper.setData({
      description: 'Updated Expense',
      amount: 99,
      date: '2025-06-06',
      categoryId: 2
    })

    await wrapper.vm.handleSubmit()

    expect(axios.put).toHaveBeenCalledWith(
      'http://localhost:8081/api/expenses/3',
      expect.any(Object),
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: 'Basic dXNlcjpwYXNz'
        })
      })
    )

    expect(wrapper.emitted('expense-updated')).toBeTruthy()
  })

  it('emits "error" on failed category fetch', async () => {
    axios.get.mockRejectedValue({ response: { data: { message: 'Auth failed' } } })

    const errorWrapper = mount(ExpenseForm, {
      global: {
        mocks: {
          localStorage: {
            getItem: jest.fn().mockReturnValue('badtoken')
          }
        }
      }
    })

    await flushPromises()

    expect(errorWrapper.emitted('error')).toBeTruthy()
  })

  it('emits "error" on failed submit', async () => {
    axios.post.mockRejectedValue({ response: { data: { message: 'Bad request' } } })

    await wrapper.setData({
      description: 'Broken',
      amount: 999,
      date: '2025-06-07',
      categoryId: 1
    })

    await wrapper.vm.handleSubmit()

    expect(wrapper.emitted('error')).toBeTruthy()
  })
})
