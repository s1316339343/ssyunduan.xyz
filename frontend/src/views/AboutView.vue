<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import {
  ArrowUpRight,
  BookOpen,
  Code2,
  Coffee,
  Github,
  Heart,
  Mail,
  MapPin,
  MessageCircle,
  Mountain,
  Sparkles,
} from 'lucide-vue-next'
import AvatarArt from '../components/AvatarArt.vue'
import { api } from '../api/client'
import { siteState } from '../stores/site'
import type { Stats } from '../types'
import { formatDate, formatNumber } from '../utils/format'

const site = computed(() => siteState.data)
const stats = ref<Stats | null>(null)

const floating = [
  { text: '写代码', class: 'n1', color: 'mint' },
  { text: '拍云', class: 'n2', color: 'blue' },
  { text: '读闲书', class: 'n3', color: 'butter' },
  { text: '咖啡续命', class: 'n4', color: 'pink' },
  { text: '慢慢来', class: 'n5', color: 'violet' },
  { text: '正在学习', class: 'n6', color: 'mint' },
]

const visitItems = computed(() => [
  { label: '今日访客', value: stats.value?.todayVisits ?? 0 },
  { label: '今日浏览', value: stats.value?.todayViews ?? 0 },
  { label: '昨日访客', value: Math.max(0, (stats.value?.monthVisits ?? 0) - (stats.value?.todayVisits ?? 0)) },
  { label: '本月浏览', value: stats.value?.monthViews ?? 0 },
  { label: '总访客', value: stats.value?.totalVisits ?? 0 },
  { label: '总浏览', value: stats.value?.totalViews ?? 0 },
])

const traits = [
  { label: '身份', value: '开发者 / 写作者' },
  { label: '方向', value: 'Web 前端 & 后端' },
  { label: '坐标', value: site.value.location },
  { label: '状态', value: '持续学习中' },
  { label: '爱好', value: '摄影 · 阅读 · 发呆' },
  { label: '信条', value: '慢慢来，比较快' },
]

onMounted(async () => {
  try {
    stats.value = await api.stats()
  } catch {
    /* 页面其余部分仍可浏览 */
  }
})
</script>

