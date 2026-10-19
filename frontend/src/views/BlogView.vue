<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowRight,
  CalendarDays,
  FolderOpen,
  Github,
  Hash,
  Mail,
  MessageCircle,
  Search,
  Sparkles,
  X,
} from 'lucide-vue-next'
import PageHeader from '../components/PageHeader.vue'
import AvatarArt from '../components/AvatarArt.vue'
import ArtCover from '../components/ArtCover.vue'
import ArticleCard from '../components/ArticleCard.vue'
import { api } from '../api/client'
import { isAdmin } from '../stores/auth'
import { siteState } from '../stores/site'
import type { Article, Stats } from '../types'
import { formatDate } from '../utils/format'

const route = useRoute()
const router = useRouter()
const articles = ref<Article[]>([])
const stats = ref<Stats | null>(null)
const loading = ref(true)
const error = ref('')
const searchInput = ref('')
const site = computed(() => siteState.data)

const categories = computed(() => Array.from(new Set(articles.value.map((a) => a.category))))
const activeCategory = computed(() => (typeof route.query.category === 'string' ? route.query.category : '全部'))
const activeTag = computed(() => (typeof route.query.tag === 'string' ? route.query.tag : ''))
const keyword = computed(() => (typeof route.query.q === 'string' ? route.query.q.trim() : ''))

const tagCounts = computed(() => {
  const map = new Map<string, number>()
  articles.value.forEach((article) => article.tags.forEach((tag) => map.set(tag, (map.get(tag) || 0) + 1)))
  return Array.from(map.entries())
    .map(([name, count]) => ({ name, count }))
    .sort((a, b) => b.count - a.count || a.name.localeCompare(b.name))
})

const recent = computed(() => [...articles.value].sort((a, b) => b.publishedAt.localeCompare(a.publishedAt)).slice(0, 3))
const featured = computed(() => {
  const list = articles.value.filter((a) => a.featured)
  return (list.length ? list : articles.value).slice(0, 4)
})
const archiveYear = computed(() => (articles.value[0]?.publishedAt || '2026').slice(0, 4))
const archiveCount = computed(() => articles.value.filter((a) => a.publishedAt.startsWith(archiveYear.value)).length)

const filtered = computed(() => {
  const q = keyword.value.toLowerCase()
  return articles.value.filter((article) => {
    if (activeCategory.value !== '全部' && article.category !== activeCategory.value) return false
    if (activeTag.value && !article.tags.includes(activeTag.value)) return false
    if (q) {
      const haystack = `${article.title} ${article.excerpt} ${article.tags.join(' ')} ${article.category}`.toLowerCase()
      if (!haystack.includes(q)) return false
    }
    return true
  })
})

function selectCategory(category: string) {
  const query = { ...route.query }
  delete query.tag
  if (category === '全部') delete query.category
  else query.category = category
  router.push({ name: 'blog', query })
}

function clearFilters() {
  searchInput.value = ''
  router.push({ name: 'blog' })
}

watch(
  () => route.query.q,
  (q) => {
    searchInput.value = typeof q === 'string' ? q : ''
  },
  { immediate: true },
)

function search() {
  const query = { ...route.query }
  if (searchInput.value.trim()) query.q = searchInput.value.trim()
  else delete query.q
  router.push({ name: 'blog', query })
}

