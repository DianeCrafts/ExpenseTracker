<!-- components/ExpenseTable.vue -->
<template>
  <div>
    <!-- Header Row with Filters -->
  <div class="grid-row header">
    <div class="header-cell">
      <div class="label">Description</div>
      <input type="text" v-model="filters.description" placeholder="Filter" />
    </div>

    <div class="header-cell">
      <div class="label">Amount</div>
      <!-- Min and Max inputs for amount range -->
      <input type="number" v-model.number="filters.minAmount" placeholder="Min" />
      <input type="number" v-model.number="filters.maxAmount" placeholder="Max" />
    </div>

    <div class="header-cell">
      <div class="label">Date</div>
      <div class="date-filter">
        <input type="date" v-model="filters.startDate" />
        <span>to</span>
        <input type="date" v-model="filters.endDate" />
      </div>
    </div>

    <div class="header-cell">
      <div class="label">Category</div>
      <input type="text" v-model="filters.category" placeholder="Filter" />
    </div>

    <div class="header-cell actions-column" v-if="currentTab == 'expenses'">
      <div class="label">Actions</div>
    </div>
  </div>

  <!-- Data Rows -->
  <div class="grid-row" v-for="expense in expenses" :key="expense.id">
    <div>{{ expense.description }}</div>
    <div>{{ expense.amount }}</div>
    <div>{{ expense.date }}</div>
    <div>{{ expense.category?.name || '—' }}</div> <!-- Display category name or dash if null -->
    <div class="actions-column" v-if="currentTab == 'expenses'">
      <button @click="$emit('delete', expense.id)" class="icon-action" title="Delete">
        <i class="fas fa-trash-alt"></i>
      </button>
      <button @click="$emit('edit', expense.id)" class="icon-action" title="Edit">
        <i class="fas fa-edit"></i>
      </button>
    </div>
  </div>


    <!-- Pagination -->
    <div class="pagination-wrapper">
      <div class="pagination-buttons">
        <button @click="$emit('change-page', currentPage - 1)" :disabled="currentPage === 0">&laquo;</button>

        <button
          v-for="page in visiblePages"
          :key="page"
          :class="{ active: page === currentPage + 1, ellipsis: page === '...'}"
          @click="page !== '...' && $emit('change-page', page - 1)"
          :disabled="page === '...'"
        >
          {{ page }}
        </button>

        <button @click="$emit('change-page', currentPage + 1)" :disabled="currentPage >= totalPages - 1">&raquo;</button>
      </div>

      <div class="page-size-selector">
        <label for="pageSize">Rows per page:</label>
        <select id="pageSize" v-model.number="localPageSize" @change="$emit('change-page-size', localPageSize)">
          <option :value="2">2</option>
          <option :value="10">10</option>
          <option :value="20">20</option>
          <option :value="50">50</option>
        </select>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ExpenseTable',
  props: {
    expenses: Array,
    currentPage: Number,
    totalPages: Number,
    pageSize: Number,
    currentTab: String,
    
  },
  data() {
    return {     
      filters: {
        description: '',
        minAmount: '',
        maxAmount: '',
        startDate: '',
        endDate: '',
        category: ''
      },
      localPageSize: this.pageSize,
    };
  },
  computed: {
    visiblePages() {
      const total = this.totalPages;
      const current = this.currentPage + 1;
      const visibleLimit = 2;
      const pages = [];

      if (total <= visibleLimit + 2) {
        return Array.from({ length: total }, (_, i) => i + 1);
      }

      pages.push(1);

      if (current > 3) pages.push('...');
      const start = Math.max(2, current - 1);
      const end = Math.min(total - 1, current + 1);
      for (let i = start; i <= end; i++) pages.push(i);
      if (current < total - 2) pages.push('...');
      pages.push(total);

      return pages;
    }
  },
  methods: {
    emitFilterChange() {
      this.$emit('filter-change', { ...this.filters });
    }
  },
  watch: {
    filters: {
      handler: 'emitFilterChange',
      deep: true
    },
    localPageSize(newVal) {
      this.$emit('change-page-size', newVal);
    }
  }
};
</script>
<style scoped>
.grid-row {
  display: grid;
  grid-template-columns: 3fr 1fr 3fr 1fr 80px;
  align-items: center;
  border-bottom: 1px solid #ddd;
  background: #fff;
  text-align: center;
}

.grid-row > div {
  /* border-left: 1px solid rgba(0, 0, 0, 0.1); */
  padding: 10px;
}

.grid-row > div:first-child {
  border-left: none;
}

.header {
  background-color: #da805a;
  color: white;
  display: grid;
  border-bottom: 1px solid #e5e5e5;
  gap: 1px;
}

.header-cell {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: stretch;
  padding: 10px;
  font-family: inherit;
  background-color: #da805a;
  border-right: 1px solid rgba(255, 255, 255, 0.3);
}

.header-cell:last-child {
  border-right: none;
}

.header-cell .label {
  font-weight: 600;
  font-size: 0.9rem;
  text-align: center;
  margin-bottom: 4px;
}

.header-cell input[type='text'],
.header-cell input[type='date'],
.date-filter input[type='date'] {
  width: 100%;
  box-sizing: border-box;
  padding: 6px 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-family: inherit;
  font-size: 0.9rem;
}

.date-filter {
  display: flex;
  gap: 4px;
  justify-content: center;
  flex-wrap: wrap;
}

.date-filter input {
  max-width: 120px;
  padding: 4px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.date-filter input[type="date"] {
  flex: 1 1 45%;
  min-width: 80px;
  max-width: 100px;
}

input[type='text'],
input[type='date'] {
  width: 100%;
  padding: 6px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.actions-column {
  display: flex;
  justify-content: center;
  align-items: center;
}

.icon-action {
  background: none;
  border: none;
  cursor: pointer;
  color: #d9534f;
  font-size: 1.1rem;
  padding: 10px;
  line-height: 1;
}

.icon-button:hover {
  color: #c9302c;
}

.pagination-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 30px;
  flex-wrap: wrap;
  border-top: 1px solid #ccc;
  padding-top: 16px;
  gap: 12px;
  position: relative;
}

.pagination-buttons {
  display: flex;
  justify-content: center;
  gap: 6px;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.pagination-buttons button {
  padding: 6px 12px;
  border: 1px solid #ccc;
  background: white;
  color: #333;
  font-weight: 500;
  cursor: pointer;
  border-radius: 6px;
  transition: 0.2s ease;
  min-width: 34px;
}

.pagination-buttons button:hover:not(:disabled):not(.ellipsis) {
  background-color: #f2f2f2;
}

.pagination-buttons button.active {
  background-color: #da805a;
  border-color: #da805a;
  color: white;
  font-weight: bold;
}

.pagination-buttons button.ellipsis {
  background: none;
  border: none;
  color: #999;
  font-size: 20px;
  font-weight: bold;
  padding: 4px 10px;
  pointer-events: none;
  cursor: default;
}

.pagination-buttons button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.page-size-selector {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: #f9f9f9;
  padding: 6px 10px;
  border-radius: 6px;
  font-size: 0.9rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.page-size-selector select {
  padding: 4px 8px;
  border-radius: 4px;
  border: 1px solid #ccc;
  background: white;
}


/* Form transition */
.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
