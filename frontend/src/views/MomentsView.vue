<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { CalendarDays, Heart, MessageCircle, PenLine, Quote, Sparkles } from 'lucide-vue-next'
import PageHeader from '../components/PageHeader.vue'
import AvatarArt from '../components/AvatarArt.vue'
import ArtCover from '../components/ArtCover.vue'
import { api } from '../api/client'
import { auth, openAuth } from '../stores/auth'
import { siteState } from '../stores/site'
import { relativeTime } from '../utils/format'
import type { Moment } from '../types'

const moments = ref<Moment[]>([])
const loading = ref(true)
const error = ref('')
const liked = ref<Set<number>>(new Set())
const site = computed(() => siteState.data)

const totalLikes = computed(() => moments.value.reduce((sum, item) => sum + item.likes, 0))

function toggleLike(moment: Moment) {
  if (!auth.user) {
    openAuth('login')
    return
  }
  const next = new Set(liked.value)
  if (next.has(moment.id)) next.delete(moment.id)
  else next.add(moment.id)
  liked.value = next
}

onMounted(async () => {
  try {
    moments.value = await api.moments()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '随笔加载失败'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page moments-page">
    <div class="container">
      <PageHeader
        align="center"
        eyebrow="LITTLE THOUGHTS"
        title="随笔随心"
        subtitle="随心所悦，热爱生活。这里没有排版工整的长文，只有忽然想记下来的几句话。"
      />

      <div class="moment-stats">
        <span><PenLine :size="13" /> {{ moments.length }} 条碎碎念</span>
        <span><Heart :size="13" /> {{ totalLikes }} 次心动</span>
        <span><CalendarDays :size="13" /> 更新于 {{ moments[0] ? relativeTime(moments[0].createdAt) : '—' }}</span>
      </div>

      <div v-if="error" class="error-box">{{ error }}</div>

      <div v-if="loading" class="loading-block">
        正在翻阅云端的碎碎念<span class="loading-dots"><i></i><i></i><i></i></span>
      </div>

      <section v-else-if="moments.length" class="moment-feed glass-card">
        <article v-for="(moment, index) in moments" :key="moment.id" class="moment-item">
          <div class="moment-avatar">
            <AvatarArt :size="46" />
            <span v-if="index === 0" class="now-dot"></span>
          </div>

          <div class="moment-main">
            <header class="moment-head">
              <div>
                <span class="moment-name">{{ site.nickname }}</span>
                <span class="moment-handle">@{{ site.author }}</span>
              </div>
              <time>{{ relativeTime(moment.createdAt) }}</time>
            </header>

            <p class="moment-text">{{ moment.content }}</p>

            <div v-if="moment.images.length" class="moment-images" :class="{ single: moment.images.length === 1 }">
              <div v-for="(image, i) in moment.images" :key="i" class="moment-image">
                <ArtCover :variant="image" ratio="16 / 9" />
              </div>
            </div>

            <footer class="moment-actions">
              <button type="button" class="moment-action" :class="{ active: liked.has(moment.id) }" @click="toggleLike(moment)">
                <Heart :size="14" :fill="liked.has(moment.id) ? 'currentColor' : 'none'" />
                {{ moment.likes + (liked.has(moment.id) ? 1 : 0) }}
              </button>
              <RouterLink to="/guestbook" class="moment-action">
                <MessageCircle :size="14" /> {{ moment.comments }}
              </RouterLink>
              <span class="moment-mark">☁</span>
            </footer>
          </div>
        </article>

        <div class="feed-end">
          <span></span>
          <p>慢慢来，比较快。</p>
          <span></span>
        </div>
      </section>

      <div v-else-if="!error" class="empty-state glass-card">还没有随笔，等我攒够一点心情。</div>

      <section class="moment-quote glass-card">
        <Quote :size="26" class="quote-icon" />
        <p>“我们记录，不是为了证明什么，<br />只是想在很久以后，还能认出当时的自己。”</p>
        <span>— 写在某个普通的傍晚</span>
        <Sparkles :size="13" class="quote-spark" />
      </section>
    </div>
  </div>
</template>

<style scoped>
.moments-page {
  padding-bottom: 58px;
}

.moment-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 8px 22px;
  margin: -4px 0 22px;
  font-size: 10px;
  color: #7c97a0;
}

.moment-stats span {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.moment-feed {
  max-width: 880px;
  margin: 0 auto;
  padding: 16px 34px 8px;
}

.moment-item {
  display: flex;
  gap: 16px;
  padding: 24px 0;
  border-bottom: 1px dashed rgba(48, 112, 131, 0.16);
}

.moment-item:last-of-type {
  border-bottom: 0;
}

.moment-avatar {
  position: relative;
  flex: none;
  padding-top: 2px;
}

.now-dot {
  position: absolute;
  right: 0;
  bottom: 2px;
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: #50c69b;
  border: 2px solid #fff;
}

.moment-main {
  flex: 1;
  min-width: 0;
}

.moment-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.moment-name {
  font-size: 13px;
  font-weight: 750;
  color: #1e5060;
}

.moment-handle {
  margin-left: 5px;
  font-size: 9.5px;
  color: #9aadb3;
}

.moment-head time {
  font-size: 9.5px;
  color: #9aadb3;
  flex: none;
}

.moment-text {
  margin: 7px 0 12px;
  white-space: pre-line;
  font-size: 13px;
  line-height: 1.95;
  color: #3f616d;
}

.moment-images {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin-bottom: 12px;
}

.moment-images.single {
  grid-template-columns: 1fr;
  max-width: 480px;
}

.moment-image {
  overflow: hidden;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.85);
  box-shadow: 0 8px 20px rgba(32, 102, 124, 0.1);
}

.moment-image :deep(svg) {
  transition: transform 0.5s ease;
}

.moment-image:hover :deep(svg) {
  transform: scale(1.03);
}

.moment-actions {
  display: flex;
  align-items: center;
  gap: 18px;
}

.moment-action {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border: 0;
  background: transparent;
  padding: 0;
  color: #8ba2aa;
  font-size: 10.5px;
  text-decoration: none;
  cursor: pointer;
  transition: color 0.2s ease;
}

.moment-action:hover,
.moment-action.active {
  color: #e2788e;
}

.moment-mark {
  margin-left: auto;
  font-size: 12px;
  color: #b9d4db;
}

.feed-end {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 22px 0 18px;
}

.feed-end span {
  width: 54px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(60, 124, 142, 0.25));
}

.feed-end span:last-child {
  background: linear-gradient(90deg, rgba(60, 124, 142, 0.25), transparent);
}

.feed-end p {
  margin: 0;
  font-family: var(--font-serif);
  font-size: 11px;
  letter-spacing: 2px;
  color: #8aa1a9;
}

.moment-quote {
  position: relative;
  max-width: 880px;
  margin: 20px auto 0;
  padding: 30px;
  text-align: center;
  background: linear-gradient(120deg, rgba(230, 248, 251, 0.9), rgba(255, 255, 255, 0.72));
}

.quote-icon {
  color: #91ccd8;
  opacity: 0.7;
}

.moment-quote p {
  margin: 8px 0 7px;
  font-family: var(--font-serif);
  font-size: 16px;
  line-height: 2;
  letter-spacing: 1.5px;
  color: #3e6d7a;
}

.moment-quote span {
  font-size: 9.5px;
  letter-spacing: 1.5px;
  color: #94a9b0;
}

.quote-spark {
  position: absolute;
  right: 24px;
  bottom: 22px;
  color: #e9c477;
}

@media (max-width: 620px) {
  .moment-feed {
    padding: 10px 18px 4px;
  }
  .moment-item {
    gap: 11px;
    padding: 18px 0;
  }
  .moment-text {
    font-size: 12.5px;
  }
  .moment-images {
    grid-template-columns: 1fr;
  }
  .moment-stats {
    gap: 7px 13px;
  }
  .moment-quote {
    padding: 24px 18px;
  }
  .moment-quote p {
    font-size: 13.5px;
  }
}
</style>
