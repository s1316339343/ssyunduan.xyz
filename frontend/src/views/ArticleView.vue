<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowLeft,
  ArrowRight,
  CalendarDays,
  Clock3,
  Eye,
  Github,
  Hash,
  ListTree,
  Mail,
  MessageCircle,
  PenLine,
  RefreshCw,
} from 'lucide-vue-next'
import ArtCover from '../components/ArtCover.vue'
import AvatarArt from '../components/AvatarArt.vue'
import ArticleCard from '../components/ArticleCard.vue'
import { api } from '../api/client'
import { isAdmin } from '../stores/auth'
import { siteState } from '../stores/site'
import type { Article } from '../types'
import { formatDate } from '../utils/format'

const route = useRoute()
const router = useRouter()
const site = computed(() => siteState.data)

const article = ref<Article | null>(null)
const all = ref<Article[]>([])
const loading = ref(true)
const error = ref('')
const progress = ref(0)
const bodyEl = ref<HTMLElement | null>(null)

const headings = computed(() => {
  if (!article.value) return []
  const matches = [...article.value.content.matchAll(/<h2[^>]*>(.*?)<\/h2>/g)]
  return matches.map((match, index) => ({
    id: `section-${index}`,
    text: match[1].replace(/<[^>]+>/g, ''),
  }))
})

const renderedContent = computed(() => {
  if (!article.value) return ''
  let index = 0
  return article.value.content.replace(/<h2(\s[^>]*)?>/g, () => `<h2 id="section-${index++}">`)
})

const related = computed(() => {
  if (!article.value) return []
  const others = all.value.filter((item) => item.id !== article.value?.id)
  const same = others.filter((item) => item.category === article.value?.category)
  const rest = others.filter((item) => !same.some((entry) => entry.id === item.id))
  return [...same, ...rest].slice(0, 2)
})

const prev = computed(() => {
  if (!article.value) return null
  const index = all.value.findIndex((item) => item.id === article.value?.id)
  return index > 0 ? all.value[index - 1] : null
})

const next = computed(() => {
  if (!article.value) return null
  const index = all.value.findIndex((item) => item.id === article.value?.id)
  return index >= 0 && index < all.value.length - 1 ? all.value[index + 1] : null
})

function updateProgress() {
  if (!bodyEl.value) {
    progress.value = 0
    return
  }
  const rect = bodyEl.value.getBoundingClientRect()
  const total = rect.height - window.innerHeight * 0.4
  const read = Math.min(Math.max(-rect.top + window.innerHeight * 0.32, 0), Math.max(total, 1))
  progress.value = Math.min(100, Math.round((read / Math.max(total, 1)) * 100))
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const [detail, list] = await Promise.all([api.article(String(route.params.slug)), api.articles()])
    article.value = detail
    all.value = list
    document.title = `${detail.title} · 云端の小窝`
    await nextTick()
    updateProgress()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '文章不存在'
  } finally {
    loading.value = false
  }
}

