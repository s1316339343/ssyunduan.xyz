<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import {
  Activity,
  CalendarDays,
  Eye,
  FileText,
  FolderOpen,
  Hash,
  LineChart,
  Radio,
  TrendingUp,
  Users,
} from 'lucide-vue-next'
import PageHeader from '../components/PageHeader.vue'
import LineChartView from '../components/LineChart.vue'
import BarChartView from '../components/BarChart.vue'
import { api } from '../api/client'
import type { Stats } from '../types'
import { formatNumber } from '../utils/format'

const stats = ref<Stats | null>(null)
const loading = ref(true)
const error = ref('')

const today = computed(() => {
  const d = new Date()
  return `${d.getFullYear()} 年 ${d.getMonth() + 1} 月 ${d.getDate()} 日`
})

const siteTiles = computed(() => [
  { label: '文章总数', value: stats.value?.articles ?? 0, icon: FileText, tone: 'mint', suffix: '篇' },
  { label: '标签数量', value: stats.value?.tags ?? 0, icon: Hash, tone: 'blue', suffix: '个' },
  { label: '分类数量', value: stats.value?.categories ?? 0, icon: FolderOpen, tone: 'butter', suffix: '类' },
  { label: '累计字数', value: stats.value?.words ?? 0, icon: LineChart, tone: 'violet', suffix: '字', format: true },
])

const visitTiles = computed(() => [
  { label: '今日访客', value: stats.value?.todayVisits ?? 0, icon: Users, tone: 'pink' },
  { label: '本月访客', value: stats.value?.monthVisits ?? 0, icon: Users, tone: 'blue' },
  { label: '总访客数', value: stats.value?.totalVisits ?? 0, icon: Users, tone: 'mint' },
  { label: '今日浏览', value: stats.value?.todayViews ?? 0, icon: Eye, tone: 'butter' },
  { label: '本月浏览', value: stats.value?.monthViews ?? 0, icon: Eye, tone: 'violet' },
  { label: '总浏览量', value: stats.value?.totalViews ?? 0, icon: Activity, tone: 'pink' },
])

