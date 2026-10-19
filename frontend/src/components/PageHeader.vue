<script setup lang="ts">
withDefaults(defineProps<{ eyebrow?: string; title: string; subtitle?: string; align?: 'left' | 'center' }>(), {
  eyebrow: '',
  subtitle: '',
  align: 'left',
})
</script>

<template>
  <header class="page-head" :class="`align-${align}`">
    <p v-if="eyebrow" class="eyebrow">{{ eyebrow }}</p>
    <h1 class="page-title">{{ title }}</h1>
    <p v-if="subtitle" class="page-sub">{{ subtitle }}</p>
    <slot />
  </header>
</template>

<style scoped>
.page-head {
  position: relative;
  padding: 108px 0 26px;
}

.page-head.align-center {
  text-align: center;
}

.eyebrow {
  margin: 0 0 10px;
  font-family: 'Times New Roman', Georgia, serif;
  font-size: 11px;
  letter-spacing: 4px;
  color: rgba(38, 92, 108, 0.72);
  text-transform: uppercase;
}

.page-title {
  margin: 0;
  font-size: clamp(30px, 3.6vw, 44px);
  line-height: 1.2;
  font-weight: 800;
  letter-spacing: 3px;
  color: #123f4d;
  text-shadow: 0 2px 18px rgba(255, 255, 255, 0.55);
}

.page-sub {
  margin: 12px 0 0;
  max-width: 620px;
  font-size: 13.5px;
  line-height: 1.9;
  color: #4d7480;
}

.align-center .page-sub {
  margin-left: auto;
  margin-right: auto;
}
</style>
