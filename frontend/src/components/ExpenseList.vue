<template>
  
  <div class="expenses-container">
    <ErrorHandler v-if="errorFlag" :message="errorMessage" @dismiss="errorFlag = false" />

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
      @filter-change="handleFilterChange"
    />
  
    
    <div class="buttons-row">
      <button @click="toggleCategoryForm">Add New Category</button>
      <button @click="toggleForm">Add New Expense</button>
    </div>

    <transition name="fade">
      <div v-if="showCatergoryForm">
        <CategoryManager></CategoryManager>
      </div>
    </transition>
    <transition name="fade">
      <div v-if="showForm">
        <expense-form
          :expense="currentEditExpense"
          @expense-added="handleAdded"
          @expense-updated="handleUpdated"
          @error="handleError"
        />
      </div>
    </transition>

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
      showForm: false,
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

      // If filters are active
      if (this.activeFilters) {
        const f = this.activeFilters;

        if (f.description) params.append('description', f.description);
        if (f.category) params.append('category', f.category);
        if (f.minAmount !== null && f.minAmount !== '') params.append('minAmount', f.minAmount);
        if (f.maxAmount !== null && f.maxAmount !== '') params.append('maxAmount', f.maxAmount);
        if (f.startDate) params.append('startDate', f.startDate);
        if (f.endDate) params.append('endDate', f.endDate);

        const url = `http://localhost:8081/api/expenses/filter?${params.toString()}`;
        console.log("Fetching with filters:", url);

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

      // Default behavior (no filters)
      const endpoint =
        this.currentTab === 'archive'
          ? 'http://localhost:8081/api/expenses/archived'
          : 'http://localhost:8081/api/expenses';

      fetch(`${endpoint}?${params.toString()}`, {
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
    },

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
      this.showCatergoryForm = !this.showCatergoryForm;
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
      this.$router.push('/');
    },
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
