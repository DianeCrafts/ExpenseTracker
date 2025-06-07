<template>
  <div class="form-container">
    <h2>Add Expense</h2>
    <form @submit.prevent="addExpense">
      <input v-model="description" placeholder="Description" required />
      <input v-model="amount" type="number" placeholder="Amount" required />
      <input v-model="date" type="date" required />
      <button type="submit">Add</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      description: '',
      amount: 0,
      date: ''
    };
  },
  methods: {
    addExpense() {
      const auth = localStorage.getItem('auth');
      axios.post('http://localhost:8081/api/expenses', {
        description: this.description,
        amount: this.amount,
        date: this.date
      }, {
        headers: { 'Authorization': `Basic ${auth}` }
      })
      .then(() => {
        this.$emit('expense-added');
        this.description = '';
        this.amount = 0;
        this.date = '';
      })
      .catch(error => console.error(error));
    }
  }
};
</script>


<style scoped>
.form-container {
  background: #FFFFFF;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-top: 20px;
}

input {
  display: block;
  width: 100%;
  margin-bottom: 12px;
  padding: 10px;
  border: 1px solid #DDD;
  border-radius: 6px;
  font-size: 14px;
}

button {
  background-color: #2A4759;
  color: white;
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

button:hover {
  background-color: #1c3340;
}
</style>