<template>
  <div class="error-overlay" @click.self="dismiss">
    <div class="error-box">
      <p>{{ message }}</p>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    message: {
      type: String,
      default: 'Something went wrong.',
    },
  },
  emits: ['dismiss'],
  methods: {
    dismiss() {
      this.$emit('dismiss');
    },
  },
};
</script>

<style scoped>
.error-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  cursor: pointer;
}

.error-box {
  background: #fff;
  border: 2px solid #333;
  padding: 24px 32px;
  border-radius: 10px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
  text-align: center;
  max-width: 400px;
  font-size: 18px;
  color: #333;
  font-weight: 500;
  cursor: default;
}
</style>
