<template>
  <div class="category-manager">
    <h2>Add New Category</h2>

    <form @submit.prevent="submitCategory">
      <input
        type="text"
        v-model="category.name"
        placeholder="Enter category name"
        required
      />
      <button type="submit">Add Category</button>
    </form>

    <div v-if="message" class="message">{{ message }}</div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'CategoryManager',
  data() {
    return {
      category: {
        name: ''
      },
      message: ''
    };
  },
  methods: {
    async submitCategory() {
        try {
            const auth = localStorage.getItem('auth'); // Get auth token from localStorage

            const response = await axios.post(
            'http://localhost:8081/api/categories',
            this.category,
            {
                headers: {
                Authorization: `Basic ${auth}`,
                'Content-Type': 'application/json'
                }
            }
            );

            this.message = `Category '${response.data.name}' added successfully.`;
            this.category.name = ''; // Clear the input
            this.$emit('category-added', response.data); // Optional: notify parent
        } catch (error) {
            console.error('Error adding category:', error);
            this.message = 'Failed to add category.';
        }
    }
  }
};
</script>
<style scoped>
.category-manager {
  max-width: 900px;
  margin: 20px auto;
  background: #ffffff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

form {
  display: flex;
  gap: 10px;
}

form input {
  flex: 1;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}

button {
  background-color: #555;
  color: white;
  padding: 12px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
}

button:hover {
  background-color: #333;
}

.message {
  margin-top: 15px;
  padding: 10px;
  background-color: #f0f8ff;
  border-left: 4px solid #007bff;
  color: #333;
  border-radius: 6px;
}
</style>