onMounted(async () => {
  try {
    const [articleData, statData] = await Promise.all([api.articles(), api.stats()])
    articles.value = articleData
    stats.value = statData
  } catch (e) {
    error.value = e instanceof Error ? e.message : '文章加载失败'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page blog-page">
    <div class="container">
      <PageHeader
        eyebrow="MY JOURNAL"
        title="博客"
        subtitle="把走过的路，写成可以重读的字。这里收集技术手记、项目复盘，以及一些不想忘记的生活片段。"
      />
    </div>

    <div class="container">
      <div v-if="error" class="error-box">
        {{ error }}
        <span class="muted">（请先启动 backend，再刷新页面）</span>
      </div>

      <div class="blog-layout">
        <aside class="blog-side">
          <div class="profile-card glass-card">
            <div class="profile-cover">
              <span class="cover-cloud c1"></span>
              <span class="cover-cloud c2"></span>
              <span class="cover-star">✦</span>
            </div>
            <div class="profile-avatar">
              <AvatarArt :size="94" />
            </div>
            <div class="profile-info">
              <h2>{{ site.nickname }}<span>YUNDUAN</span></h2>
              <p class="profile-author">{{ site.author }} · 小窝主人</p>
              <p class="profile-tagline">{{ site.tagline }}</p>

              <div class="profile-stats">
                <div><strong>{{ stats?.articles ?? articles.length }}</strong><span>文章</span></div>
                <div><strong>{{ stats?.tags ?? tagCounts.length }}</strong><span>标签</span></div>
                <div><strong>{{ stats?.categories ?? categories.length }}</strong><span>分类</span></div>
              </div>

              <a class="follow-btn" :href="site.github || '#'" target="_blank" rel="noreferrer noopener">
                <Github :size="15" /> Follow Me
              </a>

              <div class="profile-socials">
                <a :href="site.github || '#'" target="_blank" rel="noreferrer noopener" aria-label="GitHub"><Github :size="16" /></a>
                <a :href="`mailto:${site.email}`" aria-label="邮箱"><Mail :size="16" /></a>
                <RouterLink to="/guestbook" aria-label="留言板"><MessageCircle :size="16" /></RouterLink>
              </div>
            </div>
          </div>

          <div class="side-card glass-card">
            <div class="side-title">
              <span><Sparkles :size="14" /> 最近更新</span>
            </div>
            <div class="recent-list">
              <RouterLink v-for="item in recent" :key="item.id" class="recent-item" :to="`/blog/${item.slug}`">
                <ArtCover :variant="item.cover" ratio="16 / 10" class="recent-cover" />
                <div class="recent-copy">
                  <p>{{ item.title }}</p>
                  <span>{{ formatDate(item.publishedAt) }}</span>
                </div>
              </RouterLink>
              <p v-if="!recent.length" class="side-empty">暂无更新</p>
            </div>
          </div>

          <div class="side-card glass-card">
            <div class="side-title">
              <span><Hash :size="14" /> 标签</span>
            </div>
            <div class="tag-cloud">
              <RouterLink
                v-for="tag in tagCounts"
                :key="tag.name"
                :to="{ name: 'blog', query: { tag: tag.name } }"
                class="tag-item"
                :class="{ active: activeTag === tag.name }"
              >
                {{ tag.name }}<sup>{{ tag.count }}</sup>
              </RouterLink>
              <p v-if="!tagCounts.length" class="side-empty">暂无标签</p>
            </div>
          </div>
        </aside>

        <section class="blog-main">
          <div class="welcome-strip">
            <span class="welcome-emoji">👋</span>
            <p>欢迎来到「{{ site.nickname }}」的小窝～ 随便逛逛，愿你找到喜欢的那一页。</p>
            <span class="welcome-dots">· · ·</span>
          </div>

          <div class="top-grid">
            <div class="archive-card">
              <span class="archive-label"><FolderOpen :size="13" /> 归档</span>
              <strong>{{ archiveYear }}</strong>
              <span class="archive-count">{{ archiveCount }} 篇文章</span>
            </div>

            <RouterLink
              v-for="item in featured"
              :key="item.id"
              :to="`/blog/${item.slug}`"
              class="feature-mini"
            >
              <ArtCover :variant="item.cover" ratio="16 / 10" />
              <span class="feature-title">{{ item.title }}</span>
            </RouterLink>
          </div>

          <div class="list-toolbar">
            <div class="tabs">
              <button type="button" class="tab" :class="{ active: activeCategory === '全部' }" @click="selectCategory('全部')">
                全部
              </button>
              <button
                v-for="category in categories"
                :key="category"
                type="button"
                class="tab"
                :class="{ active: activeCategory === category }"
                @click="selectCategory(category)"
              >
                {{ category }}
              </button>
            </div>

            <form class="inline-search" @submit.prevent="search">
              <Search :size="14" />
              <input v-model="searchInput" type="search" placeholder="搜一搜…" aria-label="搜索文章" />
              <button v-if="searchInput" type="button" aria-label="清空" @click="searchInput = ''; search()"><X :size="12" /></button>
            </form>
          </div>

          <div v-if="activeTag || keyword" class="filter-note">
            <span>
              正在查看
              <b v-if="activeTag">标签 #{{ activeTag }}</b>
              <b v-if="activeTag && keyword"> · </b>
              <b v-if="keyword">关键词“{{ keyword }}”</b>
              的相关文章
            </span>
            <button type="button" @click="clearFilters">清除筛选 <X :size="11" /></button>
          </div>

          <div v-if="loading" class="loading-block">
            正在从云端取回文章<span class="loading-dots"><i></i><i></i><i></i></span>
          </div>

          <div v-else-if="filtered.length" class="article-list">
            <ArticleCard
              v-for="(article, index) in filtered"
              :key="article.id"
              :article="article"
              :reverse="index % 2 === 1"
            />
          </div>

          <div v-else class="empty-state glass-card">
            <p>{{ activeTag || keyword ? '没有找到匹配的文章。' : '这里还没有文章。' }}</p>
            <RouterLink v-if="isAdmin && !activeTag && !keyword" to="/admin" class="btn btn-primary">去写第一篇</RouterLink>
            <button v-else class="btn btn-soft" type="button" @click="clearFilters">看看全部文章</button>
          </div>

          <div class="list-foot">
            <span>共 {{ filtered.length }} 篇 · 慢慢写，慢慢更新</span>
            <span class="foot-line"></span>
            <span>{{ site.domain }}</span>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<style scoped>
.blog-page {
  padding-bottom: 56px;
}

.blog-layout {
  display: grid;
  grid-template-columns: 292px minmax(0, 1fr);
  gap: 22px;
  align-items: start;
  margin-top: 4px;
  min-width: 0;
}

.blog-main,
.blog-side,
.profile-card,
.side-card {
  min-width: 0;
}

.blog-side {
  display: grid;
  gap: 16px;
  position: sticky;
  top: 84px;
}

.profile-card {
  overflow: hidden;
  text-align: center;
}

.profile-cover {
  position: relative;
  height: 82px;
  overflow: hidden;
  background:
    radial-gradient(circle at 76% 18%, rgba(255, 255, 255, 0.7), transparent 32%),
    linear-gradient(120deg, #9fe4df, #7fc9e8 55%, #b7e8f4);
}

.cover-cloud {
  position: absolute;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.55);
}
.cover-cloud.c1 {
  width: 110px;
  height: 30px;
  left: -18px;
  top: 22px;
}
.cover-cloud.c2 {
  width: 86px;
  height: 24px;
  right: -12px;
  bottom: 6px;
  opacity: 0.5;
}

.cover-star {
  position: absolute;
  right: 28px;
  top: 18px;
  color: rgba(255, 255, 255, 0.85);
  font-size: 15px;
}

.profile-avatar {
  display: flex;
  justify-content: center;
  margin-top: -48px;
  position: relative;
  z-index: 2;
}

.profile-info {
  padding: 10px 20px 20px;
}

.profile-info h2 {
  margin: 0;
  font-size: 19px;
  letter-spacing: 1px;
  color: #174653;
}

.profile-info h2 span {
  margin-left: 5px;
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 1px;
  color: #8ca4ac;
}

.profile-author {
  margin: 2px 0 8px;
  font-size: 10px;
  letter-spacing: 1px;
  color: #8ba2aa;
}

.profile-tagline {
  margin: 0;
  font-family: var(--font-serif);
  font-size: 11.5px;
  letter-spacing: 0.5px;
  color: #4e7d8a;
}

.profile-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 4px;
  margin: 16px 0 13px;
  padding: 10px 0;
  border-top: 1px solid rgba(48, 113, 133, 0.1);
  border-bottom: 1px solid rgba(48, 113, 133, 0.1);
}

.profile-stats div {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.profile-stats strong {
  font-size: 19px;
  line-height: 1.1;
  color: #1d6a80;
}

.profile-stats span {
  font-size: 9px;
  letter-spacing: 1px;
  color: #92a8af;
}

.follow-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 34px;
  border-radius: 10px;
  background: linear-gradient(135deg, #62c8dd, #2f9fc1);
  color: #fff;
  font-size: 11.5px;
  font-weight: 700;
  text-decoration: none;
  box-shadow: 0 9px 20px rgba(43, 152, 180, 0.22);
  transition: all 0.2s ease;
}

.follow-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 26px rgba(43, 152, 180, 0.3);
}

.profile-socials {
  display: flex;
  justify-content: center;
  gap: 18px;
  margin-top: 14px;
}

.profile-socials a {
  color: #5a7e89;
  transition: color 0.2s ease;
}

.profile-socials a:hover {
  color: #1f88a3;
}

.side-card {
  padding: 17px 18px;
}

.side-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 10px;
  margin-bottom: 12px;
  border-bottom: 1px dashed rgba(49, 113, 132, 0.16);
}

.side-title span {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 1px;
  color: #3d6d7b;
}

.recent-list {
  display: grid;
  gap: 11px;
}

.recent-item {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  group: recent;
}

.recent-cover {
  width: 58px;
  flex: none;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 10px rgba(25, 92, 112, 0.12);
}

.recent-cover :deep(svg) {
  transition: transform 0.35s ease;
}

.recent-item:hover .recent-cover :deep(svg) {
  transform: scale(1.06);
}

.recent-copy {
  min-width: 0;
}

.recent-copy p {
  margin: 0 0 3px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-size: 11.5px;
  line-height: 1.55;
  color: #345e6c;
}

.recent-item:hover .recent-copy p {
  color: #1e7b94;
}

.recent-copy span {
  font-size: 9px;
  color: #98abb1;
}

.side-empty {
  margin: 0;
  padding: 8px 0;
  font-size: 10px;
  color: #9bb0b7;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 7px 8px;
}

.tag-item {
  display: inline-flex;
  align-items: flex-start;
  font-size: 12px;
  color: #7b6aa0;
  text-decoration: none;
  transition: color 0.2s ease;
}

.tag-item:nth-child(3n + 2) {
  color: #47869b;
}
.tag-item:nth-child(3n + 3) {
  color: #b27a56;
}
.tag-item:nth-child(4n + 1) {
  color: #c46b86;
}

.tag-item sup {
  margin-left: 1px;
  font-size: 8px;
  color: #9bb0b7;
}

.tag-item:hover,
.tag-item.active {
  color: #1f88a3;
  text-decoration: underline;
}

.welcome-strip {
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 11px 16px;
  margin-bottom: 16px;
  border-radius: 13px;
  background: rgba(255, 255, 255, 0.58);
  border: 1px solid rgba(255, 255, 255, 0.85);
  box-shadow: var(--shadow-sm);
}

.welcome-strip p {
  margin: 0;
  font-size: 11.5px;
  color: #577884;
}

.welcome-emoji {
  font-size: 15px;
}

.welcome-dots {
  margin-left: auto;
  letter-spacing: 3px;
  color: #a8bcc2;
  font-size: 9px;
}

.top-grid {
  display: grid;
  grid-template-columns: 1.08fr repeat(4, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 18px;
  min-width: 0;
}

.feature-mini {
  min-width: 0;
}

.archive-card {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 2px;
  padding: 15px 10px;
  border-radius: 15px;
  background: linear-gradient(150deg, rgba(195, 240, 234, 0.95), rgba(160, 224, 224, 0.78));
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 10px 24px rgba(42, 127, 137, 0.1);
}

.archive-label {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 9px;
  letter-spacing: 2px;
  color: #3d7d82;
}

.archive-card strong {
  font-family: 'Times New Roman', Georgia, serif;
  font-size: 28px;
  line-height: 1.1;
  color: #2e7a84;
  letter-spacing: 1px;
}

.archive-count {
  font-size: 8.5px;
  color: #6f989b;
}

.feature-mini {
  overflow: hidden;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.75);
  border: 1px solid rgba(255, 255, 255, 0.85);
  box-shadow: 0 8px 20px rgba(31, 98, 120, 0.1);
  text-decoration: none;
  transition: transform 0.25s ease;
}

.feature-mini:hover {
  transform: translateY(-3px);
}

.feature-mini :deep(.art-cover) {
  border-radius: 0;
}

.feature-title {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  padding: 8px 9px;
  font-size: 9.5px;
  line-height: 1.5;
  color: #3d6470;
  min-height: 36px;
}

.list-toolbar {
  display: flex;
  align-items: center;
  gap: 14px;
  justify-content: space-between;
  padding: 7px 10px;
  margin-bottom: 14px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.66);
  border: 1px solid rgba(255, 255, 255, 0.85);
  box-shadow: var(--shadow-sm);
}

