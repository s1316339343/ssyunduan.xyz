<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import {
  ArrowDown,
  ArrowRight,
  BookOpen,
  CalendarDays,
  Eye,
  Github,
  Mail,
  MapPin,
  MessageCircle,
  RefreshCw,
  Sparkles,
} from 'lucide-vue-next'
import PondScene from '../components/PondScene.vue'
import AvatarArt from '../components/AvatarArt.vue'
import ArticleCard from '../components/ArticleCard.vue'
import { api } from '../api/client'
import { isAdmin } from '../stores/auth'
import { siteState, loadSite } from '../stores/site'
import type { Article, Stats } from '../types'
import { formatDate } from '../utils/format'

const site = computed(() => siteState.data)
const articles = ref<Article[]>([])
const stats = ref<Stats | null>(null)
const loading = ref(true)
const typed = ref('')
const typeDone = ref(false)

const quotes = [
  { text: '星光不问赶路人，时光不负有心人。', from: '佚名' },
  { text: '把日子过成诗，把代码写成歌。', from: '云端' },
  { text: '山有峰顶，海有彼岸，漫漫长途，终有回转。', from: '佚名' },
  { text: '愿你在自己的时区里，不紧不慢地发光。', from: '云端' },
  { text: '所有热爱，都值得被认真安放。', from: '云端' },
]
const quoteIndex = ref(0)
const quote = computed(() => quotes[quoteIndex.value])

const fullTitle = '欢迎来到我的小窝'
let typeTimer: number | undefined
let quoteSwitching = false

function nextQuote() {
  if (quoteSwitching) return
  quoteSwitching = true
  quoteIndex.value = (quoteIndex.value + 1) % quotes.length
  window.setTimeout(() => (quoteSwitching = false), 320)
}

function typeTitle() {
  if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
    typed.value = fullTitle
    typeDone.value = true
    return
  }
  let index = 0
  typeTimer = window.setInterval(() => {
    index += 1
    typed.value = fullTitle.slice(0, index)
    if (index >= fullTitle.length) {
      window.clearInterval(typeTimer)
      typeDone.value = true
    }
  }, 145)
}

onMounted(async () => {
  loadSite()
  typeTitle()
  const [articleResult, statResult] = await Promise.allSettled([api.articles(), api.stats()])
  if (articleResult.status === 'fulfilled') articles.value = articleResult.value.slice(0, 3)
  if (statResult.status === 'fulfilled') stats.value = statResult.value
  loading.value = false
})

onUnmounted(() => {
  if (typeTimer) window.clearInterval(typeTimer)
})
</script>

