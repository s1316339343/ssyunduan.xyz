<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import {
  Award,
  BookOpen,
  Code2,
  GraduationCap,
  Heart,
  Mail,
  MapPin,
  Phone,
  Sparkles,
  Target,
} from 'lucide-vue-next'
import PageHeader from '../components/PageHeader.vue'
import AvatarArt from '../components/AvatarArt.vue'
import { api } from '../api/client'
import { siteState } from '../stores/site'
import type { AboutData, AboutProfile, Honor } from '../types'
import { formatDate } from '../utils/format'

const fallbackProfile: AboutProfile = {
  name: '宋爽',
  nickname: '云端',
  birthDate: '2003-10-14',
  hometown: '河北唐山',
  phone: '18731513039',
  email: '1316339343@qq.com',
  status: '沧州交通学院 · 物联网工程 · 本科在读',
  intro:
    '本人为沧州交通学院物联网工程专业本科在读生，具备专升本学习背景，学习目标清晰、执行力强，长期规划以人工智能方向深造为核心，对智能体、大模型上层应用领域抱有浓厚热情。在校期间主动夯实计算机基础，备考计算机二级、四级以及 408 相关课程；积极参与学科竞赛，曾获蓝桥杯省赛二等奖，同时自主开展项目实践，先后独立开发鸿蒙美食推荐应用、校园论坛、个人静态博客，尝试本地大模型部署，擅长结合 AI 能力落地实用应用，偏向智能应用开发。',
  goal: '深耕人工智能与智能体应用，把想法持续转化为可运行的产品。',
  collegeSchool: '河北工业职业技术大学',
  collegeMajor: '移动应用开发',
  collegePeriod: '2023.09 - 2026.06',
  collegeCourses: 'Vue、HTML、CSS、JavaScript、Web 前端开发、小程序开发',
  collegeExperience: '参加校园内部前端实战班，重点学习前端知识，提升项目实战经验；在班级里学习成绩优异，实训项目完成度优秀。',
  bachelorSchool: '沧州交通学院',
  bachelorMajor: '物联网工程',
  bachelorPeriod: '2026.09 - 至今',
  bachelorCourses: '主修课程与校内经历暂无',
  bachelorExperience: '本科在读，正在继续补充物联网工程与人工智能相关基础。',
  skills: 'Vue 3、TypeScript、Spring Boot、鸿蒙应用开发、AI 应用实践、项目复盘',
}

const fallbackHonors: Honor[] = [
  { id: 1, title: '华为开发者学堂专业证书', issuer: '华为开发者联盟', awardedAt: '2024-02-27', imageUrl: '/honors/huawei.jpg', description: 'HarmonyOS 应用开发者基础认证。', priority: 100, visible: true },
  { id: 2, title: '阿里云 Apsara Clouder 专项技能认证', issuer: '阿里云', awardedAt: '2024-06-17', imageUrl: '/honors/aliyun.jpg', description: '云数据库 Clouder 认证：云数据库 RDS 快速入门。', priority: 90, visible: true },
  { id: 3, title: 'AI 驱动的 TDSQL-C Serverless 数据库技术实战营结业证书', issuer: '腾讯云开发者社区 / 腾讯云数据库 / CSDN', awardedAt: '2026-10', imageUrl: '/honors/tdsql.jpg', description: '完成 AI 驱动的 TDSQL-C Serverless 数据库技术实战营。', priority: 80, visible: true },
  { id: 4, title: '第 16 届蓝桥杯 Web 应用开发河北省赛二等奖', issuer: '工业和信息化部人才交流中心 / 蓝桥杯大赛组委会', awardedAt: '2025-05-26', imageUrl: '/honors/lanqiao.jpg', description: '全国软件和信息技术专业人才大赛河北赛区 Web 应用开发职业院校组二等奖。', priority: 70, visible: true },
  { id: 5, title: '2024 年百度商业 AI 技术创新大赛北部赛区二等奖', issuer: '中国人工智能学会 / 百度商业 / NVIDIA / 飞桨', awardedAt: '2024-08', imageUrl: '/honors/baidu.jpg', description: '行业智能体搭建赛道，所在团队「慢走不宋先生队」获北部赛区二等奖。', priority: 60, visible: true },
  { id: 6, title: '文心智能体 DeepSeek 挑战赛一等奖', issuer: '文心智能体', awardedAt: '2025', imageUrl: null, description: 'DeepSeek 挑战赛一等奖（暂无证书图片）。', priority: 50, visible: true },
]

