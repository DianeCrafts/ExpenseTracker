<template>
  <div class="expenses-container">
    <div>
    <!-- Tab Buttons -->
    <div class="titles-wrapper">
      <button
        class="tab-button"
        :class="{ active: currentTab === 'expenses' }"
        @click="switchTab('expenses')"
      >
        Expenses
      </button>
      <button
        class="tab-button"
        :class="{ active: currentTab === 'archive' }"
        @click="switchTab('archive')"
      >
        Archive
      </button>
    </div>

   
  </div>

    <!-- Use your reusable component -->
    <ExpenseTable
      :currentTab="currentTab"
      :expenses="expenses"
      :currentPage="currentPage"
      :totalPages="totalPages"
      :pageSize="pageSize"
      @change-page="changePage"
      @change-page-size="changePageSize"
      @delete="deleteRecord"
      @edit="updateRecord"
    />

    <div class="buttons-row">
      <button @click="toggleForm">Add New Expense</button>
    </div>

    <transition name="fade">
      <div v-if="showForm">
        <expense-form
          :expense="currentEditExpense"
          @expense-added="handleAdded"
          @expense-updated="handleUpdated"
        />
      </div>
    </transition>

    <button class="logout-button" @click="logout">Logout</button>
  </div>
</template>

<script>
import ExpenseForm from './ExpenseForm.vue';
import ExpenseTable from './ExpenseTable.vue'

export default {
  components: { ExpenseForm, ExpenseTable },
  data() {
    return {
      currentTab: 'expenses',
      expenses: [],
      currentPage: 0,
      totalPages: 0,
      pageSize: 10,
      showForm: false,
      currentEditExpense: null,
    };
  },
  methods: {
    fetchExpenses() {
      const auth = localStorage.getItem('auth');
      const params = new URLSearchParams({
        page: this.currentPage,
        size: this.pageSize
      });

      // Choose the endpoint based on currentTab
      const endpoint =
        this.currentTab === 'archive'
          ? 'http://localhost:8081/api/expenses/archived'
          : 'http://localhost:8081/api/expenses';

      fetch(`${endpoint}?${params}`, {
        headers: { Authorization: `Basic ${auth}` }
      })
        .then(res => res.json())
        .then(data => {
          this.expenses = data.expenses;
          this.totalPages = data.totalPages;
          this.currentPage = data.currentPage;
        })
        .catch(console.error);
    },



    changePage(index) {
      this.currentPage = index;
      this.fetchExpenses();
    },
    changePageSize(newSize) {
      this.pageSize = newSize;
      this.currentPage = 0;
      this.fetchExpenses();
    },
    toggleForm() {
      this.showForm = !this.showForm;
    },
    handleAdded() {
      this.fetchExpenses();
      this.showForm = false;
    },
    handleUpdated() {
      this.fetchExpenses();
      this.showForm = false;
    },
    async deleteRecord(id) {
      await fetch(`http://localhost:8081/api/expenses/softDelete/${id}`, {
        method: 'DELETE',
        headers: { Authorization: `Basic ${localStorage.getItem('auth')}` },
      });
      this.fetchExpenses();
    },
    updateRecord(id) {
      this.currentEditExpense = this.expenses.find(exp => exp.id === id);
      this.showForm = true;
    },
    logout() {
      localStorage.removeItem('auth');
      this.$router.push('/login');
    },
    switchTab(tabName) {
      this.currentTab = tabName;
      this.fetchExpenses();
    },
  },
  mounted() {
    this.fetchExpenses();
  }
};
</script>
<style scoped>
.expenses-container {
  max-width: 900px;
  margin: 40px auto;
  padding: 20px;
  background: #eeeeee;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  font-family: sans-serif;
}

.titles-wrapper {
  width: 100%;
  display: flex;
  
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
}

.tab-button {
  flex: 1;
  padding: 5px 0;
  background-color: #555;
  margin:4px;
  color: white;
  font-size: 1rem;
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: background 0.2s ease;
}

.tab-button:hover {
  background-color: #444;
}

.tab-button.active {
  background-color: #333;
  font-weight: bold;
}



.buttons-row {
  display: flex;
  margin: 16px 0;
}

button {
  background-color: #555;
  color: white;
  padding: 10px 18px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s ease;
}

button:hover {
  background-color: #444;
}


/* logout */
.logout-button {
  position: fixed;
  bottom: 20px;
  left: 20px;
  background: #999;
  color: white;
  padding: 8px 14px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.logout-button:hover {
  background: #777;
}
</style>