<template>
  <div class="home">
    <section class="hero">
      <PondScene :wave="false" />

      <div class="hero-inner">
        <div class="hero-badge">
          <span class="badge-halo"></span>
          <span class="badge-face">
            <svg viewBox="0 0 64 64" aria-hidden="true">
              <path d="M18 40h28a9 9 0 0 0 .6-18A13.5 13.5 0 0 0 21 21.8 10.2 10.2 0 0 0 18 40Z" fill="#f4fffb" />
              <circle cx="27" cy="31" r="2" fill="#2e6a64" />
              <circle cx="37" cy="31" r="2" fill="#2e6a64" />
              <path d="M28 36c2.5 2 5.5 2 8 0" stroke="#2e6a64" stroke-width="1.7" fill="none" stroke-linecap="round" />
            </svg>
          </span>
          <span class="badge-name">云端</span>
          <i class="orbit-dot d1"></i>
          <i class="orbit-dot d2"></i>
        </div>

        <p class="hero-eyebrow">HI THERE, WELCOME TO MY BLOG</p>

        <h1 class="hero-title">
          <span>{{ typed }}</span><span class="type-cursor" :class="{ done: typeDone }">|</span>
        </h1>

        <p class="hero-sub">
          我是宋爽，网名云端。一个在代码与文字之间来回散步的人，<br class="hide-mobile" />
          把生活里细小的光，慢慢收进这片小窝。
        </p>

        <div class="quote-card">
          <span class="quote-mark">“</span>
          <Transition name="quote" mode="out-in">
            <p :key="quoteIndex" class="quote-text">{{ quote.text }}</p>
          </Transition>
          <div class="quote-bottom">
            <span class="quote-from">— {{ quote.from }}</span>
            <button type="button" class="quote-refresh" aria-label="换一句" @click="nextQuote">
              <RefreshCw :size="13" />
            </button>
          </div>
        </div>

        <a class="scroll-cue" href="#recent">
          <span>向下滚动</span>
          <span class="scroll-line"></span>
          <ArrowDown :size="14" />
        </a>
      </div>

      <div class="hero-side hero-side-left">
        <span class="side-label">{{ site.domain }}</span>
        <span class="side-line"></span>
        <span class="side-year">EST. 2026</span>
      </div>

      <div class="hero-side hero-side-right">
        <a href="https://github.com/" target="_blank" rel="noreferrer noopener" aria-label="GitHub"><Github :size="15" /></a>
        <a :href="`mailto:${site.email}`" aria-label="邮件"><Mail :size="15" /></a>
        <RouterLink to="/guestbook" aria-label="留言板"><MessageCircle :size="15" /></RouterLink>
      </div>

      <svg class="hero-wave" viewBox="0 0 1600 120" preserveAspectRatio="none" aria-hidden="true">
        <path d="M0 66 C150 18 300 70 470 80 C650 90 760 34 940 44 C1120 54 1280 102 1430 88 C1500 82 1550 64 1600 52 L1600 120 L0 120Z" fill="#f7fdff" />
      </svg>
    </section>

    <div class="home-body">
      <section id="recent" class="container recent-section">
        <div class="section-head">
          <div>
            <p class="eyebrow">RECENT WRITING</p>
            <h2>最近在写</h2>
            <p>一些技术笔记，一些生活碎片，都是认真活过的证据。</p>
          </div>
          <RouterLink class="section-link" to="/blog">全部文章 <ArrowRight :size="13" /></RouterLink>
        </div>

        <div v-if="loading" class="recent-grid">
          <div v-for="i in 3" :key="i" class="recent-skeleton glass-card"></div>
        </div>
        <div v-else-if="articles.length" class="recent-grid">
          <ArticleCard v-for="article in articles" :key="article.id" :article="article" layout="tile" />
        </div>
        <div v-else class="empty-state glass-card">
          <p>还没有文章，小窝正在等第一段故事。</p>
          <RouterLink v-if="isAdmin" to="/admin" class="btn btn-primary">去内容管理发布第一篇</RouterLink>
          <span v-else>等站长登录后发布第一篇。</span>
        </div>
      </section>

      <section class="container home-about">
        <div class="about-card glass-card">
          <div class="about-main">
            <AvatarArt :size="104" />
            <div>
              <p class="eyebrow">ABOUT ME</p>
              <h2>{{ site.nickname }} <span>/ {{ site.author }}</span></h2>
              <p class="about-tagline">{{ site.tagline }}</p>
              <p class="about-bio">{{ site.bio }}</p>
              <div class="about-meta">
                <span><MapPin :size="13" /> {{ site.location }}</span>
                <span><CalendarDays :size="13" /> 小窝建于 {{ formatDate(site.startDate) }}</span>
                <span><BookOpen :size="13" /> {{ stats?.articles ?? 0 }} 篇文章</span>
              </div>
            </div>
          </div>
          <div class="about-stats">
            <div><strong>{{ stats?.articles ?? 0 }}</strong><span>文章</span></div>
            <div><strong>{{ stats?.tags ?? 0 }}</strong><span>标签</span></div>
            <div><strong>{{ stats?.categories ?? 0 }}</strong><span>分类</span></div>
            <div><strong>{{ stats?.totalViews ?? 0 }}</strong><span>浏览</span></div>
          </div>
        </div>

        <div class="little-grid">
          <div class="little-card glass-card">
            <span class="little-icon mint"><Sparkles :size="16" /></span>
            <p class="little-label">正在做</p>
            <p class="little-text">把这个小窝一点点搭起来，也把生活一点点整理好。</p>
          </div>
          <div class="little-card glass-card">
            <span class="little-icon butter"><BookOpen :size="16" /></span>
            <p class="little-label">最近在读</p>
            <p class="little-text">《设计中的设计》—— 原来“留白”也是一种表达。</p>
          </div>
          <div class="little-card glass-card">
            <span class="little-icon pink"><Eye :size="16" /></span>
            <p class="little-label">今年想看</p>
            <p class="little-text">一次海边的日落，和一场不用赶时间的旅行。</p>
          </div>
        </div>
      </section>

      <section class="container home-cta">
        <div class="cta-card">
          <div>
            <p class="eyebrow">LEAVE A TRACE</p>
            <h2>既然来了，就坐一会儿吧。</h2>
            <p>留下你的名字，或者一句今天想说的话。云会记得。</p>
          </div>
          <RouterLink class="btn btn-primary" to="/guestbook">
            去留言板 <ArrowRight :size="14" />
          </RouterLink>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.home {
  position: relative;
}

