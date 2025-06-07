<template>
  <div class="form-container">
    <h2>{{ expense ? 'Edit Expense' : 'Add Expense' }}</h2>
    <form @submit.prevent="handleSubmit" class="form">
      <input v-model="description" placeholder="Description" required />
      <input v-model.number="amount" type="number" placeholder="Amount" required />
      <input v-model="date" type="date" required />

      <select v-model="categoryId" required>
        <option disabled value="">Select Category</option>
        <option v-for="cat in categories" :key="cat.id" :value="cat.id">
          {{ cat.name }}
        </option>
      </select>

      <div class="form-footer">
        <button type="submit">{{ expense ? 'Update' : 'Add' }}</button>
      </div>
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
      date: '',
      categoryId: '',
      categories: []
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
          this.categoryId = newVal.category?.id || '';
        } else {
          this.description = '';
          this.amount = 0;
          this.date = '';
          this.categoryId = '';
        }
      }
    }
  },
  mounted() {
    this.fetchCategories();
  },
  methods: {
    async fetchCategories() {
      const auth = localStorage.getItem('auth');
      try {
        const response = await axios.get('http://localhost:8081/api/categories', {
          headers: { Authorization: `Basic ${auth}` }
        });
        this.categories = response.data;
      } catch (error) {
        console.error('Failed to load categories:', error);
        const errorData = error.response?.data;
        const message = errorData ? Object.values(errorData)[0] : 'Could not load categories.';
        this.$emit('error', message);
      }
    },
    async handleSubmit() {
      const auth = localStorage.getItem('auth');
      const payload = {
        description: this.description,
        amount: this.amount,
        date: this.date,
        category: { id: this.categoryId }
      };
      console.log(payload);
      try {
        
        if (this.expense) {
          await axios.put(`http://localhost:8081/api/expenses/${this.expense.id}`, payload, {
            headers: { Authorization: `Basic ${auth}` }
          });
          this.$emit('expense-updated');
        } else {
          await axios.post('http://localhost:8081/api/expenses', payload, {
            headers: { Authorization: `Basic ${auth}` }
          });
          this.$emit('expense-added');
          this.description = '';
          this.amount = 0;
          this.date = '';
          this.categoryId = '';
        }
      } catch (error) {
          const errorData = error.response?.data;
          const message = errorData ? Object.values(errorData)[0] : 'Failed to submit expense. Please try again.';
          this.$emit('error', message);
        }

    }
  }
};
</script>

<style scoped>
.form-container {
  max-width: 900px;
  margin: 20px auto;
  background: #ffffff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.form input,
.form select {
  display: block;
  width: 100%;
  margin-bottom: 12px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}

.form select {
  background-color: #fff;
  color: #333;
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

button {
  background-color: #555;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

button:hover {
  background-color: #333;
}
</style>