onMounted(async () => {
  try {
    stats.value = await api.stats()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '数据加载失败'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page stats-page">
    <div class="container">
      <PageHeader
        eyebrow="SITE ANALYTICS"
        title="数据统计"
        subtitle="网站访问与内容数据，看看这片小窝被多少个温柔的灵魂路过。"
      />

      <div v-if="error" class="error-box">{{ error }}</div>

      <div v-if="loading" class="loading-block stats-loading">
        正在计算云朵的重量<span class="loading-dots"><i></i><i></i><i></i></span>
      </div>

      <template v-else-if="stats">
        <section class="stats-panel glass-card">
          <header class="panel-head">
            <div>
              <p class="eyebrow">OVERVIEW</p>
              <h2>小窝概览</h2>
              <p>截至 {{ today }} 的站点数据</p>
            </div>
            <span class="live-pill"><i></i> 实时数据</span>
          </header>

          <div class="block-label">
            <span>站点统计</span>
            <small>文章 / 标签 / 分类</small>
          </div>
          <div class="stat-grid four">
            <div v-for="tile in siteTiles" :key="tile.label" class="stat-tile">
              <span class="tile-icon" :class="tile.tone"><component :is="tile.icon" :size="17" /></span>
              <div>
                <strong>{{ tile.format ? formatNumber(tile.value) : tile.value }}</strong>
                <span class="tile-label">{{ tile.label }}<em v-if="tile.suffix"> · {{ tile.suffix }}</em></span>
              </div>
            </div>
          </div>

          <div class="block-label visit-label">
            <span>访问统计</span>
            <small>访客按独立 IP 计算</small>
          </div>
          <div class="stat-grid three">
            <div v-for="tile in visitTiles" :key="tile.label" class="stat-tile">
              <span class="tile-icon" :class="tile.tone"><component :is="tile.icon" :size="17" /></span>
              <div>
                <strong>{{ formatNumber(tile.value) }}</strong>
                <span class="tile-label">{{ tile.label }}</span>
              </div>
            </div>
          </div>

          <div class="chart-block">
            <div class="chart-head">
              <div>
                <h3><TrendingUp :size="15" /> 访问趋势</h3>
                <p>近 30 天浏览量 / 访客数</p>
              </div>
              <span class="chart-total">
                30 天累计 <b>{{ formatNumber(stats.visitTrend.reduce((sum, p) => sum + p.pv, 0)) }}</b> 次浏览
              </span>
            </div>
            <div class="chart-scroll">
              <LineChartView :points="stats.visitTrend" :height="280" />
            </div>
            <p class="chart-scroll-hint">← 左右滑动查看完整图表 →</p>
          </div>

          <div class="chart-block bar-block">
            <div class="chart-head">
              <div>
                <h3><CalendarDays :size="15" /> 文章发布</h3>
                <p>按月统计 · 最近 10 个月</p>
              </div>
              <span class="chart-total">
                共 <b>{{ stats.articles }}</b> 篇文章
              </span>
            </div>
            <div class="chart-scroll">
              <BarChartView :points="stats.monthlyArticles" />
            </div>
            <p class="chart-scroll-hint">← 左右滑动查看完整图表 →</p>
          </div>

          <footer class="panel-foot">
            <span><Radio :size="12" /> 数据每次访问后更新</span>
            <span>统计不追踪个人隐私，仅记录匿名访问量。</span>
          </footer>
        </section>

        <section class="stats-notes">
          <div class="note-card glass-card">
            <span class="note-icon mint"><FileText :size="16" /></span>
            <h3>关于写作</h3>
            <p>平均每篇约 {{ Math.round(stats.words / Math.max(stats.articles, 1)) }} 字。不追求日更，只写真正想留下的东西。</p>
          </div>
          <div class="note-card glass-card">
            <span class="note-icon blue"><Users :size="16" /></span>
            <h3>关于来访</h3>
            <p>每一个数字背后，都是一个真实的人。谢谢你愿意在这里停留几分钟。</p>
          </div>
          <div class="note-card glass-card">
            <span class="note-icon butter"><Activity :size="16" /></span>
            <h3>关于数据</h3>
            <p>统计接口由 Spring Boot 提供，数据存放在本地 H2 数据库，简单、透明、可迁移。</p>
          </div>
        </section>
      </template>
    </div>
  </div>
</template>

<style scoped>
.stats-page {
  padding-bottom: 56px;
}

.stats-loading {
  margin-top: 10px;
}

.stats-panel {
  padding: 26px 28px 22px;
}

.panel-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
  padding-bottom: 18px;
  border-bottom: 1px dashed rgba(48, 112, 131, 0.16);
}

.panel-head h2 {
  margin: 4px 0 3px;
  font-size: 24px;
  letter-spacing: 1px;
  color: #123f4d;
}

.panel-head p:last-child {
  margin: 0;
  font-size: 11px;
  color: #86a0a8;
}

.live-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  flex: none;
  padding: 5px 11px;
  border-radius: 999px;
  background: #eefaf4;
  border: 1px solid #d8f1e5;
  color: #3c9b77;
  font-size: 10px;
  font-weight: 650;
}

.live-pill i {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #45bd8d;
  box-shadow: 0 0 0 3px rgba(69, 189, 141, 0.16);
  animation: livePulse 2s ease-in-out infinite;
}

.block-label {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin: 22px 0 12px;
}

.block-label span {
  position: relative;
  padding-left: 12px;
  font-size: 14px;
  font-weight: 750;
  color: #225462;
}

.block-label span::before {
  content: '';
  position: absolute;
  left: 0;
  top: 4px;
  bottom: 4px;
  width: 4px;
  border-radius: 99px;
  background: linear-gradient(#68d0dd, #2fa8c9);
}

.block-label small {
  font-size: 9.5px;
  letter-spacing: 0.5px;
  color: #9aadb3;
}

.visit-label {
  margin-top: 26px;
}

.stat-grid {
  display: grid;
  gap: 11px;
}

.stat-grid.four {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.stat-grid.three {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.stat-tile {
  display: flex;
  align-items: center;
  gap: 11px;
  padding: 13px 15px;
  border-radius: 13px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 6px 16px rgba(32, 102, 124, 0.05);
  transition: transform 0.2s ease;
}

.stat-tile:hover {
  transform: translateY(-2px);
}

.tile-icon {
  width: 38px;
  height: 38px;
  flex: none;
  display: grid;
  place-items: center;
  border-radius: 11px;
}

.tile-icon.mint {
  background: #e2f8f1;
  color: #35a184;
}
.tile-icon.blue {
  background: #e5f4fb;
  color: #3a93b7;
}
.tile-icon.butter {
  background: #fff5dc;
  color: #bd8d35;
}
.tile-icon.violet {
  background: #f0edfb;
  color: #8874c0;
}
.tile-icon.pink {
  background: #ffeaf0;
  color: #d6758c;
}

.stat-tile strong {
  display: block;
  font-size: 23px;
  line-height: 1.1;
  color: #1b5262;
  font-weight: 780;
  letter-spacing: 0.5px;
}

.tile-label {
  display: block;
  margin-top: 2px;
  font-size: 9.5px;
  color: #8ba2aa;
}

.tile-label em {
  font-style: normal;
  opacity: 0.6;
}

.chart-block {
  margin-top: 24px;
  padding: 18px 18px 8px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.52);
  border: 1px solid rgba(255, 255, 255, 0.82);
}

.chart-scroll {
  width: 100%;
  max-width: 100%;
  overflow-x: auto;
  overflow-y: hidden;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: thin;
}

.chart-scroll-hint {
  display: none;
  margin: 2px 0 0;
  text-align: center;
  font-size: 9.5px;
  letter-spacing: 0.5px;
  color: #8ba2aa;
}

.chart-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 0 4px 8px;
}

.chart-head h3 {
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 0 0 3px;
  font-size: 14px;
  color: #245666;
}

.chart-head p {
  margin: 0;
  font-size: 9.5px;
  color: #93a8af;
}

.chart-total {
  font-size: 9.5px;
  color: #8ba2aa;
  flex: none;
}

.chart-total b {
  color: #2a7e94;
  font-size: 11px;
}

.bar-block {
  margin-top: 14px;
}

.panel-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px dashed rgba(48, 112, 131, 0.14);
  font-size: 9px;
  color: #9aadb3;
}

.panel-foot span {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.stats-notes {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-top: 18px;
}

.note-card {
  padding: 20px 22px;
}

.note-icon {
  width: 32px;
  height: 32px;
  display: grid;
  place-items: center;
  border-radius: 10px;
  margin-bottom: 9px;
}

.note-icon.mint {
  background: #e2f8f1;
  color: #35a184;
}
.note-icon.blue {
  background: #e5f4fb;
  color: #3a93b7;
}
.note-icon.butter {
  background: #fff5dc;
  color: #bd8d35;
}

.note-card h3 {
  margin: 0 0 4px;
  font-size: 12.5px;
  color: #2b5b69;
}

.note-card p {
  margin: 0;
  font-size: 10.5px;
  line-height: 1.8;
  color: #708a93;
}

@keyframes livePulse {
  0%,
  100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.55;
    transform: scale(0.85);
  }
}

@media (max-width: 900px) {
  .stat-grid.four {
    grid-template-columns: 1fr 1fr;
  }
  .stat-grid.three {
    grid-template-columns: 1fr 1fr;
  }
  .stats-notes {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 620px) {
  .stats-panel {
    padding: 20px 16px 16px;
  }
  .panel-head {
    flex-direction: column;
    gap: 8px;
  }
  .stat-grid.four,
  .stat-grid.three {
    grid-template-columns: 1fr 1fr;
  }
  .stat-tile {
    padding: 11px;
  }
  .tile-icon {
    width: 32px;
    height: 32px;
  }
  .stat-tile strong {
    font-size: 19px;
  }
  .chart-block {
    padding: 14px 10px 6px;
  }
  .chart-head {
    flex-direction: column;
    gap: 4px;
  }
  .chart-scroll :deep(.line-chart),
  .chart-scroll :deep(.bar-chart) {
    min-width: 680px;
  }
  .chart-scroll-hint {
    display: block;
  }
  .panel-foot {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
}
</style>