<template>
  <div class="page about-page">
    <div class="container">
      <section class="about-hero">
        <span
          v-for="item in floating"
          :key="item.class"
          class="float-note"
          :class="[item.class, item.color]"
        >
          {{ item.text }}
        </span>

        <div class="hero-avatar">
          <span class="avatar-ring r1"></span>
          <span class="avatar-ring r2"></span>
          <AvatarArt :size="158" />
          <span class="avatar-badge"><Sparkles :size="12" /> 云端</span>
        </div>

        <p class="eyebrow">ABOUT ME</p>
        <h1>你好，我是宋爽，<br />也是「云端」。</h1>
        <p class="hero-desc">
          一个在代码与文字之间来回散步的人。喜欢把复杂的事情拆小，<br class="hide-mobile" />
          也喜欢把普通的日子，过得稍微认真一点。
        </p>
        <div class="hero-chips">
          <span><Code2 :size="12" /> Vue 3 / Spring Boot</span>
          <span><Mountain :size="12" /> 山野与云</span>
          <span><Coffee :size="12" /> 咖啡与长夜</span>
        </div>
      </section>

      <section class="intro-grid">
        <div class="name-card">
          <div class="name-card-top">
            <span class="name-tag">网名</span>
            <span class="name-spark">✦</span>
          </div>
          <h2>{{ site.nickname }}<span> / {{ site.author }}</span></h2>
          <p class="name-line">有了想做的事，就慢慢去做。</p>
          <p class="name-bio">
            我是宋爽，网名云端。这里是我的个人小站，记录技术、生活，以及那些不值得发朋友圈、却又舍不得忘记的小事。
          </p>
          <div class="name-meta">
            <span><MapPin :size="12" /> {{ site.location }}</span>
            <span><BookOpen :size="12" /> 小窝建于 {{ formatDate(site.startDate) }}</span>
            <span><Heart :size="12" /> 保持热爱</span>
          </div>
        </div>

        <div class="pursuit-card">
          <p class="pursuit-label">追求</p>
          <h3>源于<br /><b>乐趣 · 兴趣 · 学习</b></h3>
          <p>
            不为了成为谁，只想把喜欢的事做得久一点。写代码是，写字也是。
          </p>
          <span class="pursuit-line"></span>
          <span class="pursuit-mark">“</span>
        </div>
      </section>

      <section class="about-grid">
        <div class="visit-card">
          <header class="card-head">
            <div>
              <p class="card-eyebrow">VISITORS</p>
              <h3>访问统计</h3>
            </div>
            <span class="head-dot"><i></i> 实时</span>
          </header>
          <div class="visit-grid">
            <div v-for="item in visitItems" :key="item.label">
              <strong>{{ formatNumber(item.value) }}</strong>
              <span>{{ item.label }}</span>
            </div>
          </div>
          <div class="visit-foot">
            <span>在这个不大的地方，已经留下了 {{ formatNumber(stats?.totalViews ?? 0) }} 次目光。</span>
          </div>
        </div>

        <div class="map-card">
          <div class="map-grid"></div>
          <svg class="map-lines" viewBox="0 0 600 280" preserveAspectRatio="none" aria-hidden="true">
            <path d="M-20 210 C80 150 170 190 240 140 C320 82 400 120 470 80 C530 46 580 60 620 40" fill="none" stroke="rgba(63, 145, 160, 0.2)" stroke-width="2" />
            <path d="M-10 250 C100 220 180 240 270 200 C360 160 440 190 520 150 C570 126 600 132 620 120" fill="none" stroke="rgba(63, 145, 160, 0.13)" stroke-width="1.5" />
            <path d="M120 300 C150 220 140 160 200 100 C250 50 330 70 380 20" fill="none" stroke="rgba(63, 145, 160, 0.1)" stroke-width="1.5" />
          </svg>
          <span class="map-pin"><i></i></span>
          <div class="map-copy">
            <p class="map-label">WHERE I LIVE</p>
            <h3>我住在 {{ site.location }}</h3>
            <p>云很远，日子很近。欢迎来我的城市看一次日落。</p>
          </div>
          <span class="map-coord">CLOUD · EARTH</span>
        </div>
      </section>

      <section class="traits-section">
        <div class="traits-card glass-card">
          <header class="traits-head">
            <p class="eyebrow">SOME FACTS</p>
            <h3>关于我的几个关键词</h3>
          </header>
          <div class="traits-grid">
            <div v-for="trait in traits" :key="trait.label" class="trait-item">
              <span>{{ trait.label }}</span>
              <p>{{ trait.value }}</p>
            </div>
          </div>
        </div>

        <div class="site-card glass-card">
          <header class="traits-head">
            <p class="eyebrow">THIS SITE</p>
            <h3>关于这个小窝</h3>
          </header>
          <p class="site-desc">
            前端用 Vue 3 + TypeScript，后端用 Spring Boot，数据存放在本地 H2 数据库。没有复杂的框架堆叠，只想要一个干净、好维护、能慢慢长大的个人站。
          </p>
          <div class="stack">
            <span v-for="tech in site.techStack" :key="tech">{{ tech }}</span>
          </div>
          <ul class="site-facts">
            <li><span>域名</span><b>{{ site.domain }}</b></li>
            <li><span>上线</span><b>{{ formatDate(site.startDate) }}</b></li>
            <li><span>插画</span><b>原创 SVG 绘制</b></li>
            <li><span>备案</span><b>{{ site.icp }}</b></li>
          </ul>
        </div>
      </section>

      <section class="contact-card">
        <div>
          <p class="eyebrow">SAY HELLO</p>
          <h2>如果你也喜欢云、文字和代码，<br />那我们大概会成为朋友。</h2>
          <p>欢迎写信，或者去留言板坐坐。看到都会认真回复。</p>
        </div>
        <div class="contact-actions">
          <a class="contact-btn" :href="`mailto:${site.email}`"><Mail :size="15" /> 写邮件</a>
          <a class="contact-btn" :href="site.github || '#'" target="_blank" rel="noreferrer noopener"><Github :size="15" /> GitHub</a>
          <RouterLink class="contact-btn primary" to="/guestbook"><MessageCircle :size="15" /> 留言板 <ArrowUpRight :size="12" /></RouterLink>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.about-page {
  padding-bottom: 50px;
}

.about-hero {
  position: relative;
  min-height: 560px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 112px 0 30px;
}