const about = ref<AboutData>({ profile: fallbackProfile, honors: fallbackHonors })
const site = computed(() => siteState.data)
const profile = computed(() => about.value.profile || fallbackProfile)
const honors = computed(() =>
  [...(about.value.honors?.length ? about.value.honors : fallbackHonors)]
    .filter((honor) => honor.visible)
    .sort((a, b) => b.priority - a.priority || a.id - b.id),
)
const skillList = computed(() =>
  (profile.value.skills || '')
    .split(/[、,，]/)
    .map((item) => item.trim())
    .filter(Boolean),
)
const courses = computed(() =>
  (profile.value.collegeCourses || '')
    .split(/[、,，]/)
    .map((item) => item.trim())
    .filter(Boolean),
)

const BASE = import.meta.env.BASE_URL.endsWith('/') ? import.meta.env.BASE_URL : `${import.meta.env.BASE_URL}/`

function assetUrl(url: string | null | undefined): string {
  if (!url) return ''
  if (/^https?:\/\//.test(url)) return url
  return `${BASE}${url.replace(/^\//, '')}`
}

function honorsDate(value: string): string {
  if (!value) return ''
  return value.includes('-') ? formatDate(value) : value
}

onMounted(async () => {
  try {
    about.value = await api.about()
  } catch {
    /* 保留简历兜底，页面仍可浏览 */
  }
})
</script>

<template>
  <div class="page about-page">
    <div class="container">
      <PageHeader
        eyebrow="ABOUT ME"
        title="关于我"
        subtitle="在代码、文字与人工智能之间，慢慢把想法做成可以运行的东西。"
      />

      <section class="about-hero glass-card">
        <div class="hero-avatar">
          <AvatarArt :size="132" />
          <span class="avatar-badge"><Sparkles :size="11" /> 云端</span>
        </div>
        <div class="hero-copy">
          <p class="hero-kicker">HELLO, I AM 宋爽</p>
          <h1>{{ profile.name }} <span>/ {{ profile.nickname }}</span></h1>
          <p class="hero-status">{{ profile.status }}</p>
          <p class="hero-intro">{{ profile.intro }}</p>
          <div class="hero-meta">
            <span><MapPin :size="12" /> {{ profile.hometown }}</span>
            <span><BookOpen :size="12" /> 小窝建于 {{ formatDate(site.startDate) }}</span>
            <span><Heart :size="12" /> 慢慢来，比较快</span>
          </div>
          <div class="hero-chips">
            <span v-for="skill in skillList" :key="skill">{{ skill }}</span>
          </div>
        </div>
      </section>

      <section class="about-grid">
        <article class="info-card glass-card">
          <span class="card-icon"><Target :size="18" /></span>
          <h2>我的方向</h2>
          <p>{{ profile.goal }}</p>
          <p class="muted-line">长期关注人工智能、智能体与大模型上层应用，把 AI 能力落到实际可用的产品里。</p>
        </article>
        <article class="info-card glass-card">
          <span class="card-icon"><Code2 :size="18" /></span>
          <h2>技术关键词</h2>
          <div class="tag-list">
            <span v-for="skill in skillList" :key="skill">{{ skill }}</span>
          </div>
          <p class="muted-line">前端、后端、移动端都做过；比起堆技术，更在意一个项目能不能完整跑起来。</p>
        </article>
      </section>

      <section class="section-block">
        <div class="section-head">
          <p class="eyebrow">EDUCATION</p>
          <h2><GraduationCap :size="20" /> 教育背景</h2>
        </div>
        <div class="timeline">
          <article class="timeline-item glass-card">
            <div class="timeline-dot"></div>
            <div class="timeline-head">
              <div>
                <h3>{{ profile.bachelorSchool }}</h3>
                <p>{{ profile.bachelorMajor }}</p>
              </div>
              <span>{{ profile.bachelorPeriod }}</span>
            </div>
            <p class="timeline-text">{{ profile.bachelorCourses }}</p>
            <p class="timeline-text">{{ profile.bachelorExperience }}</p>
          </article>

          <article class="timeline-item glass-card">
            <div class="timeline-dot"></div>
            <div class="timeline-head">
              <div>
                <h3>{{ profile.collegeSchool }}</h3>
                <p>{{ profile.collegeMajor }}</p>
              </div>
              <span>{{ profile.collegePeriod }}</span>
            </div>
            <p class="timeline-text">主修课程：{{ profile.collegeCourses }}</p>
            <div class="course-tags">
              <span v-for="course in courses" :key="course">{{ course }}</span>
            </div>
            <p class="timeline-text">{{ profile.collegeExperience }}</p>
          </article>
        </div>
      </section>

      <section class="section-block">
        <div class="section-head">
          <p class="eyebrow">HONORS &amp; CERTIFICATES</p>
          <h2><Award :size="20" /> 荣誉与证书</h2>
          <p class="section-desc">按照展示优先级排列，未展示的荣誉不会出现在这里。</p>
        </div>

        <div v-if="honors.length" class="honor-grid">
          <article v-for="honor in honors" :key="honor.id" class="honor-card glass-card">
            <a
              v-if="honor.imageUrl"
              :href="assetUrl(honor.imageUrl)"
              target="_blank"
              rel="noreferrer noopener"
              class="honor-image"
              :aria-label="`查看${honor.title}证书大图`"
            >
              <img :src="assetUrl(honor.imageUrl)" :alt="honor.title" loading="lazy" />
              <span class="honor-image-hint">点击查看大图</span>
            </a>
            <div v-else class="honor-image honor-placeholder">
              <Award :size="38" />
              <span>暂无证书图片</span>
            </div>
            <div class="honor-copy">
              <span class="honor-date">{{ honorsDate(honor.awardedAt) }}</span>
              <h3>{{ honor.title }}</h3>
              <p class="honor-issuer">{{ honor.issuer }}</p>
              <p v-if="honor.description" class="honor-desc">{{ honor.description }}</p>
            </div>
          </article>
        </div>
        <div v-else class="empty-state glass-card">荣誉正在整理中。</div>
      </section>

      <section class="contact-card glass-card">
        <div>
          <p class="eyebrow">SAY HELLO</p>
          <h2>如果你也在做有趣的事，欢迎聊聊。</h2>
          <p>无论是 AI 应用、Web 项目、竞赛，还是只是想留一句话，都可以找到我。</p>
        </div>
        <div class="contact-list">
          <a :href="`mailto:${profile.email}`"><Mail :size="15" /> {{ profile.email }}</a>
          <a :href="`tel:${profile.phone}`"><Phone :size="15" /> {{ profile.phone }}</a>
          <span><MapPin :size="15" /> {{ profile.hometown }}</span>
          <span><Sparkles :size="15" /> {{ profile.birthDate }} 出生</span>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.about-page {
  padding-bottom: 56px;
}

.about-hero {
  display: flex;
  gap: 26px;
  align-items: center;
  padding: 28px;
  margin-bottom: 16px;
}

.hero-avatar {
  flex: none;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-badge {
  position: absolute;
  right: -6px;
  bottom: -3px;
  display: inline-flex;
  align-items: center;
  gap: 3px;
  padding: 3px 8px;
  border-radius: 999px;
  background: #fff;
  color: #247f96;
  font-size: 9px;
  font-weight: 700;
  box-shadow: 0 5px 14px rgba(31, 99, 120, 0.14);
}

.hero-copy {
  min-width: 0;
}

.hero-kicker {
  margin: 0 0 3px;
  font: 10px/1.5 'Times New Roman', serif;
  letter-spacing: 2.5px;
  color: #7d9aa4;
}

.hero-copy h1 {
  margin: 0;
  font-size: clamp(26px, 4vw, 40px);
  color: #164452;
}

.hero-copy h1 span {
  font-size: 0.52em;
  color: #6f99a4;
  font-weight: 400;
}

.hero-status {
  margin: 5px 0 10px;
  font-size: 12px;
  font-weight: 650;
  color: #2b8ca4;
}

.hero-intro {
  margin: 0 0 12px;
  max-width: 820px;
  font-size: 12px;
  line-height: 1.9;
  color: #5f7f89;
}

.hero-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 6px 14px;
  font-size: 10px;
  color: #8ba2aa;
}

.hero-meta span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.hero-chips,
.tag-list,
.course-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.hero-chips {
  margin-top: 12px;
}

.hero-chips span,
.tag-list span,
.course-tags span {
  padding: 4px 9px;
  border-radius: 999px;
  background: #eef9fb;
  color: #4a7f8d;
  font-size: 9.5px;
}

.about-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.info-card {
  padding: 22px;
}

.card-icon {
  width: 34px;
  height: 34px;
  display: grid;
  place-items: center;
  border-radius: 10px;
  background: #e8f8fb;
  color: #2b8ca4;
  margin-bottom: 8px;
}

.info-card h2 {
  margin: 0 0 8px;
  font-size: 17px;
  color: #1d4f5e;
}

.info-card p {
  margin: 0 0 10px;
  font-size: 12px;
  line-height: 1.9;
  color: #5f7f89;
}

.muted-line {
  color: #8ba2aa !important;
  font-size: 10.5px !important;
}

.section-block {
  margin-top: 28px;
}

.section-head {
  display: block;
  margin-bottom: 12px;
}

.section-head h2 {
  display: flex;
  align-items: center;
  gap: 7px;
  margin: 3px 0 5px;
  font-size: 21px;
  color: #164452;
}

.section-desc {
  margin: 0;
  font-size: 10.5px;
  color: #8ba2aa;
}

.timeline {
  position: relative;
  display: grid;
  gap: 12px;
  padding-left: 18px;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 4px;
  top: 12px;
  bottom: 12px;
  width: 1px;
  background: linear-gradient(#8fd7df, rgba(143, 215, 223, 0.1));
}

.timeline-item {
  position: relative;
  padding: 18px 20px;
}

.timeline-dot {
  position: absolute;
  left: -18px;
  top: 23px;
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: #65cdd9;
  border: 2px solid #fff;
  box-shadow: 0 0 0 3px rgba(101, 205, 217, 0.16);
}

.timeline-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
}

.timeline-head h3 {
  margin: 0;
  font-size: 16px;
  color: #1d4f5e;
}

.timeline-head p {
  margin: 3px 0 0;
  font-size: 11px;
  color: #2b8ca4;
}

.timeline-head > span {
  flex: none;
  font-size: 10px;
  color: #8ba2aa;
}

.timeline-text {
  margin: 9px 0 0;
  font-size: 11.5px;
  line-height: 1.85;
  color: #607f89;
}

.course-tags {
  margin-top: 8px;
}

.honor-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.honor-card {
  overflow: hidden;
}

.honor-image {
  display: block;
  position: relative;
  overflow: hidden;
  height: 230px;
  background: #f3fafc;
  border-bottom: 1px solid rgba(208, 236, 241, 0.8);
}

.honor-image-hint {
  position: absolute;
  right: 10px;
  bottom: 9px;
  padding: 4px 9px;
  border-radius: 999px;
  background: rgba(21, 86, 100, 0.72);
  color: #fff;
  font-size: 9px;
  letter-spacing: 0.5px;
  opacity: 0;
  transform: translateY(3px);
  transition: opacity 0.2s ease, transform 0.2s ease;
  pointer-events: none;
}

.honor-image:hover .honor-image-hint,
.honor-image:focus-visible .honor-image-hint {
  opacity: 1;
  transform: translateY(0);
}

.honor-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
  transition: transform 0.25s ease;
}