.tabs {
  display: flex;
  align-items: center;
  gap: 2px;
  overflow-x: auto;
  scrollbar-width: none;
}

.tabs::-webkit-scrollbar {
  display: none;
}

.tab {
  flex: none;
  border: 0;
  border-radius: 9px;
  padding: 7px 12px;
  background: transparent;
  color: #6a8993;
  font-size: 11.5px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab:hover {
  color: #2e7388;
}

.tab.active {
  background: #fff;
  color: #1a728c;
  box-shadow: 0 5px 14px rgba(37, 111, 134, 0.12);
}

.inline-search {
  display: flex;
  align-items: center;
  gap: 6px;
  width: 168px;
  flex: none;
  padding: 5px 9px;
  border-radius: 9px;
  background: rgba(244, 251, 253, 0.9);
  border: 1px solid rgba(70, 139, 158, 0.1);
  color: #8da6ae;
}

.inline-search input {
  min-width: 0;
  flex: 1;
  border: 0;
  outline: 0;
  background: transparent;
  font-size: 10.5px;
  color: #2b5d6b;
}

.inline-search button {
  border: 0;
  background: transparent;
  color: #98adb4;
  padding: 0;
  cursor: pointer;
  display: grid;
  place-items: center;
}

.filter-note {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 9px 14px;
  margin-bottom: 14px;
  border-radius: 11px;
  background: #effafc;
  border: 1px solid #dff2f6;
  font-size: 11px;
  color: #557d89;
}

.filter-note b {
  color: #1e7b94;
}

.filter-note button {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  border: 0;
  background: transparent;
  color: #2c8aa3;
  font-size: 10.5px;
  cursor: pointer;
  flex: none;
}

.article-list {
  display: grid;
  gap: 16px;
}

.list-foot {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 26px;
  font-size: 9.5px;
  letter-spacing: 1px;
  color: #93a9b0;
}

.foot-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, rgba(50, 114, 132, 0.14), transparent);
}

@media (max-width: 1050px) {
  .blog-layout {
    grid-template-columns: 255px minmax(0, 1fr);
  }
  .top-grid {
    grid-template-columns: 1fr 1fr;
  }
  .archive-card {
    grid-row: span 2;
  }
}

@media (max-width: 860px) {
  .blog-layout {
    grid-template-columns: minmax(0, 1fr);
  }
  .blog-side {
    position: static;
    grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  }
  .profile-card {
    grid-row: span 2;
  }
  .blog-main {
    margin-top: 4px;
  }
}

@media (max-width: 620px) {
  .blog-side {
    grid-template-columns: minmax(0, 1fr);
  }
  .profile-card {
    grid-row: auto;
  }
  .top-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .archive-card {
    grid-row: auto;
    grid-column: 1 / -1;
    flex-direction: row;
    gap: 10px;
    padding: 12px;
  }
  .archive-card strong {
    font-size: 22px;
  }
  .list-toolbar {
    flex-wrap: wrap;
    gap: 8px;
  }
  .inline-search {
    width: 100%;
  }
  .list-foot {
    flex-wrap: wrap;
  }
}
</style>