.hero {
  position: relative;
  min-height: 100vh;
  min-height: 100svh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  color: #f4fffb;
}

.hero-inner {
  position: relative;
  z-index: 2;
  width: min(720px, calc(100% - 42px));
  padding: 110px 0 130px;
  text-align: center;
  animation: riseIn 0.9s ease both;
}

.hero-badge {
  position: relative;
  width: 92px;
  height: 92px;
  margin: 0 auto 22px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: rgba(9, 48, 43, 0.4);
  border: 1px solid rgba(217, 255, 240, 0.35);
  box-shadow:
    0 0 0 8px rgba(217, 255, 240, 0.04),
    0 14px 40px rgba(0, 0, 0, 0.22);
  backdrop-filter: blur(10px);
}

.badge-halo {
  position: absolute;
  inset: -18px;
  border-radius: 50%;
  border: 1px solid rgba(198, 255, 236, 0.16);
  animation: pulseRing 4.8s ease-in-out infinite;
}

.badge-face {
  position: absolute;
  top: 12px;
  width: 42px;
  height: 42px;
  opacity: 0.92;
}

.badge-name {
  margin-top: 28px;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 3px;
  color: #f1fffa;
}

.orbit-dot {
  position: absolute;
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: #c9ffe9;
  box-shadow: 0 0 10px #c9ffe9;
}
.orbit-dot.d1 {
  top: 12px;
  right: 15px;
}
.orbit-dot.d2 {
  bottom: 16px;
  left: 11px;
  width: 3px;
  height: 3px;
  opacity: 0.7;
}

.hero-eyebrow {
  margin: 0 0 14px;
  font-family: 'Times New Roman', Georgia, serif;
  font-size: 11px;
  letter-spacing: 6px;
  color: rgba(222, 255, 244, 0.66);
  text-indent: 6px;
}

.hero-title {
  margin: 0;
  min-height: 1.25em;
  font-size: clamp(34px, 5.2vw, 60px);
  line-height: 1.25;
  font-weight: 800;
  letter-spacing: 4px;
  color: #f7fffc;
  text-shadow: 0 6px 40px rgba(0, 0, 0, 0.28);
}

.type-cursor {
  display: inline-block;
  margin-left: 4px;
  font-weight: 300;
  color: rgba(236, 255, 248, 0.8);
  animation: cursorBlink 0.9s step-end infinite;
}

.type-cursor.done {
  animation: cursorBlink 1.1s step-end infinite;
}

.hero-sub {
  margin: 20px auto 0;
  font-size: 13.5px;
  line-height: 2;
  letter-spacing: 0.7px;
  color: rgba(224, 250, 242, 0.78);
}

.quote-card {
  position: relative;
  width: min(530px, 100%);
  margin: 34px auto 0;
  padding: 17px 22px 13px;
  text-align: left;
  border-radius: 16px;
  background: linear-gradient(112deg, rgba(145, 210, 196, 0.32), rgba(255, 255, 255, 0.18));
  border: 1px solid rgba(226, 255, 246, 0.3);
  box-shadow: 0 18px 44px rgba(0, 0, 0, 0.18);
  backdrop-filter: blur(16px);
}

.quote-mark {
  position: absolute;
  top: 6px;
  left: 13px;
  font-family: Georgia, serif;
  font-size: 34px;
  line-height: 1;
  color: rgba(226, 255, 246, 0.32);
}

