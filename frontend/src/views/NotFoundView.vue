<script setup lang="ts">
import { ArrowLeft, Compass, Home } from 'lucide-vue-next'
import PondScene from '../components/PondScene.vue'
</script>

<template>
  <div class="not-found">
    <PondScene />
    <div class="nf-inner">
      <p class="nf-code">4 0 4</p>
      <p class="nf-eyebrow">LOST IN THE CLOUDS</p>
      <h1>这页云游去了</h1>
      <p class="nf-text">你要找的页面可能被风吹散，或者从来没有存在过。<br />不如回到小窝，喝杯茶再找找。</p>
      <div class="nf-actions">
        <RouterLink to="/" class="btn btn-primary"><Home :size="14" /> 回到首页</RouterLink>
        <RouterLink to="/blog" class="btn btn-ghost"><ArrowLeft :size="14" /> 去博客看看</RouterLink>
      </div>
      <Compass :size="18" class="nf-compass" />
    </div>
    <svg class="nf-wave" viewBox="0 0 1600 120" preserveAspectRatio="none" aria-hidden="true">
      <path d="M0 66 C150 18 300 70 470 80 C650 90 760 34 940 44 C1120 54 1280 102 1430 88 C1500 82 1550 64 1600 52 L1600 120 L0 120Z" fill="#f7fdff" />
    </svg>
  </div>
</template>

<style scoped>
.not-found {
  position: relative;
  min-height: 100vh;
  min-height: 100svh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  color: #f4fffb;
}

.nf-inner {
  position: relative;
  z-index: 2;
  width: min(560px, calc(100% - 40px));
  padding: 100px 0 130px;
  text-align: center;
}

.nf-code {
  margin: 0;
  font-family: 'Times New Roman', Georgia, serif;
  font-size: clamp(70px, 12vw, 120px);
  line-height: 1;
  letter-spacing: 12px;
  color: rgba(232, 255, 248, 0.2);
}

.nf-eyebrow {
  margin: 14px 0 8px;
  font-family: 'Times New Roman', serif;
  font-size: 10px;
  letter-spacing: 5px;
  color: rgba(222, 255, 244, 0.55);
}

.nf-inner h1 {
  margin: 0 0 10px;
  font-size: clamp(28px, 4vw, 40px);
  letter-spacing: 3px;
}

.nf-text {
  margin: 0 0 24px;
  font-size: 12px;
  line-height: 2;
  color: rgba(225, 252, 244, 0.7);
}

.nf-actions {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 9px;
}

.nf-actions :deep(.btn-primary) {
  background: rgba(255, 255, 255, 0.92);
  color: #17627a;
  box-shadow: 0 12px 26px rgba(0, 0, 0, 0.16);
}

.nf-actions :deep(.btn-ghost) {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.3);
  color: #e9fff8;
}

.nf-compass {
  margin-top: 28px;
  color: rgba(219, 255, 243, 0.45);
  animation: compassSpin 9s linear infinite;
}

.nf-wave {
  position: absolute;
  left: 0;
  right: 0;
  bottom: -1px;
  z-index: 3;
  width: 100%;
  height: 92px;
}

@keyframes compassSpin {
  to {
    transform: rotate(360deg);
  }
}
</style>