.hero-avatar {
  position: relative;
  margin-bottom: 20px;
}

.avatar-ring {
  position: absolute;
  left: 50%;
  top: 50%;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.55);
  transform: translate(-50%, -50%);
}

.avatar-ring.r1 {
  width: 196px;
  height: 196px;
}
.avatar-ring.r2 {
  width: 232px;
  height: 232px;
  opacity: 0.55;
}

.avatar-badge {
  position: absolute;
  left: 50%;
  bottom: -6px;
  transform: translateX(-50%);
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.9);
  color: #2e7185;
  font-size: 9px;
  font-weight: 700;
  letter-spacing: 1px;
  box-shadow: 0 6px 16px rgba(31, 99, 120, 0.16);
}

.float-note {
  position: absolute;
  z-index: 2;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 9.5px;
  font-weight: 600;
  letter-spacing: 0.5px;
  box-shadow: 0 8px 20px rgba(31, 99, 120, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.85);
  animation: floatNote 6s ease-in-out infinite;
}

.float-note.mint {
  background: rgba(226, 249, 243, 0.9);
  color: #2e9079;
}
.float-note.blue {
  background: rgba(229, 245, 252, 0.92);
  color: #317f9e;
}
.float-note.butter {
  background: rgba(255, 246, 218, 0.92);
  color: #a9832f;
}
.float-note.pink {
  background: rgba(255, 235, 240, 0.92);
  color: #bd6b80;
}
.float-note.violet {
  background: rgba(242, 238, 252, 0.92);
  color: #7c6ab3;
}

.float-note.n1 {
  top: 150px;
  left: 22%;
}
.float-note.n2 {
  top: 210px;
  right: 18%;
  animation-delay: 1.2s;
}
.float-note.n3 {
  top: 320px;
  left: 11%;
  animation-delay: 2.1s;
}
.float-note.n4 {
  top: 130px;
  right: 25%;
  animation-delay: 0.7s;
}
.float-note.n5 {
  top: 360px;
  right: 12%;
  animation-delay: 1.7s;
}
.float-note.n6 {
  top: 260px;
  left: 5%;
  animation-delay: 2.7s;
}

.about-hero h1 {
  margin: 7px 0 10px;
  font-size: clamp(30px, 4.2vw, 46px);
  line-height: 1.4;
  letter-spacing: 3px;
  color: #123f4d;
  text-shadow: 0 2px 20px rgba(255, 255, 255, 0.6);
}

.hero-desc {
  margin: 0;
  font-size: 12.5px;
  line-height: 2;
  color: #537681;
}

.hero-chips {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
  margin-top: 18px;
}

.hero-chips span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 5px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.55);
  border: 1px solid rgba(255, 255, 255, 0.8);
  font-size: 9px;
  color: #5d8793;
}

.intro-grid {
  display: grid;
  grid-template-columns: 1.35fr 0.65fr;
  gap: 16px;
}

.name-card {
  position: relative;
  overflow: hidden;
  padding: 30px 32px;
  border-radius: 22px;
  color: #f3fffc;
  background:
    radial-gradient(circle at 84% 18%, rgba(255, 255, 255, 0.22), transparent 30%),
    linear-gradient(125deg, #2382a2, #3fb2c8 55%, #8bdde0);
  box-shadow: 0 20px 50px rgba(29, 122, 151, 0.22);
}

.name-card::after {
  content: '';
  position: absolute;
  right: -60px;
  bottom: -100px;
  width: 280px;
  height: 280px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.16);
  box-shadow:
    0 0 0 36px rgba(255, 255, 255, 0.05),
    0 0 0 78px rgba(255, 255, 255, 0.03);
}

.name-card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.name-tag {
  font-size: 9px;
  letter-spacing: 2px;
  color: rgba(236, 255, 251, 0.66);
}

.name-spark {
  color: rgba(255, 255, 255, 0.7);
  font-size: 13px;
}

.name-card h2 {
  margin: 9px 0 4px;
  font-size: 29px;
  letter-spacing: 2px;
}

.name-card h2 span {
  font-size: 12px;
  font-weight: 400;
  letter-spacing: 1px;
  color: rgba(236, 255, 251, 0.62);
}

.name-line {
  margin: 0 0 9px;
  font-family: var(--font-serif);
  font-size: 12px;
  letter-spacing: 1.5px;
  color: rgba(240, 255, 252, 0.8);
}

