<template>
  <div class="page-container">
    <div class="main-container">
      <h2 class="title">Login</h2>
      <form @submit.prevent="login" class="login-form">
        <input v-model="username" type="text" placeholder="Username" required />
        <input v-model="password" type="password" placeholder="Password" required />
        <button type="submit">Login</button>
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios';


// const instance = axios.create({
//   baseURL: 'http://localhost:8081',
// });

// instance.interceptors.request.use((config) => {
//   const token = localStorage.getItem('auth');
//   if (token) {
//     config.headers['Authorization'] = `Basic ${token}`;
//   }
//   return config;
// });


export default {
  data() {
    return {
      username: '',
      password: ''
    };
  },
  methods: {
    async login() {
      const credentials = btoa(`${this.username}:${this.password}`);
      try {
        const response = await axios.get('http://localhost:8081/api/expenses', {
          headers: {
            Authorization: `Basic ${credentials}`
          }
        });
        console.log(response)

        localStorage.setItem('auth', credentials);
        this.$router.push('/expenses');
        
      } catch (error) {
        alert('Login failed: invalid username or password');
      }
    }
  }
};
</script>


<style>
.page-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #EEEEEE;
  font-family: 'Segoe UI', sans-serif;
}

.main-container {
  width: 400px;
  padding: 30px 25px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #DDDDDD;
}

.title {
  margin-bottom: 20px;
  color: #2A4759;
  text-align: center;
  font-size: 24px;
  font-weight: 600;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.login-form input {
  padding: 12px;
  border: 1px solid #DDDDDD;
  border-radius: 6px;
  font-size: 16px;
  background-color: #FAFAFA;
}

.login-form input:focus {
  outline: none;
  border-color: #F79B72;
  background-color: #FFFFFF;
}

.login-form button {
  padding: 12px;
  background-color: #F79B72;
  border: none;
  border-radius: 6px;
  color: white;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.login-form button:hover {
  background-color: #e28761;
}
</style> 