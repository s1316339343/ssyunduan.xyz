<script setup lang="ts">
import { computed, ref } from 'vue'
import type { MonthPoint } from '../types'

const props = defineProps<{ points: MonthPoint[] }>()

const W = 820
const H = 250
const PAD = { left: 40, right: 18, top: 26, bottom: 34 }
const hover = ref(-1)

const max = computed(() => Math.max(4, ...props.points.map((p) => p.count)) + 1)

const bars = computed(() => {
  const count = Math.max(1, props.points.length)
  const slot = (W - PAD.left - PAD.right) / count
  const width = Math.min(46, slot * 0.5)
  return props.points.map((point, i) => {
    const h = (point.count / max.value) * (H - PAD.top - PAD.bottom)
    return {
      x: PAD.left + slot * i + (slot - width) / 2,
      y: H - PAD.bottom - h,
      w: width,
      h,
      data: point,
    }
  })
})

const ticks = computed(() =>
  [0, 0.25, 0.5, 0.75, 1].map((r) => ({
    y: PAD.top + r * (H - PAD.top - PAD.bottom),
    label: Math.round(max.value * (1 - r)),
  })),
)
</script>

<template>
  <div class="bar-chart">
    <svg :viewBox="`0 0 ${W} ${H}`" preserveAspectRatio="none" role="img" aria-label="按月文章发布数量柱状图">
      <defs>
        <linearGradient id="barGrad" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0" stop-color="#6fd3e8" />
          <stop offset="1" stop-color="#2aa7c9" />
        </linearGradient>
      </defs>

      <g>
        <line
          v-for="(t, i) in ticks"
          :key="i"
          :x1="PAD.left"
          :x2="W - PAD.right"
          :y1="t.y"
          :y2="t.y"
          stroke="rgba(41, 97, 113, 0.09)"
          stroke-dasharray="4 6"
        />
        <text v-for="(t, i) in ticks" :key="`l${i}`" :x="PAD.left - 10" :y="t.y + 4" text-anchor="end" font-size="10" fill="#93aab1">
          {{ t.label }}
        </text>
      </g>

      <g v-for="(bar, i) in bars" :key="i" @mouseenter="hover = i" @mouseleave="hover = -1">
        <rect
          :x="bar.x - 6"
          :y="PAD.top"
          :width="bar.w + 12"
          :height="H - PAD.top - PAD.bottom"
          fill="transparent"
        />
        <rect
          :x="bar.x"
          :y="bar.y"
          :width="bar.w"
          :height="bar.h"
          rx="6"
          fill="url(#barGrad)"
          :opacity="hover === -1 || hover === i ? 1 : 0.45"
          class="bar"
        />
        <text
          v-if="bar.data.count > 0"
          :x="bar.x + bar.w / 2"
          :y="bar.y - 8"
          text-anchor="middle"
          font-size="10.5"
          font-weight="700"
          fill="#2f7186"
        >
          {{ bar.data.count }}
        </text>
        <text :x="bar.x + bar.w / 2" :y="H - 12" text-anchor="middle" font-size="9.5" fill="#93aab1">
          {{ bar.data.month }}
        </text>
        <g v-if="hover === i">
          <title>{{ bar.data.month }}：{{ bar.data.count }} 篇</title>
        </g>
      </g>
    </svg>
  </div>
</template>

<style scoped>
.bar-chart {
  width: 100%;
  height: 250px;
}

.bar-chart svg {
  width: 100%;
  height: 100%;
  display: block;
  overflow: visible;
}

.bar {
  transition: opacity 0.2s ease;
}
</style>
