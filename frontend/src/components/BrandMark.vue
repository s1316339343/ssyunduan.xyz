<script setup lang="ts">
withDefaults(defineProps<{ compact?: boolean; light?: boolean }>(), { compact: false, light: false })
</script>

<template>
  <span class="brand" :class="{ compact, light }">
    <span class="brand-mark" aria-hidden="true">
      <svg viewBox="0 0 48 48" fill="none">
        <defs>
          <linearGradient id="brandCloud" x1="6" y1="8" x2="42" y2="42">
            <stop stop-color="#8fe6da" />
            <stop offset="1" stop-color="#4aaed4" />
          </linearGradient>
        </defs>
        <rect x="2" y="2" width="44" height="44" rx="14" fill="url(#brandCloud)" />
        <path d="M13 31h21a6.4 6.4 0 0 0 .5-12.8A9.5 9.5 0 0 0 16.9 17 7.2 7.2 0 0 0 13 31Z" fill="#fff" opacity="0.96" />
        <circle cx="21" cy="24.4" r="1.4" fill="#2c7488" />
        <circle cx="28" cy="24.4" r="1.4" fill="#2c7488" />
        <path d="M21.5 27.5c1.8 1.5 3.9 1.5 5.4 0" stroke="#2c7488" stroke-width="1.3" stroke-linecap="round" />
      </svg>
    </span>
    <span v-if="!compact" class="brand-text">
      <span class="brand-name">云端</span><span class="brand-sub">の小窝</span>
    </span>
  </span>
</template>

<style scoped>
.brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  color: var(--ink);
  text-decoration: none;
  line-height: 1;
  white-space: nowrap;
}

.brand-mark {
  width: 34px;
  height: 34px;
  display: block;
  filter: drop-shadow(0 6px 12px rgba(32, 111, 132, 0.2));
}

.brand-mark svg {
  width: 100%;
  height: 100%;
  display: block;
}

.brand-text {
  display: inline-flex;
  align-items: baseline;
  gap: 1px;
  letter-spacing: 0.5px;
}

.brand-name {
  font-size: 19px;
  font-weight: 800;
  letter-spacing: 2px;
}

.brand-sub {
  font-size: 15px;
  font-weight: 500;
  opacity: 0.72;
}

.brand.light {
  color: #f2fffb;
}

.brand.compact .brand-mark {
  width: 32px;
  height: 32px;
}

@media (max-width: 520px) {
  .brand-name {
    font-size: 17px;
  }
  .brand-sub {
    font-size: 13px;
  }
}
</style>