function scrollTo(id: string) {
  document.getElementById(id)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

onMounted(() => {
  load()
  window.addEventListener('scroll', updateProgress, { passive: true })
})
onUnmounted(() => window.removeEventListener('scroll', updateProgress))

watch(
  () => route.params.slug,
  () => {
    window.scrollTo({ top: 0 })
    load()
  },
)
</script>

<template>
  <div class="page article-page">
    <div class="reading-bar" :style="{ width: `${progress}%` }"></div>

    <div v-if="loading" class="container">
      <div class="loading-block article-loading">
        正在展开这一页<span class="loading-dots"><i></i><i></i><i></i></span>
      </div>
    </div>

    <div v-else-if="error" class="container">
      <div class="article-error glass-card">
        <p class="error-emoji">☁️</p>
        <h1>这页好像被风吹走了</h1>
        <p>{{ error }}</p>
        <RouterLink to="/blog" class="btn btn-primary"><ArrowLeft :size="14" /> 回到博客</RouterLink>
      </div>
    </div>

    <template v-else-if="article">
      <header class="article-head container">
        <div class="article-topline">
          <RouterLink to="/blog" class="back-link"><ArrowLeft :size="13" /> 返回博客</RouterLink>
          <RouterLink
            v-if="isAdmin"
            :to="{ name: 'admin', query: { edit: article.slug } }"
            class="edit-article-link"
          >
            <PenLine :size="13" /> 编辑本文
          </RouterLink>
        </div>

        <div class="head-tags">
          <span class="head-cat">{{ article.category }}</span>
          <span v-if="article.featured" class="head-featured">精选</span>
        </div>

        <h1>{{ article.title }}</h1>
        <p class="article-excerpt">{{ article.excerpt }}</p>

        <div class="article-meta">
          <span class="author-mini"><AvatarArt :size="26" :ring="false" /> {{ site.nickname }}</span>
          <span><CalendarDays :size="13" /> 发表于 {{ formatDate(article.publishedAt) }}</span>
          <span><RefreshCw :size="12" /> 更新于 {{ formatDate(article.updatedAt) }}</span>
          <span><Eye :size="13" /> {{ article.views }} 次阅读</span>
          <span><Clock3 :size="13" /> {{ article.words }} 字</span>
        </div>
      </header>

      <div class="container">
        <div class="article-cover-wrap">
          <ArtCover :variant="article.cover" ratio="21 / 8" />
          <span class="cover-caption">{{ article.category }} · {{ site.domain }}</span>
        </div>
      </div>

      <div class="container article-layout">
        <article ref="bodyEl" class="article-body glass-card">
          <div class="prose" v-html="renderedContent"></div>

          <div class="article-end">
            <span class="end-line"></span>
            <p>谢谢你读到这里。</p>
            <span class="end-line"></span>
          </div>

          <div class="article-tags">
            <RouterLink
              v-for="tag in article.tags"
              :key="tag"
              :to="{ name: 'blog', query: { tag } }"
              class="tag-chip"
            >
              <Hash :size="10" /> {{ tag }}
            </RouterLink>
          </div>

          <div class="author-card">
            <AvatarArt :size="58" />
            <div>
              <p class="author-name">{{ site.author }} <span>/ {{ site.nickname }}</span></p>
              <p class="author-bio">{{ site.bio }}</p>
              <div class="author-links">
                <a :href="site.github || '#'" target="_blank" rel="noreferrer noopener"><Github :size="13" /> GitHub</a>
                <a :href="`mailto:${site.email}`"><Mail :size="13" /> 写信</a>
                <RouterLink to="/guestbook"><MessageCircle :size="13" /> 留言</RouterLink>
              </div>
            </div>
          </div>

          <nav class="article-nav">
            <RouterLink v-if="prev" :to="`/blog/${prev.slug}`" class="nav-item">
              <span class="nav-label"><ArrowLeft :size="11" /> 上一篇</span>
              <span class="nav-title">{{ prev.title }}</span>
            </RouterLink>
            <span v-else class="nav-empty"></span>

            <RouterLink v-if="next" :to="`/blog/${next.slug}`" class="nav-item right">
              <span class="nav-label">下一篇 <ArrowRight :size="11" /></span>
              <span class="nav-title">{{ next.title }}</span>
            </RouterLink>
          </nav>
        </article>

        <aside class="article-side">
          <div class="side-box glass-card">
            <div class="side-head"><ListTree :size="14" /> 本文目录</div>
            <nav v-if="headings.length" class="toc">
              <button v-for="(heading, index) in headings" :key="heading.id" type="button" @click="scrollTo(heading.id)">
                <span>{{ String(index + 1).padStart(2, '0') }}</span>
                {{ heading.text }}
              </button>
            </nav>
            <p v-else class="toc-empty">这篇文章没有小标题，慢慢读就好。</p>
            <div class="reading-state">
              <span>阅读进度</span>
              <div class="reading-track"><i :style="{ width: `${progress}%` }"></i></div>
              <b>{{ progress }}%</b>
            </div>
          </div>

          <div class="side-box glass-card side-profile">
            <AvatarArt :size="62" />
            <p class="side-name">{{ site.nickname }}</p>
            <p class="side-sign">{{ site.tagline }}</p>
            <RouterLink to="/about" class="side-btn">认识我 <ArrowRight :size="12" /></RouterLink>
          </div>

          <div class="side-box glass-card side-note">
            <PenLine :size="16" />
            <p>文字是时间的锚点，<br />写下来，就不会丢。</p>
            <RouterLink to="/moments">去看随笔 →</RouterLink>
          </div>
        </aside>
      </div>

      <section v-if="related.length" class="container related-section">
        <div class="section-head">
          <div>
            <p class="eyebrow">KEEP READING</p>
            <h2>再读两篇</h2>
          </div>
          <RouterLink to="/blog" class="section-link">全部文章 <ArrowRight :size="13" /></RouterLink>
        </div>
        <div class="related-grid">
          <ArticleCard v-for="item in related" :key="item.id" :article="item" layout="tile" />
        </div>
      </section>
    </template>
  </div>
</template>

<style scoped>
.article-page {
  padding-bottom: 56px;
}

.reading-bar {
  position: fixed;
  top: 66px;
  left: 0;
  height: 2px;
  background: linear-gradient(90deg, #4cc5d8, #1f8faf);
  z-index: 51;
  transition: width 0.15s ease;
  box-shadow: 0 0 8px rgba(47, 168, 201, 0.6);
}

.article-loading {
  margin-top: 90px;
}

.article-error {
  margin-top: 110px;
  padding: 60px 30px;
  text-align: center;
}

.error-emoji {
  font-size: 40px;
  margin: 0 0 8px;
}

.article-error h1 {
  margin: 0 0 6px;
  font-size: 25px;
}

.article-error p {
  margin: 0 0 20px;
  color: #7d98a2;
  font-size: 12.5px;
}

.article-head {
  padding-top: 104px;
  padding-bottom: 24px;
  max-width: 960px;
}

.article-topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 22px;
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 11px;
  font-weight: 650;
  color: #4f8495;
  text-decoration: none;
}

.edit-article-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 5px 10px;
  border-radius: 7px;
  background: rgba(255, 255, 255, 0.68);
  border: 1px solid rgba(255, 255, 255, 0.9);
  color: #2b8198;
  font-size: 10px;
  font-weight: 650;
  text-decoration: none;
}

