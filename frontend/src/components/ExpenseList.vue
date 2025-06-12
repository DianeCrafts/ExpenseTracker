/**
 * ExpensesView.vue
 * 
 * This component manages the display and interaction for the Expenses and Archive views.
 * It handles tab switching, expense listing, filtering, pagination, and form toggling.
 * 
 * Subcomponents used:
 * - ExpenseForm: For adding/editing an expense
 * - ExpenseTable: To display and interact with expense records
 * - CategoryManager: For managing categories
 * - ErrorHandler: For showing error messages
 */


<template>
  
  <div class="expenses-container">
    <!-- Error Display -->
    <Error-handler v-if="errorFlag" :message="errorMessage" @dismiss="errorFlag = false" />

    <!-- Tabs for switching views -->
    <div>
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

    <!-- Table of expenses -->
    <Expense-table
      :currentTab="currentTab"
      :expenses="expenses"
      :currentPage="currentPage"
      :totalPages="totalPages"
      :pageSize="pageSize"
      @change-page="changePage"
      @change-page-size="changePageSize"
      @delete="deleteRecord"
      @edit="updateRecord"
      @filter-change="handleFilterChange"
    />
  
    <!-- Buttons to open forms -->
    <div class="buttons-row">
      <button @click="toggleCategoryForm">Add New Category</button>
      <button @click="toggleExpenseForm">Add New Expense</button>
    </div>
    <!-- Conditional Forms for category and expense -->
    <transition name="fade">
      <div v-if="showCatergoryForm">
        <Category-manager></Category-manager>
      </div>
    </transition>
    <transition name="fade">
      <div v-if="showExpenseForm">
        <expense-form
          :expense="currentEditExpense"
          @expense-added="handleAdded"
          @expense-updated="handleUpdated"
          @error="handleError"
        />
      </div>
    </transition>
    <!-- Logout button -->
    <button class="logout-button" @click="logout">Logout</button>
  </div>
</template>

<script>
import ExpenseForm from './ExpenseForm.vue';
import ExpenseTable from './ExpenseTable.vue'
import CategoryManager from './CategoryManager.vue'
import ErrorHandler from './ErrorHandler.vue'

export default {
  components: { ExpenseForm, ExpenseTable, CategoryManager, ErrorHandler },
  data() {
    return {
      currentTab: 'expenses',
      expenses: [],
      currentPage: 0,
      totalPages: 0,
      pageSize: 10,
      showCatergoryForm: false,
      showExpenseForm: false,
      currentEditExpense: null,
      activeFilters: null,
      errorFlag: false,
      errorMessage: '',
    };
  },
  methods: {
    fetchExpenses() {
      const auth = localStorage.getItem('auth');
      const params = new URLSearchParams({
        page: this.currentPage,
        size: this.pageSize
      });
      
      const f = this.activeFilters;
      if (f && f.category) params.append('category', f.category);
        if (f && f.description) params.append('description', f.description);
        if (f && f.minAmount !== null && f.minAmount !== '') params.append('minAmount', f.minAmount);
        if (f && f.maxAmount !== null && f.maxAmount !== '') params.append('maxAmount', f.maxAmount);
        if (f && f.startDate) params.append('startDate', f.startDate);
        if (f && f.endDate) params.append('endDate', f.endDate);
      if (this.currentTab === 'expenses'){
        const url = `http://localhost:8081/api/expenses/filter?${params.toString()}`;
        console.log(url);
        fetch(url, {
          headers: { Authorization: `Basic ${auth}` }
        })
          .then(res => res.json())
          .then(data => {
            this.expenses = data.expenses;
            this.totalPages = data.totalPages;
            this.currentPage = data.currentPage;
          })
          .catch(error => {
            console.error(error);
            this.errorFlag = true;
            this.errorMessage = 'An error occurred while fetching expenses.';
          });
        return;
      }
      else if (this.currentTab === 'archive') {
        const url = `http://localhost:8081/api/expenses/filter/archived?${params.toString()}`;
        fetch(url, {
                headers: { Authorization: `Basic ${auth}` }
              })
                .then(res => res.json())
                .then(data => {
                  this.expenses = data.expenses;
                  this.totalPages = data.totalPages;
                  this.currentPage = data.currentPage;
                })
                .catch(error => {
                  console.error(error);
                  this.errorFlag = true;
                  this.errorMessage = 'An error occurred while fetching expenses.';
                });
      }
    },

    // Triggered when filters change (from child component)
    handleFilterChange(filters) {
      
      this.activeFilters = filters;
      this.currentPage = 0;
      this.fetchExpenses();
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

    toggleCategoryForm(){
      if (!this.showCatergoryForm && this.showExpenseForm)
        this.showExpenseForm = !this.showExpenseForm;
      this.showCatergoryForm = !this.showCatergoryForm;
    },
    toggleExpenseForm() {
      if (!this.showExpenseForm && this.showCatergoryForm)
        this.showCatergoryForm = !this.showCatergoryForm;
      this.showExpenseForm = !this.showExpenseForm;
    },

    // Handler after a new expense is added
    handleAdded() {
      this.fetchExpenses();
      this.showExpenseForm = false;
    },

    // Handler after an expense is updated
    handleUpdated() {
      this.fetchExpenses();
      this.showExpenseForm = false;
      this.currentEditExpense = null;
    },

    // Delete (soft) an expense by ID
    async deleteRecord(id) {
      await fetch(`http://localhost:8081/api/expenses/softDelete/${id}`, {
        method: 'DELETE',
        headers: { Authorization: `Basic ${localStorage.getItem('auth')}` },
      });
      this.fetchExpenses();
    },

    // Prepare expense for editing
    updateRecord(id) {
      this.currentEditExpense = this.expenses.find(exp => exp.id === id);
      this.showExpenseForm = true;
    },

    logout() {
      localStorage.removeItem('auth');
      this.$router.push('/');
    },

    // Switch between tabs (Expenses / Archive)
    switchTab(tabName) {
      this.currentTab = tabName;
      this.fetchExpenses();
    },

    handleError(message) {
      this.errorMessage = message;
      this.errorFlag = true;
    }
  },
  mounted() {
    this.fetchExpenses();
  }
};
</script>
<style scoped>
.expenses-container {
  max-width: 60vw;
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
  display: inline-flex;
  margin: 16px 0;
}


button {
  background-color: #555;
  color: white;
  padding: 10px 18px;
  margin-right: 5px;
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