.honor-image:hover img {
  transform: scale(1.02);
}

.honor-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #9ab7bf;
  font-size: 10px;
}

.honor-copy {
  padding: 15px 17px 17px;
}

.honor-date {
  font: 9px/1.5 'Times New Roman', serif;
  letter-spacing: 1px;
  color: #a07a45;
}

.honor-copy h3 {
  margin: 3px 0 4px;
  font-size: 14px;
  line-height: 1.55;
  color: #1d4f5e;
}

.honor-issuer {
  margin: 0;
  font-size: 10px;
  color: #6f99a4;
}

.honor-desc {
  margin: 7px 0 0;
  font-size: 10px;
  line-height: 1.7;
  color: #8ba2aa;
}

.contact-card {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  margin-top: 28px;
  padding: 24px;
}

.contact-card h2 {
  margin: 3px 0 6px;
  font-size: 20px;
  color: #164452;
}

.contact-card p:not(.eyebrow) {
  margin: 0;
  max-width: 560px;
  font-size: 11.5px;
  line-height: 1.8;
  color: #6f8a93;
}

.contact-list {
  display: grid;
  align-content: center;
  gap: 7px;
  flex: none;
  font-size: 11px;
}

.contact-list a,
.contact-list span {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #4a7f8d;
  text-decoration: none;
}

