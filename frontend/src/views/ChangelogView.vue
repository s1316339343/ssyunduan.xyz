<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { CalendarDays, GitCommitHorizontal, Hammer, Sparkles } from 'lucide-vue-next'
import PageHeader from '../components/PageHeader.vue'
import { api } from '../api/client'
import type { ChangelogEntry } from '../types'

const entries = ref<ChangelogEntry[]>([])
const loading = ref(true)
const error = ref('')
const totalItems = computed(() => entries.value.reduce((sum, entry) => sum + entry.items.length, 0))

onMounted(async () => {
  try {
    entries.value = await api.changelog()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '日志加载失败'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page changelog-page">
    <div class="container narrow">
      <PageHeader
        eyebrow="CHANGELOG"
        title="网站更新日志"
        subtitle="施工中…… 每一次小小的改动，都是这片小窝长大的痕迹。"
      />

      <div class="log-summary">
        <span><Hammer :size="13" /> {{ entries.length }} 次更新</span>
        <span><GitCommitHorizontal :size="13" /> {{ totalItems }} 个改动</span>
        <span class="build-state"><i></i> 持续施工中</span>
      </div>

      <div v-if="error" class="error-box">{{ error }}</div>

      <div v-if="loading" class="loading-block">
        正在翻阅施工记录<span class="loading-dots"><i></i><i></i><i></i></span>
      </div>

      <div v-else-if="entries.length" class="log-list">
        <article v-for="(entry, index) in entries" :key="entry.id" class="log-entry">
          <div class="log-rail">
            <span class="log-dot" :class="{ latest: index === 0 }"></span>
            <span v-if="index < entries.length - 1" class="log-line"></span>
          </div>

          <div class="log-content">
            <header class="log-head">
              <div class="log-date">
                <CalendarDays :size="14" />
                <strong>{{ entry.date }}</strong>
                <span v-if="index === 0" class="latest-tag">LATEST</span>
              </div>
              <span class="log-version">{{ entry.version }}</span>
            </header>

            <div class="log-card glass-card">
              <ol class="log-items">
                <li v-for="(item, i) in entry.items" :key="i">
                  <span class="item-no">{{ String(i + 1).padStart(2, '0') }}</span>
                  <span>{{ item }}</span>
                </li>
              </ol>
            </div>
          </div>
        </article>

        <div class="build-note glass-card">
          <Sparkles :size="16" />
          <div>
            <p>下一版想做：全文搜索、文章收藏、夜间模式，还有更多生活照片。</p>
            <span>如果你有想看的功能，欢迎去留言板告诉我。</span>
          </div>
          <RouterLink to="/guestbook">提个建议 →</RouterLink>
        </div>
      </div>

      <div v-else-if="!error" class="empty-state glass-card">施工记录还在整理中。</div>
    </div>
  </div>
</template>

<style scoped>
.changelog-page {
  padding-bottom: 56px;
}

.log-summary {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 20px;
  margin: -2px 0 26px;
  font-size: 10px;
  color: #7f99a2;
}

.log-summary span {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.build-state {
  margin-left: auto;
  padding: 4px 10px;
  border-radius: 999px;
  background: #fff8e7;
  border: 1px solid #f7e8c2;
  color: #b58a37;
}

.build-state i {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #e2b55f;
  animation: buildPulse 2s ease-in-out infinite;
}

.log-list {
  display: grid;
  gap: 4px;
}

.log-entry {
  display: grid;
  grid-template-columns: 24px minmax(0, 1fr);
  gap: 12px;
}

.log-rail {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 6px;
}

.log-dot {
  width: 11px;
  height: 11px;
  border-radius: 50%;
  background: #c3e2e9;
  border: 2px solid #f7fdff;
  box-shadow: 0 0 0 3px rgba(127, 199, 214, 0.18);
  flex: none;
  z-index: 2;
}

.log-dot.latest {
  background: #4dbdd1;
  box-shadow:
    0 0 0 3px rgba(77, 189, 209, 0.2),
    0 0 14px rgba(77, 189, 209, 0.45);
}

.log-line {
  flex: 1;
  width: 1px;
  margin: 4px 0;
  background: linear-gradient(rgba(82, 155, 175, 0.3), rgba(82, 155, 175, 0.08));
}

.log-content {
  padding-bottom: 22px;
}

.log-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 7px;
}

.log-date {
  display: flex;
  align-items: center;
  gap: 7px;
  color: #497582;
}

.log-date strong {
  font-size: 13.5px;
  letter-spacing: 1px;
  color: #2a5b69;
}

.latest-tag {
  padding: 1px 6px;
  border-radius: 4px;
  background: #e3f7fa;
  color: #2e93aa;
  font-family: 'Times New Roman', serif;
  font-size: 7.5px;
  letter-spacing: 1px;
}

.log-version {
  font-family: 'SFMono-Regular', Consolas, monospace;
  font-size: 8.5px;
  color: #9fb2b8;
  padding: 2px 7px;
  border-radius: 999px;
  border: 1px solid rgba(56, 119, 137, 0.14);
}

.log-card {
  padding: 13px 18px;
}

.log-items {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 4px;
}

.log-items li {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 5px 0;
  font-size: 11.5px;
  line-height: 1.7;
  color: #537681;
}

.item-no {
  flex: none;
  font-family: 'Times New Roman', serif;
  font-size: 9px;
  color: #b0c3c8;
  padding-top: 2px;
}

.build-note {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 8px 0 0 36px;
  padding: 16px 20px;
  background: linear-gradient(110deg, rgba(238, 250, 252, 0.92), rgba(255, 255, 255, 0.7));
}

.build-note > svg {
  color: #e0b35e;
  flex: none;
}

.build-note div {
  flex: 1;
}

.build-note p {
  margin: 0 0 2px;
  font-size: 11.5px;
  color: #3e6a77;
}

.build-note span {
  font-size: 9.5px;
  color: #92a7ae;
}

.build-note a {
  flex: none;
  font-size: 10px;
  color: #2c8ba3;
  text-decoration: none;
}

@keyframes buildPulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.35;
  }
}

@media (max-width: 620px) {
  .log-entry {
    grid-template-columns: 18px minmax(0, 1fr);
    gap: 8px;
  }
  .log-card {
    padding: 11px 13px;
  }
  .log-items li {
    font-size: 11px;
  }
  .build-note {
    margin-left: 26px;
    flex-wrap: wrap;
  }
  .build-note a {
    margin-left: 28px;
  }
  .build-state {
    margin-left: 0;
  }
}
</style>