.edit-article-link:hover {
  background: #fff;
}

.back-link:hover {
  color: #1b7f9e;
}

.head-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.head-cat {
  padding: 4px 9px;
  border-radius: 6px;
  background: #e2f6fa;
  color: #1e7c95;
  font-size: 9.5px;
  font-weight: 700;
  letter-spacing: 1.5px;
}

.head-featured {
  padding: 4px 9px;
  border-radius: 6px;
  background: #fff2d4;
  color: #b17c19;
  font-size: 9.5px;
  font-weight: 700;
  letter-spacing: 1px;
}

.article-head h1 {
  margin: 0;
  font-size: clamp(28px, 4vw, 43px);
  line-height: 1.35;
  letter-spacing: 1px;
  color: #123d4a;
}

.article-excerpt {
  max-width: 760px;
  margin: 13px 0 18px;
  font-size: 13.5px;
  line-height: 1.9;
  color: #5d7d87;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 16px;
  font-size: 10.5px;
  color: #829ba4;
}

.article-meta > span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.author-mini {
  gap: 7px !important;
  padding-right: 10px;
  border-right: 1px solid rgba(50, 113, 132, 0.16);
  color: #446e7b;
  font-weight: 650;
}

.article-cover-wrap {
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 20px 50px rgba(25, 92, 112, 0.18);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.article-cover-wrap :deep(.art-cover) {
  display: block;
}

.cover-caption {
  position: absolute;
  right: 16px;
  bottom: 14px;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.82);
  color: #3f6d79;
  font-size: 9px;
  letter-spacing: 1px;
  backdrop-filter: blur(8px);
}

.article-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 264px;
  gap: 22px;
  align-items: start;
  margin-top: 22px;
}

.article-body {
  padding: 38px 46px 30px;
  min-width: 0;
}