.name-bio {
  position: relative;
  z-index: 2;
  max-width: 480px;
  margin: 0;
  font-size: 11.5px;
  line-height: 1.9;
  color: rgba(234, 255, 250, 0.78);
}

.name-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 7px 16px;
  margin-top: 20px;
  font-size: 9px;
  color: rgba(232, 255, 250, 0.62);
}

.name-meta span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.pursuit-card {
  position: relative;
  padding: 26px 28px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(255, 255, 255, 0.92);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.pursuit-label {
  margin: 0 0 6px;
  font-size: 9px;
  letter-spacing: 2px;
  color: #95a9b0;
}

.pursuit-card h3 {
  margin: 0 0 10px;
  font-size: 23px;
  line-height: 1.5;
  font-weight: 500;
  color: #3d6571;
  letter-spacing: 1px;
}

.pursuit-card h3 b {
  color: #df6f6a;
  font-weight: 750;
}

.pursuit-card p {
  margin: 0;
  max-width: 230px;
  font-size: 10.5px;
  line-height: 1.8;
  color: #829aa2;
}

.pursuit-line {
  display: block;
  width: 36px;
  height: 2px;
  margin-top: 18px;
  background: #edb1a9;
}

.pursuit-mark {
  position: absolute;
  right: 12px;
  bottom: -30px;
  font-family: Georgia, serif;
  font-size: 130px;
  line-height: 1;
  color: rgba(223, 111, 106, 0.08);
}

.about-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-top: 16px;
}

.visit-card {
  padding: 24px 26px;
  border-radius: 20px;
  background: linear-gradient(140deg, rgba(209, 242, 250, 0.9), rgba(255, 255, 255, 0.76));
  border: 1px solid rgba(255, 255, 255, 0.85);
  box-shadow: var(--shadow-sm);
}

.card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.card-eyebrow {
  margin: 0;
  font-family: 'Times New Roman', serif;
  font-size: 8px;
  letter-spacing: 2px;
  color: #7da4ae;
}

.card-head h3 {
  margin: 3px 0 0;
  font-size: 16px;
  color: #235666;
}

.head-dot {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 8.5px;
  color: #4ba08a;
}

.head-dot i {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: #49b78f;
}

.visit-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px 8px;
  margin: 18px 0 14px;
}

.visit-grid strong {
  display: block;
  font-size: 21px;
  line-height: 1.1;
  color: #1d6c82;
  font-weight: 780;
}

.visit-grid span {
  font-size: 8.5px;
  color: #8ba2aa;
}

.visit-foot {
  padding-top: 10px;
  border-top: 1px dashed rgba(48, 112, 131, 0.16);
  font-size: 9px;
  color: #91a7ae;
}

.map-card {
  position: relative;
  overflow: hidden;
  min-height: 220px;
  border-radius: 20px;
  background: linear-gradient(135deg, #d7f0f4, #a9dde8 55%, #8dcfdd);
  border: 1px solid rgba(255, 255, 255, 0.85);
  box-shadow: var(--shadow-sm);
}

.map-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.28) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.28) 1px, transparent 1px);
  background-size: 34px 34px;
  opacity: 0.55;
}

.map-lines {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.map-pin {
  position: absolute;
  right: 26%;
  top: 36%;
  width: 38px;
  height: 38px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.28);
  border: 1px solid rgba(255, 255, 255, 0.6);
  animation: pinPulse 2.6s ease-in-out infinite;
}

.map-pin i {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #f27f8e;
  box-shadow: 0 0 0 4px rgba(242, 127, 142, 0.25);
}

.map-copy {
  position: absolute;
  left: 24px;
  bottom: 20px;
  z-index: 2;
}

.map-label {
  margin: 0 0 3px;
  font-family: 'Times New Roman', serif;
  font-size: 8px;
  letter-spacing: 2px;
  color: rgba(32, 92, 105, 0.55);
}

.map-copy h3 {
  margin: 0 0 3px;
  font-size: 16px;
  color: #1d5a68;
}

.map-copy p:last-child {
  margin: 0;
  max-width: 220px;
  font-size: 9px;
  line-height: 1.7;
  color: rgba(38, 93, 105, 0.72);
}