@media (max-width: 760px) {
  .about-hero {
    flex-direction: column;
    align-items: center;
    gap: 16px;
    padding: 20px 18px;
  }
  .hero-avatar {
    align-self: center;
  }
  .hero-copy {
    width: 100%;
  }
  .hero-kicker {
    letter-spacing: 2px;
    font-size: 9px;
  }
  .hero-status {
    font-size: 12.5px;
    line-height: 1.65;
  }
  .hero-intro {
    margin-bottom: 14px;
    font-size: 13.5px;
    line-height: 1.85;
  }
  .hero-meta {
    gap: 6px 12px;
    font-size: 10.5px;
    line-height: 1.6;
  }
  .hero-chips {
    margin-top: 10px;
  }
  .hero-chips span,
  .tag-list span,
  .course-tags span {
    padding: 5px 10px;
    font-size: 10.5px;
  }
  .about-grid {
    gap: 12px;
    grid-template-columns: 1fr;
  }
  .info-card {
    padding: 18px;
  }
  .info-card h2 {
    font-size: 18px;
  }
  .info-card p {
    font-size: 13px;
    line-height: 1.85;
  }
  .muted-line {
    font-size: 11.5px !important;
  }
  .section-block {
    margin-top: 24px;
  }
  .section-head h2 {
    font-size: 21px;
  }
  .section-desc {
    font-size: 11.5px;
    line-height: 1.7;
  }
  .timeline {
    gap: 10px;
    padding-left: 16px;
  }
  .timeline-item {
    padding: 16px;
  }
  .timeline-dot {
    left: -17px;
    top: 21px;
  }
  .timeline-head h3 {
    font-size: 17px;
  }
  .timeline-head p {
    font-size: 12.5px;
  }
  .timeline-head > span {
    font-size: 11px;
  }
  .timeline-text {
    font-size: 13px;
    line-height: 1.8;
  }
  .honor-grid {
    gap: 12px;
    grid-template-columns: 1fr;
  }
  .honor-image {
    height: 280px;
  }
  .honor-image-hint {
    right: 8px;
    bottom: 8px;
    opacity: 1;
    transform: none;
    font-size: 9.5px;
  }
  .honor-copy {
    padding: 14px 15px 16px;
  }
  .honor-date {
    font-size: 10px;
  }
  .honor-copy h3 {
    font-size: 15.5px;
    line-height: 1.55;
  }
  .honor-issuer {
    font-size: 11.5px;
    line-height: 1.65;
  }
  .honor-desc {
    font-size: 11.5px;
    line-height: 1.75;
  }
  .contact-card {
    flex-direction: column;
    gap: 16px;
    padding: 20px;
  }
  .contact-card h2 {
    font-size: 19px;
    line-height: 1.5;
  }
  .contact-card p:not(.eyebrow) {
    font-size: 12.5px;
    line-height: 1.85;
  }
  .contact-list {
    gap: 8px;
    font-size: 12.5px;
  }
  :global(.float-tools) {
    display: none !important;
  }
}

@media (max-width: 480px) {
  .about-hero {
    gap: 14px;
    padding: 16px 15px;
  }
  .hero-copy h1 {
    font-size: 28px;
  }
  .hero-intro {
    font-size: 13px;
  }
  .timeline-head {
    flex-direction: column;
    gap: 3px;
  }
  .honor-image {
    height: 260px;
  }
  .contact-card {
    padding: 18px;
  }
}

@media (max-width: 380px) {
  .honor-image {
    height: 245px;
  }
  .hero-meta {
    font-size: 10px;
  }
}
</style>
