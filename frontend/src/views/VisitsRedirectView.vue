<script setup lang="ts">
import { onMounted } from 'vue'

const target = `${import.meta.env.BASE_URL}visits.html`

onMounted(() => {
  window.location.replace(target)
})
</script>

<template>
  <div class="page redirect-page">
    <div class="container narrow">
      <div class="redirect-card glass-card">
        <p class="eyebrow">VISITORS</p>
        <h1>正在打开访问统计…</h1>
        <p>如果没有自动跳转，请点击下面的按钮。</p>
        <a class="btn btn-primary" :href="target">打开访问统计</a>
      </div>
    </div>
  </div>
</template>

<style scoped>
.redirect-page {
  min-height: 70vh;
  display: grid;
  place-items: center;
  padding: 40px 0;
}

.redirect-card {
  max-width: 460px;
  margin: 0 auto;
  padding: 32px;
  text-align: center;
}

.redirect-card h1 {
  margin: 8px 0 10px;
  font-size: 24px;
  color: #164452;
}

.redirect-card p:not(.eyebrow) {
  margin: 0 0 18px;
  font-size: 12px;
  line-height: 1.8;
  color: #6f8a93;
}
</style>