.quote-text {
  margin: 0;
  padding-left: 12px;
  font-size: 13.5px;
  letter-spacing: 1px;
  color: #f4fffb;
}

.quote-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
  padding-left: 12px;
}

.quote-from {
  font-size: 10.5px;
  letter-spacing: 2px;
  color: rgba(220, 250, 241, 0.55);
}

.quote-refresh {
  width: 26px;
  height: 26px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(232, 255, 249, 0.28);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.12);
  color: rgba(236, 255, 249, 0.8);
  cursor: pointer;
  transition: all 0.25s ease;
}

.quote-refresh:hover {
  transform: rotate(90deg);
  background: rgba(255, 255, 255, 0.2);
}

.quote-enter-active,
.quote-leave-active {
  transition: all 0.25s ease;
}
.quote-enter-from,
.quote-leave-to {
  opacity: 0;
  transform: translateY(5px);
}

.scroll-cue {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  margin-top: 38px;
  font-size: 10px;
  letter-spacing: 2px;
  color: rgba(222, 250, 242, 0.58);
  text-decoration: none;
}

.scroll-line {
  width: 1px;
  height: 24px;
  background: linear-gradient(rgba(224, 255, 245, 0.5), rgba(224, 255, 245, 0));
  position: relative;
  overflow: hidden;
}

.scroll-line::after {
  content: '';
  position: absolute;
  top: -24px;
  left: 0;
  width: 1px;
  height: 18px;
  background: #e7fff7;
  animation: scrollDown 2.2s ease-in-out infinite;
}

.scroll-cue svg {
  opacity: 0.65;
}

.hero-side {
  position: absolute;
  z-index: 2;
  bottom: 42px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 9px;
  letter-spacing: 2.5px;
  color: rgba(223, 251, 242, 0.5);
}

.hero-side-left {
  left: 34px;
}

.hero-side-right {
  right: 34px;
  gap: 14px;
}

.hero-side-right a {
  color: rgba(227, 255, 246, 0.66);
  transition: color 0.2s ease;
}

.hero-side-right a:hover {
  color: #fff;
}

.side-line {
  width: 34px;
  height: 1px;
  background: rgba(221, 252, 241, 0.24);
}

.hero-wave {
  position: absolute;
  left: 0;
  right: 0;
  bottom: -1px;
  z-index: 3;
  width: 100%;
  height: 92px;
  pointer-events: none;
}

.home-body {
  position: relative;
  padding-top: 10px;
  background: linear-gradient(180deg, #f7fdff 0, rgba(247, 253, 255, 0.86) 120px, rgba(247, 253, 255, 0) 380px);
}

.recent-section {
  padding-top: 26px;
}

.recent-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
}

.recent-skeleton {
  height: 310px;
  animation: shimmer 1.6s ease-in-out infinite;
}

.home-about {
  margin-top: 54px;
}

.about-card {
  display: grid;
  grid-template-columns: 1.55fr 1fr;
  gap: 26px;
  padding: 30px 32px;
}

.about-main {
  display: flex;
  align-items: flex-start;
  gap: 22px;
}

.about-main h2 {
  margin: 5px 0 5px;
  font-size: 23px;
  letter-spacing: 1px;
  color: #174653;
}

.about-main h2 span {
  font-size: 12px;
  font-weight: 500;
  color: #7c99a3;
  letter-spacing: 1px;
}

.about-tagline {
  margin: 0 0 8px;
  font-family: var(--font-serif);
  font-size: 13px;
  letter-spacing: 1px;
  color: #3c7d8e;
}

.about-bio {
  margin: 0;
  max-width: 480px;
  font-size: 12px;
  line-height: 1.85;
  color: #65818b;
}

.about-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 6px 14px;
  margin-top: 13px;
  font-size: 10px;
  color: #8ba2aa;
}

.about-meta span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.about-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  align-content: center;
}

.about-stats div {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  min-height: 70px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.55);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.about-stats strong {
  font-size: 22px;
  line-height: 1;
  color: #1d6f86;
  font-weight: 750;
}

.about-stats span {
  font-size: 9.5px;
  color: #8ba2aa;
  letter-spacing: 1px;
}

.little-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-top: 16px;
}

