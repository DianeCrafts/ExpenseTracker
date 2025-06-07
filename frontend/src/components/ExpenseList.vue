
<template>
  <div class="expenses-container">
    <h2>Expenses</h2>

    <table class="expenses-table">
      <thead>
        <tr>
          <th>Description<br />
            <input v-model="filters.description" placeholder="Filter" />
          </th>
          <th>Amount<br />
            <input v-model="filters.amount" placeholder="Filter" />
          </th>
          <th>Date<br />
            <div class="date-filter">
              <input type="date" v-model="filters.startDate" />
              <span>to</span>
              <input type="date" v-model="filters.endDate" />
            </div>
          </th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="expense in filteredExpenses" :key="expense.id">
          <td>{{ expense.description }}</td>
          <td>{{ expense.amount }}</td>
          <td>{{ expense.date }}</td>
          <td>
            <button @click="deleteRecord(expense.id)" class="icon-button" title="Delete">
              🗑️
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <div class="buttons-row">
      <button @click="toggleForm">Add New Expense</button>
    </div>

    <transition name="fade">
      <div v-if="showForm">
        <expense-form @expense-added="handleAdded" />
      </div>
    </transition>

    <button class="logout-button" @click="logout">Logout</button>
  </div>
</template>


<script>
import axios from 'axios';
import ExpenseForm from './ExpenseForm.vue';



export default {
  components: { ExpenseForm },
  data() {
    return {
      expenses: [],
      selected: [],
      showForm: false,
      filters: {
        description: '',
        amount: '',
        startDate: '',
        endDate: ''
      }
    };
  },
  computed: {
    filteredExpenses() {
    return this.expenses.filter(expense => {
      const matchesDescription = this.filters.description === '' || expense.description.toLowerCase().includes(this.filters.description.toLowerCase());
      const matchesAmount = this.filters.amount === '' || expense.amount.toString().includes(this.filters.amount);
      
      const expenseDate = new Date(expense.date);
      const startDate = this.filters.startDate ? new Date(this.filters.startDate) : null;
      const endDate = this.filters.endDate ? new Date(this.filters.endDate) : null;

      const matchesStartDate = !startDate || expenseDate >= startDate;
      const matchesEndDate = !endDate || expenseDate <= endDate;

      return matchesDescription && matchesAmount && matchesStartDate && matchesEndDate;
    });
  },
    allSelected() {
      return this.filteredExpenses.length && this.selected.length === this.filteredExpenses.length;
    }
  },
  methods: {
    fetchExpenses() {
      const auth = localStorage.getItem('auth');
      fetch('http://localhost:8081/api/expenses', {
        headers: { Authorization: `Basic ${auth}` }
      })
        .then(res => res.json())
        .then(data => this.expenses = data);
    },
    toggleForm() {
      this.showForm = !this.showForm;
    },
    handleAdded() {
      this.fetchExpenses();
      this.showForm = false;
    },
    toggleAll(event) {
      this.selected = event.target.checked ? this.filteredExpenses.map(e => e.id) : [];
    },
    async deleteRecord(id) {
    try {
      await axios.delete(`http://localhost:8081/api/expenses/${id}`, {
        headers: {
          Authorization: `Basic ${localStorage.getItem('auth')}`
        }
      });
      this.fetchExpenses(); // Refresh the table
    } catch (error) {
      console.error(error);
      alert('Failed to delete the expense.');
    }
  },
  
    logout() {
      localStorage.removeItem('auth');
      this.$router.push('/login');
    }
  },
  mounted() {
    this.fetchExpenses();
  }
};

// export default {
//   components: { ExpenseForm },
//   data() {
//     return {
//       expenses: []
//     };
//   },
//   mounted() {
//     this.fetchExpenses();
//   },
//   methods: {
//     fetchExpenses() {
//       const auth = localStorage.getItem('auth');
//       if (!auth) {
//         this.$router.push('/');
//         return;
//       }
//       axios.get('http://localhost:8081/api/expenses', {
//         headers: { 'Authorization': `Basic ${auth}` }
//       })
//       .then(response => {
//         this.expenses = response.data;
//       })
//       .catch(() => this.$router.push('/'));
//     },
//     deleteExpense(id) {
//       const auth = localStorage.getItem('auth');
//       axios.delete(`http://localhost:8081/api/expenses/${id}`, {
//         headers: { 'Authorization': `Basic ${auth}` }
//       })
//       .then(() => this.fetchExpenses());
//     },
//     logout() {
//       localStorage.removeItem('auth');
//       this.$router.push('/');
//     }
//   }
// };
</script>


<style scoped>
.expenses-container {
  max-width: 900px;
  margin: 40px auto;
  padding: 20px;
  background: #EEEEEE;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.expenses-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
  background: #FFFFFF;
}

.expenses-table th, .expenses-table td {
  border: 1px solid #DDD;
  padding: 10px;
  text-align: left;
}

.expenses-table th {
  background-color: #da805a;
  color: #fff;
}

.expenses-table input {
  width: 90%;
  padding: 4px;
  border: 1px solid #CCC;
  border-radius: 4px;
}



.expenses-table th {
  vertical-align: top;
  text-align: left;
  padding: 8px;
}

/* Make Description and Date columns wider */
.expenses-table th:nth-child(2),
.expenses-table td:nth-child(2) {
  width: 30%;
}

.expenses-table th:nth-child(4),
.expenses-table td:nth-child(4) {
  width: 25%;
}

.date-filter {
  display: flex;
  align-items: center;
  gap: 4px;
}

.date-filter input {
  width: 100%;
  max-width: 120px;
}


.buttons-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

button {
  background-color: #2A4759;
  color: white;
  padding: 10px 18px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s ease;
}

button:hover {
  background-color: #1c3340;
}

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

.fade-enter-active, .fade-leave-active {
  transition: all 0.3s ease;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

th input[type="date"] {
  display: block;
  width: 100%;
  margin-bottom: 4px;
}

.icon-button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.2rem;
  color: #d9534f;
}

.icon-button:hover {
  color: #c9302c;
}
</style>