.map-coord {
  position: absolute;
  top: 16px;
  right: 18px;
  font-family: 'Times New Roman', serif;
  font-size: 8px;
  letter-spacing: 1.5px;
  color: rgba(32, 92, 105, 0.5);
}

.traits-section {
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 16px;
  margin-top: 16px;
}

.traits-card,
.site-card {
  padding: 24px 26px;
}

.traits-head h3 {
  margin: 4px 0 14px;
  font-size: 16px;
  color: #245563;
}

.traits-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px 16px;
}

.trait-item {
  padding: 9px 12px;
  border-radius: 10px;
  background: rgba(246, 252, 253, 0.8);
  border: 1px solid rgba(227, 243, 247, 0.9);
}

.trait-item span {
  display: block;
  font-size: 8.5px;
  letter-spacing: 1px;
  color: #9aadb3;
}

.trait-item p {
  margin: 2px 0 0;
  font-size: 11px;
  font-weight: 600;
  color: #3c6875;
}

.site-desc {
  margin: 0 0 12px;
  font-size: 10.5px;
  line-height: 1.9;
  color: #6f8a93;
}

.stack {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  margin-bottom: 14px;
}

.stack span {
  padding: 4px 8px;
  border-radius: 6px;
  background: #eaf8fb;
  color: #2e8198;
  font-size: 8.5px;
  font-weight: 650;
}

.site-facts {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 5px;
}

.site-facts li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding-bottom: 5px;
  border-bottom: 1px dashed rgba(48, 112, 131, 0.1);
  font-size: 9px;
}

.site-facts span {
  color: #96aab1;
}

.site-facts b {
  font-weight: 600;
  color: #4d7581;
}

.contact-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 26px;
  margin-top: 18px;
  padding: 30px 34px;
  border-radius: 22px;
  background: linear-gradient(120deg, rgba(255, 255, 255, 0.78), rgba(233, 249, 251, 0.78));
  border: 1px solid rgba(255, 255, 255, 0.88);
  box-shadow: var(--shadow-sm);
}

.contact-card h2 {
  margin: 6px 0 5px;
  font-size: 21px;
  line-height: 1.5;
  letter-spacing: 1px;
  color: #194b5a;
}

.contact-card p:last-child {
  margin: 0;
  font-size: 10.5px;
  color: #819aa3;
}

.contact-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  flex: none;
}

.contact-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 9px 14px;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.7);
  color: #386c7b;
  font-size: 10.5px;
  font-weight: 650;
  text-decoration: none;
  transition: all 0.2s ease;
}

.contact-btn:hover {
  background: #fff;
  transform: translateY(-1px);
}

.contact-btn.primary {
  background: #2fa8c9;
  border-color: #2fa8c9;
  color: #fff;
  box-shadow: 0 10px 22px rgba(31, 133, 162, 0.22);
}

@keyframes floatNote {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-7px);
  }
}

@keyframes pinPulse {
  0%,
  100% {
    transform: scale(1);
    opacity: 0.95;
  }
  50% {
    transform: scale(1.12);
    opacity: 0.7;
  }
}

@media (max-width: 900px) {
  .intro-grid,
  .about-grid,
  .traits-section {
    grid-template-columns: 1fr;
  }
  .float-note.n1,
  .float-note.n3,
  .float-note.n6 {
    left: 3%;
  }
  .float-note.n2,
  .float-note.n5 {
    right: 3%;
  }
  .contact-card {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 620px) {
  .about-hero {
    min-height: 520px;
    padding-top: 90px;
  }
  .float-note {
    font-size: 8px;
  }
  .float-note.n1 {
    top: 152px;
    left: 3%;
  }
  .float-note.n2 {
    top: 208px;
    right: 3%;
  }
  .float-note.n3,
  .float-note.n4,
  .float-note.n5,
  .float-note.n6 {
    display: none;
  }
  .about-hero h1 {
    font-size: 28px;
    letter-spacing: 2px;
  }
  .hide-mobile {
    display: none;
  }
  .name-card,
  .pursuit-card {
    padding: 22px;
  }
  .visit-grid {
    grid-template-columns: 1fr 1fr;
  }
  .traits-grid {
    grid-template-columns: 1fr;
  }
  .contact-card {
    padding: 24px 20px;
  }
  .contact-card h2 {
    font-size: 18px;
  }
}
</style>