.little-card {
  padding: 19px 20px;
  transition: transform 0.25s ease;
}

.little-card:hover {
  transform: translateY(-3px);
}

.little-icon {
  width: 32px;
  height: 32px;
  display: grid;
  place-items: center;
  border-radius: 10px;
  margin-bottom: 10px;
}

.little-icon.mint {
  background: #dff6ef;
  color: #2f9b83;
}
.little-icon.butter {
  background: #fff4d8;
  color: #b98a2e;
}
.little-icon.pink {
  background: #ffe7ec;
  color: #d37188;
}

.little-label {
  margin: 0 0 3px;
  font-size: 10px;
  letter-spacing: 2px;
  color: #8ba2aa;
}

.little-text {
  margin: 0;
  font-size: 11.5px;
  line-height: 1.8;
  color: #5c7b86;
}

.home-cta {
  margin-top: 26px;
}

.cta-card {
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 26px;
  padding: 32px 36px;
  border-radius: 24px;
  color: #f6fffc;
  background:
    radial-gradient(circle at 82% 18%, rgba(255, 255, 255, 0.24), transparent 30%),
    linear-gradient(120deg, #14657a, #2f9db4 58%, #76ccd2);
  box-shadow: 0 22px 54px rgba(21, 104, 126, 0.24);
}

.cta-card::after {
  content: '';
  position: absolute;
  right: -40px;
  bottom: -80px;
  width: 260px;
  height: 260px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.14);
  box-shadow:
    0 0 0 34px rgba(255, 255, 255, 0.05),
    0 0 0 72px rgba(255, 255, 255, 0.03);
}

.cta-card .eyebrow {
  color: rgba(230, 255, 250, 0.6);
}

.cta-card h2 {
  margin: 6px 0 5px;
  font-size: 22px;
  letter-spacing: 1px;
}

.cta-card p {
  margin: 0;
  font-size: 12px;
  color: rgba(233, 255, 250, 0.75);
}

.cta-card .btn {
  flex: none;
  background: rgba(255, 255, 255, 0.94);
  color: #14647b;
  box-shadow: 0 12px 26px rgba(0, 0, 0, 0.14);
}

@keyframes cursorBlink {
  0%,
  49% {
    opacity: 1;
  }
  50%,
  100% {
    opacity: 0;
  }
}

@keyframes pulseRing {
  0%,
  100% {
    transform: scale(1);
    opacity: 0.6;
  }
  50% {
    transform: scale(1.06);
    opacity: 0.2;
  }
}

@keyframes scrollDown {
  0% {
    top: -24px;
    opacity: 0;
  }
  30% {
    opacity: 0.9;
  }
  100% {
    top: 32px;
    opacity: 0;
  }
}

@keyframes shimmer {
  0%,
  100% {
    opacity: 0.55;
  }
  50% {
    opacity: 0.9;
  }
}

@media (max-width: 900px) {
  .recent-grid {
    grid-template-columns: 1fr 1fr;
  }
  .recent-grid > :last-child {
    display: none;
  }
  .about-card {
    grid-template-columns: 1fr;
  }
  .about-stats {
    grid-template-columns: repeat(4, 1fr);
  }
  .little-grid {
    grid-template-columns: 1fr;
  }
  .hero-side {
    display: none;
  }
}

@media (max-width: 620px) {
  .hero-inner {
    padding-top: 94px;
    padding-bottom: 110px;
  }
  .hero-title {
    letter-spacing: 2px;
  }
  .hero-eyebrow {
    font-size: 9px;
    letter-spacing: 3px;
    text-indent: 3px;
  }
  .hero-sub {
    font-size: 12.5px;
  }
  .hide-mobile {
    display: none;
  }
  .quote-card {
    margin-top: 26px;
  }
  .recent-grid {
    grid-template-columns: 1fr;
  }
  .recent-grid > :last-child {
    display: block;
  }
  .about-card {
    padding: 22px;
  }
  .about-main {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  .about-meta {
    justify-content: center;
  }
  .about-stats {
    grid-template-columns: 1fr 1fr;
  }
  .cta-card {
    flex-direction: column;
    align-items: flex-start;
    padding: 26px;
  }
}
</style>