.prose {
  font-size: 14px;
  line-height: 2;
  color: #34545f;
}

.prose :deep(p) {
  margin: 0 0 18px;
}

.prose :deep(h2) {
  position: relative;
  margin: 34px 0 14px;
  padding-left: 14px;
  font-size: 21px;
  line-height: 1.5;
  color: #164452;
  scroll-margin-top: 92px;
}

.prose :deep(h2)::before {
  content: '';
  position: absolute;
  left: 0;
  top: 7px;
  bottom: 7px;
  width: 4px;
  border-radius: 99px;
  background: linear-gradient(#65cdd9, #2f9fbd);
}

.prose :deep(h3) {
  margin: 26px 0 10px;
  font-size: 16.5px;
  color: #225966;
}

.prose :deep(blockquote) {
  margin: 22px 0;
  padding: 15px 20px;
  border-left: 3px solid #8fd7df;
  border-radius: 0 12px 12px 0;
  background: #f2fbfc;
  color: #4e7884;
  font-family: var(--font-serif);
  font-size: 13.5px;
}

.prose :deep(blockquote p:last-child) {
  margin-bottom: 0;
}

.prose :deep(ul),
.prose :deep(ol) {
  margin: 0 0 18px;
  padding-left: 22px;
}

.prose :deep(li) {
  margin-bottom: 7px;
}

.prose :deep(strong) {
  color: #154a5b;
}

.prose :deep(a) {
  color: #1b7f9e;
  text-underline-offset: 3px;
}

.prose :deep(code) {
  padding: 2px 6px;
  border-radius: 5px;
  background: #edf7fa;
  color: #22708a;
  font-family: 'SFMono-Regular', Consolas, monospace;
  font-size: 0.9em;
}

.prose :deep(pre) {
  position: relative;
  margin: 20px 0;
  padding: 18px 20px;
  overflow-x: auto;
  border-radius: 13px;
  background: #123644;
  color: #cfedf3;
  font-size: 12px;
  line-height: 1.8;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.06);
}

.prose :deep(pre code) {
  padding: 0;
  background: transparent;
  color: inherit;
  font-size: inherit;
}

.prose :deep(hr) {
  margin: 28px 0;
  border: 0;
  border-top: 1px dashed rgba(48, 112, 131, 0.2);
}

.prose :deep(img) {
  border-radius: 14px;
  margin: 10px 0;
}

.article-end {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin: 40px 0 22px;
  color: #9bb0b7;
  font-family: var(--font-serif);
  font-size: 12px;
}

.article-end p {
  margin: 0;
  letter-spacing: 2px;
  flex: none;
}

.end-line {
  width: 54px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(63, 126, 144, 0.28));
}

.end-line:last-child {
  background: linear-gradient(90deg, rgba(63, 126, 144, 0.28), transparent);
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 7px;
  padding-bottom: 26px;
  border-bottom: 1px dashed rgba(49, 113, 132, 0.16);
}

.author-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-top: 24px;
  padding: 18px;
  border-radius: 16px;
  background: rgba(242, 251, 252, 0.8);
  border: 1px solid rgba(206, 238, 244, 0.8);
}

.author-name {
  margin: 2px 0 4px;
  font-size: 13.5px;
  font-weight: 750;
  color: #1c4d5c;
}

.author-name span {
  font-size: 10px;
  font-weight: 500;
  color: #8ba2aa;
}

.author-bio {
  margin: 0 0 8px;
  font-size: 11.5px;
  line-height: 1.7;
  color: #64818b;
}

.author-links {
  display: flex;
  gap: 14px;
  font-size: 10px;
}

.author-links a {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #3b8499;
  text-decoration: none;
}

.article-nav {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-top: 24px;
}

.nav-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 13px 15px;
  border-radius: 12px;
  background: #f7fcfd;
  border: 1px solid #e7f3f6;
  text-decoration: none;
  transition: all 0.2s ease;
}

.nav-item:hover {
  background: #eefafc;
  border-color: #d2edf3;
}

.nav-item.right {
  text-align: right;
}

