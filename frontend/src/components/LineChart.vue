<script setup lang="ts">
import { computed, ref } from 'vue'
import type { VisitPoint } from '../types'
import { formatMonthDay } from '../utils/format'

const props = withDefaults(defineProps<{ points: VisitPoint[]; height?: number }>(), { height: 260 })

const W = 820
const H = 270
const PAD = { left: 44, right: 18, top: 20, bottom: 34 }
const hover = ref(-1)
const wrap = ref<HTMLElement | null>(null)

const max = computed(() => {
  const m = Math.max(10, ...props.points.map((p) => Math.max(p.pv, p.uv)))
  return Math.ceil(m / 10) * 10
})

const pts = computed(() => {
  const n = Math.max(1, props.points.length - 1)
  return props.points.map((point, i) => ({
    x: PAD.left + (i / n) * (W - PAD.left - PAD.right),
    pv: H - PAD.bottom - (point.pv / max.value) * (H - PAD.top - PAD.bottom),
    uv: H - PAD.bottom - (point.uv / max.value) * (H - PAD.top - PAD.bottom),
    data: point,
  }))
})

function smooth(values: { x: number; y: number }[]) {
  if (!values.length) return ''
  if (values.length === 1) return `M ${values[0].x} ${values[0].y}`
  let d = `M ${values[0].x} ${values[0].y}`
  for (let i = 0; i < values.length - 1; i++) {
    const p0 = values[i - 1] || values[i]
    const p1 = values[i]
    const p2 = values[i + 1]
    const p3 = values[i + 2] || p2
    const c1x = p1.x + (p2.x - p0.x) / 6
    const c1y = p1.y + (p2.y - p0.y) / 6
    const c2x = p2.x - (p3.x - p1.x) / 6
    const c2y = p2.y - (p3.y - p1.y) / 6
    d += ` C ${c1x} ${c1y}, ${c2x} ${c2y}, ${p2.x} ${p2.y}`
  }
  return d
}

const pvLine = computed(() => smooth(pts.value.map((p) => ({ x: p.x, y: p.pv }))))
const uvLine = computed(() => smooth(pts.value.map((p) => ({ x: p.x, y: p.uv }))))
const pvArea = computed(() => `${pvLine.value} L ${pts.value[pts.value.length - 1]?.x ?? 0} ${H - PAD.bottom} L ${pts.value[0]?.x ?? 0} ${H - PAD.bottom} Z`)

const grid = computed(() => [0, 0.25, 0.5, 0.75, 1].map((ratio) => ({
  y: PAD.top + ratio * (H - PAD.top - PAD.bottom),
  label: Math.round(max.value * (1 - ratio)),
})))

const labelStep = computed(() => Math.max(1, Math.ceil(props.points.length / 10)))

function onMove(event: MouseEvent) {
  if (!wrap.value || !pts.value.length) return
  const rect = wrap.value.getBoundingClientRect()
  const x = ((event.clientX - rect.left) / rect.width) * W
  let nearest = 0
  let distance = Infinity
  pts.value.forEach((p, i) => {
    const d = Math.abs(p.x - x)
    if (d < distance) {
      distance = d
      nearest = i
    }
  })
  hover.value = nearest
}

const active = computed(() => (hover.value >= 0 ? pts.value[hover.value] : null))
const tooltipStyle = computed(() => {
  if (!active.value) return {}
  const left = (active.value.x / W) * 100
  return { left: `${Math.min(86, Math.max(8, left))}%` }
})
</script>

