<template>
  <div class="form-container">
    <h2>{{ expense ? 'Edit Expense' : 'Add Expense' }}</h2>
    <form @submit.prevent="handleSubmit">
      <input v-model="description" placeholder="Description" required />
      <input v-model="amount" type="number" placeholder="Amount" required />
      <input v-model="date" type="date" required />
      <button type="submit">{{ expense ? 'Update' : 'Add' }}</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  props: {
    expense: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      description: '',
      amount: 0,
      date: ''
    };
  },
  watch: {
    expense: {
      immediate: true,
      handler(newVal) {
        if (newVal) {
          this.description = newVal.description;
          this.amount = newVal.amount;
          this.date = newVal.date;
        } else {
          this.description = '';
          this.amount = 0;
          this.date = '';
        }
      }
    }
  },
  methods: {
    handleSubmit() {
      const auth = localStorage.getItem('auth');
      const payload = {
        description: this.description,
        amount: this.amount,
        date: this.date
      };

      if (this.expense) {
        // Update existing
        axios.put(`http://localhost:8081/api/expenses/${this.expense.id}`, payload, {
          headers: { 'Authorization': `Basic ${auth}` }
        })
        .then(() => {
          this.$emit('expense-updated');
        })
        .catch(error => console.error(error));
      } else {
        // Add new
        axios.post('http://localhost:8081/api/expenses', payload, {
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
  background-color: #555;
  color: white;
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

button:hover {
  background-color: #333;
}
</style>