.nav-label {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 9.5px;
  color: #8da4ac;
}

.nav-item.right .nav-label {
  justify-content: flex-end;
}

.nav-title {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-size: 11.5px;
  font-weight: 600;
  color: #376574;
}

.article-side {
  display: grid;
  gap: 14px;
  position: sticky;
  top: 88px;
}

.side-box {
  padding: 17px;
}

.side-head {
  display: flex;
  align-items: center;
  gap: 6px;
  padding-bottom: 9px;
  margin-bottom: 9px;
  border-bottom: 1px dashed rgba(48, 112, 131, 0.16);
  font-size: 11px;
  font-weight: 700;
  color: #3c6e7c;
}

.toc {
  display: grid;
  gap: 2px;
}

.toc button {
  display: flex;
  align-items: flex-start;
  gap: 7px;
  width: 100%;
  padding: 6px;
  border: 0;
  border-radius: 7px;
  background: transparent;
  text-align: left;
  font-size: 10.5px;
  line-height: 1.5;
  color: #6d8992;
  cursor: pointer;
}

.toc button:hover {
  background: #f1fafc;
  color: #1e7d96;
}

.toc button span {
  font-family: 'Times New Roman', serif;
  font-size: 9px;
  color: #a2b5bb;
  flex: none;
  padding-top: 1px;
}

.toc-empty {
  margin: 0;
  font-size: 10.5px;
  color: #98abb1;
  line-height: 1.7;
}

.reading-state {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  padding-top: 11px;
  border-top: 1px dashed rgba(48, 112, 131, 0.16);
  font-size: 9px;
  color: #8fa5ac;
}

.reading-track {
  flex: 1;
  height: 4px;
  border-radius: 99px;
  background: #e9f4f7;
  overflow: hidden;
}

.reading-track i {
  display: block;
  height: 100%;
  border-radius: 99px;
  background: linear-gradient(90deg, #6fd3e0, #2fa8c9);
  transition: width 0.15s linear;
}

.reading-state b {
  color: #32849a;
  font-size: 9px;
}

.side-profile {
  text-align: center;
}

.side-profile :deep(.avatar-art) {
  margin: 0 auto;
}

.side-name {
  margin: 7px 0 2px;
  font-size: 13px;
  font-weight: 750;
  color: #1e4f5e;
}

.side-sign {
  margin: 0 0 10px;
  font-family: var(--font-serif);
  font-size: 10px;
  color: #6f8c96;
}

.side-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  width: 100%;
  padding: 7px;
  border-radius: 8px;
  background: #eaf8fb;
  color: #237c95;
  font-size: 10px;
  font-weight: 650;
  text-decoration: none;
}

.side-note {
  text-align: center;
  color: #6f9aa5;
}

.side-note p {
  margin: 8px 0;
  font-family: var(--font-serif);
  font-size: 11px;
  line-height: 1.8;
  color: #5d818c;
}

.side-note a {
  font-size: 9.5px;
  color: #2f8ba3;
  text-decoration: none;
}

.related-section {
  margin-top: 40px;
}

.related-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
}

@media (max-width: 900px) {
  .article-layout {
    grid-template-columns: 1fr;
  }
  .article-side {
    position: static;
    grid-template-columns: 1fr 1fr;
  }
  .side-box:first-child {
    grid-column: 1 / -1;
  }
  .article-body {
    padding: 30px 28px 24px;
  }
}

@media (max-width: 620px) {
  .article-head {
    padding-top: 88px;
  }
  .article-meta {
    gap: 6px 12px;
  }
  .author-mini {
    border-right: 0;
  }
  .article-layout {
    margin-top: 14px;
  }
  .article-body {
    padding: 24px 18px 20px;
  }
  .prose {
    font-size: 13.5px;
  }
  .prose :deep(h2) {
    font-size: 18px;
  }
  .article-side {
    grid-template-columns: 1fr;
  }
  .author-card {
    flex-direction: column;
  }
  .article-nav,
  .related-grid {
    grid-template-columns: 1fr;
  }
  .reading-bar {
    top: 60px;
  }
}
</style>