<template>
  <div ref="wrap" class="line-chart" :style="{ height: `${height}px` }" @mousemove="onMove" @mouseleave="hover = -1">
    <svg :viewBox="`0 0 ${W} ${H}`" preserveAspectRatio="none" role="img" aria-label="近 30 天访问趋势折线图">
      <defs>
        <linearGradient id="lineArea" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0" stop-color="#ff8fa3" stop-opacity="0.35" />
          <stop offset="1" stop-color="#ff8fa3" stop-opacity="0" />
        </linearGradient>
        <linearGradient id="pvStroke" x1="0" y1="0" x2="1" y2="0">
          <stop offset="0" stop-color="#ef7f8f" />
          <stop offset="1" stop-color="#e2556c" />
        </linearGradient>
      </defs>

      <g>
        <line
          v-for="(g, i) in grid"
          :key="i"
          :x1="PAD.left"
          :x2="W - PAD.right"
          :y1="g.y"
          :y2="g.y"
          stroke="rgba(41, 97, 113, 0.09)"
          stroke-dasharray="4 6"
        />
        <text
          v-for="(g, i) in grid"
          :key="`t${i}`"
          :x="PAD.left - 12"
          :y="g.y + 4"
          text-anchor="end"
          font-size="10"
          fill="#93aab1"
        >
          {{ g.label }}
        </text>
      </g>

      <path :d="pvArea" fill="url(#lineArea)" />
      <path :d="uvLine" fill="none" stroke="#e9b46a" stroke-width="2" opacity="0.9" />
      <path :d="pvLine" fill="none" stroke="url(#pvStroke)" stroke-width="2.6" stroke-linecap="round" />

      <g v-if="active">
        <line :x1="active.x" :x2="active.x" :y1="PAD.top" :y2="H - PAD.bottom" stroke="rgba(34, 91, 108, 0.2)" stroke-dasharray="3 4" />
        <circle :cx="active.x" :cy="active.pv" r="5" fill="#fff" stroke="#e2556c" stroke-width="3" />
        <circle :cx="active.x" :cy="active.uv" r="4" fill="#fff" stroke="#e9b46a" stroke-width="2.5" />
      </g>

      <g v-else-if="pts.length">
        <circle :cx="pts[pts.length - 1].x" :cy="pts[pts.length - 1].pv" r="4.5" fill="#fff" stroke="#e2556c" stroke-width="2.5" />
      </g>

      <g>
        <text
          v-for="(p, i) in pts"
          v-show="i % labelStep === 0 || i === pts.length - 1"
          :key="`x${i}`"
          :x="p.x"
          :y="H - 12"
          text-anchor="middle"
          font-size="9.5"
          fill="#93aab1"
        >
          {{ formatMonthDay(p.data.date) }}
        </text>
      </g>
    </svg>

    <div v-if="active" class="chart-tip" :style="tooltipStyle">
      <p class="tip-date">{{ formatMonthDay(active.data.date) }}</p>
      <p><span class="dot pv"></span>浏览量 <b>{{ active.data.pv }}</b></p>
      <p><span class="dot uv"></span>访客数 <b>{{ active.data.uv }}</b></p>
    </div>

    <div class="chart-legend">
      <span><i class="pv"></i>浏览量</span>
      <span><i class="uv"></i>访客数</span>
    </div>
  </div>
</template>

<style scoped>
.line-chart {
  position: relative;
  width: 100%;
  min-height: 200px;
}

.line-chart svg {
  width: 100%;
  height: 100%;
  display: block;
  overflow: visible;
}

.chart-tip {
  position: absolute;
  top: 12px;
  transform: translateX(-50%);
  min-width: 112px;
  padding: 9px 11px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 10px 26px rgba(23, 82, 103, 0.16);
  pointer-events: none;
  z-index: 2;
}

.chart-tip p {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 10.5px;
  color: #6d8b95;
  line-height: 1.7;
}

.chart-tip b {
  margin-left: auto;
  color: #245463;
}

.tip-date {
  font-weight: 700;
  color: #2d6172 !important;
  padding-bottom: 3px;
  border-bottom: 1px dashed rgba(46, 109, 128, 0.16);
  margin-bottom: 3px !important;
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  display: inline-block;
}
.dot.pv {
  background: #e2556c;
}
.dot.uv {
  background: #e9b46a;
}

.chart-legend {
  position: absolute;
  top: -2px;
  right: 4px;
  display: flex;
  gap: 14px;
  font-size: 10px;
  color: #7b969f;
}

.chart-legend span {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.chart-legend i {
  width: 14px;
  height: 3px;
  border-radius: 99px;
  display: inline-block;
}
.chart-legend i.pv {
  background: #e2556c;
}
.chart-legend i.uv {
  background: #e9b46a;
}
</style>
