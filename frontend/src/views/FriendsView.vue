<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ArrowUpRight, Check, Copy, HeartHandshake, Link2, Mail, Sparkles } from 'lucide-vue-next'
import ArtCover from '../components/ArtCover.vue'
import { api } from '../api/client'
import { siteState } from '../stores/site'
import { notify } from '../stores/toast'
import type { Friend } from '../types'
import { formatDate } from '../utils/format'

const friends = ref<Friend[]>([])
const loading = ref(true)
const error = ref('')
const copied = ref('')
const site = computed(() => siteState.data)

const siteInfo = computed(() => [
  { label: '名称', value: site.value.name, key: 'name' },
  { label: '地址', value: `https://${site.value.domain}`, key: 'url' },
  { label: '描述', value: site.value.tagline, key: 'desc' },
  { label: '头像', value: `https://${site.value.domain}/avatar.svg`, key: 'avatar' },
  { label: '封面', value: `https://${site.value.domain}/cover.svg`, key: 'cover' },
])

async function copy(text: string, key: string) {
  try {
    await navigator.clipboard.writeText(text)
    copied.value = key
    notify('已复制到剪贴板', 'ok')
    window.setTimeout(() => (copied.value = ''), 1600)
  } catch {
    notify('复制失败，请手动选择文本复制', 'warn')
  }
}

onMounted(async () => {
  try {
    friends.value = await api.friends()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '友链加载失败'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page friends-page">
    <div class="container">
      <section class="friends-banner">
        <ArtCover variant="sunset" ratio="21 / 6" />
        <div class="fb-overlay"></div>
        <div class="fb-copy">
          <p class="fb-eyebrow">FRIENDS' BOOK</p>
          <h1>清风来故人，明月照初心</h1>
          <p>山水有相逢，来日皆可期。愿在这里，遇见更多有趣的灵魂。</p>
        </div>
        <a class="fb-apply" href="#apply"><Link2 :size="14" /> 申请友链</a>
        <span class="fb-stamp">友人帐</span>
      </section>

      <div class="friends-intro">
        <p class="eyebrow">EXCHANGE LINKS</p>
        <h2>把彼此的小窝，连成一片小小的星空。</h2>
        <p>如果你也有一座认真打理的站点，欢迎交换友链。先添加本站，再提交申请，我会尽快回访。</p>
      </div>

      <div class="info-layout">
        <section class="site-info glass-card">
          <header class="info-head">
            <span class="info-icon"><Sparkles :size="15" /></span>
            <div>
              <h3>本站信息</h3>
              <p>复制下面的信息，添加到你的友链页</p>
            </div>
          </header>

          <div class="info-table">
            <div v-for="row in siteInfo" :key="row.key" class="info-row">
              <span class="info-label">{{ row.label }}</span>
              <code class="info-value">{{ row.value }}</code>
              <button type="button" class="copy-btn" :aria-label="`复制${row.label}`" @click="copy(row.value, row.key)">
                <Check v-if="copied === row.key" :size="13" />
                <Copy v-else :size="13" />
              </button>
            </div>
          </div>

          <div class="info-foot">
            <span><Mail :size="12" /> 申请前请先邮件告知：{{ site.email }}</span>
          </div>
        </section>

        <section id="apply" class="apply-card glass-card">
          <header class="info-head">
            <span class="info-icon pink"><HeartHandshake :size="15" /></span>
            <div>
              <h3>申请须知</h3>
              <p>三步就能成为邻居</p>
            </div>
          </header>

          <ol class="apply-steps">
            <li>
              <span class="step-no">01</span>
              <div>
                <h4>先添加本站</h4>
                <p>请先将「{{ site.name }}」添加到你的友链页面。</p>
              </div>
            </li>
            <li>
              <span class="step-no">02</span>
              <div>
                <h4>站点内容健康</h4>
                <p>不添加广告营销、无实质内容或无法访问的站点。</p>
              </div>
            </li>
            <li>
              <span class="step-no">03</span>
              <div>
                <h4>提交后等待审核</h4>
                <p>申请提交后，需站长审核通过才会展示在这里。</p>
              </div>
            </li>
          </ol>

          <a class="apply-btn" :href="`mailto:${site.email}?subject=申请友链 - 云端の小窝`">
            <Mail :size="14" /> 发送友链申请
          </a>
        </section>
      </div>

      <section class="friend-list-section">
        <div class="section-head">
          <div>
            <p class="eyebrow">NEIGHBORS</p>
            <h2>友情链接</h2>
            <p>一些常去串门的小窝，排名不分先后。</p>
          </div>
          <span class="friend-count">{{ friends.length }} 位邻居</span>
        </div>

        <div v-if="error" class="error-box">{{ error }}</div>

        <div v-if="loading" class="loading-block">
          正在敲门<span class="loading-dots"><i></i><i></i><i></i></span>
        </div>

        <div v-else-if="friends.length" class="friend-grid">
          <a
            v-for="friend in friends"
            :key="friend.id"
            class="friend-card"
            :href="friend.url"
            target="_blank"
            rel="noreferrer noopener"
          >
            <div class="friend-cover">
              <ArtCover :variant="friend.art" ratio="16 / 7" />
              <span class="friend-shade"></span>
            </div>
            <div class="friend-body">
              <span class="friend-avatar">
                <ArtCover :variant="friend.avatar" ratio="1 / 1" />
              </span>
              <div class="friend-copy">
                <h3>{{ friend.name }} <ArrowUpRight :size="12" /></h3>
                <p>{{ friend.description }}</p>
                <span class="friend-date">{{ formatDate(friend.date) }} 加入</span>
              </div>
            </div>
          </a>
        </div>

        <div v-else-if="!error" class="empty-state glass-card">还没有邻居，欢迎成为第一个。</div>
      </section>

      <div class="friends-foot glass-card">
        <span class="foot-emoji">☁</span>
        <p>“一个人走得快，一群人走得远。感谢每一个愿意互相照亮的小站。”</p>
        <RouterLink to="/guestbook">去留言板聊聊 →</RouterLink>
      </div>
    </div>
  </div>
</template>

<style scoped>
.friends-page {
  padding-bottom: 50px;
}

.friends-banner {
  position: relative;
  margin-top: 92px;
  border-radius: 26px;
  overflow: hidden;
  box-shadow: 0 24px 60px rgba(22, 88, 108, 0.22);
  border: 1px solid rgba(255, 255, 255, 0.78);
}

.friends-banner :deep(.art-cover) {
  display: block;
}

.fb-overlay {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(90deg, rgba(38, 27, 34, 0.68), rgba(38, 27, 34, 0.14) 70%),
    linear-gradient(180deg, rgba(20, 24, 34, 0.15), rgba(20, 24, 34, 0.38));
}

.fb-copy {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0 46px;
  color: #fff9f3;
}

.fb-eyebrow {
  margin: 0 0 8px;
  font-family: 'Times New Roman', serif;
  font-size: 10px;
  letter-spacing: 3px;
  color: rgba(255, 245, 235, 0.7);
}

.fb-copy h1 {
  margin: 0 0 8px;
  font-size: clamp(23px, 3vw, 36px);
  letter-spacing: 3px;
  line-height: 1.4;
  text-shadow: 0 4px 24px rgba(0, 0, 0, 0.26);
}

.fb-copy p:last-child {
  margin: 0;
  font-family: var(--font-serif);
  font-size: 11.5px;
  letter-spacing: 1.5px;
  color: rgba(255, 246, 238, 0.75);
}

.fb-apply {
  position: absolute;
  right: 32px;
  top: 50%;
  transform: translateY(-50%);
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  border-radius: 999px;
  background: #ff9fae;
  color: #5b2b33;
  font-size: 11.5px;
  font-weight: 750;
  text-decoration: none;
  box-shadow: 0 12px 26px rgba(94, 32, 44, 0.22);
  transition: all 0.2s ease;
}

.fb-apply:hover {
  background: #ffb3be;
  transform: translateY(-50%) scale(1.02);
}

.fb-stamp {
  position: absolute;
  left: 46px;
  bottom: 14px;
  font-size: 9px;
  letter-spacing: 3px;
  color: rgba(255, 247, 239, 0.48);
}

.friends-intro {
  max-width: 700px;
  margin: 28px auto 22px;
  text-align: center;
}

.friends-intro h2 {
  margin: 7px 0 6px;
  font-size: 21px;
  letter-spacing: 1.5px;
  color: #174653;
}

.friends-intro > p:last-child {
  margin: 0;
  font-size: 11.5px;
  line-height: 1.85;
  color: #6f8b94;
}

.info-layout {
  display: grid;
  grid-template-columns: 1.15fr 0.85fr;
  gap: 18px;
}

.site-info,
.apply-card {
  padding: 22px 24px;
}

.info-head {
  display: flex;
  align-items: flex-start;
  gap: 11px;
  padding-bottom: 14px;
  border-bottom: 1px dashed rgba(48, 112, 131, 0.16);
}

.info-icon {
  width: 34px;
  height: 34px;
  flex: none;
  display: grid;
  place-items: center;
  border-radius: 10px;
  background: #e2f8f1;
  color: #35a184;
}

.info-icon.pink {
  background: #ffeaf0;
  color: #d6758c;
}

.info-head h3 {
  margin: 1px 0 2px;
  font-size: 14px;
  color: #245462;
}

.info-head p {
  margin: 0;
  font-size: 9.5px;
  color: #94a9b0;
}

.info-table {
  padding: 6px 0;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 9px 0;
  border-bottom: 1px solid rgba(48, 112, 131, 0.07);
}

.info-row:last-child {
  border-bottom: 0;
}

.info-label {
  width: 42px;
  flex: none;
  font-size: 10px;
  color: #8ba2aa;
}

.info-value {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-family: 'SFMono-Regular', Consolas, monospace;
  font-size: 10px;
  color: #3f7787;
  background: #f4fbfc;
  padding: 4px 8px;
  border-radius: 6px;
}

.copy-btn {
  width: 26px;
  height: 26px;
  flex: none;
  display: grid;
  place-items: center;
  border: 1px solid #e0eff3;
  border-radius: 7px;
  background: #fff;
  color: #6b929e;
  cursor: pointer;
  transition: all 0.2s ease;
}

.copy-btn:hover {
  color: #1e86a0;
  border-color: #bfe3eb;
}

.info-foot {
  padding-top: 10px;
  border-top: 1px dashed rgba(48, 112, 131, 0.14);
  font-size: 9px;
  color: #93a8af;
}

.info-foot span {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.apply-steps {
  margin: 0;
  padding: 10px 0 0;
  list-style: none;
  display: grid;
  gap: 12px;
}

.apply-steps li {
  display: flex;
  gap: 10px;
}

.step-no {
  flex: none;
  font-family: 'Times New Roman', serif;
  font-size: 11px;
  color: #d99aa9;
  padding-top: 2px;
}

.apply-steps h4 {
  margin: 0 0 2px;
  font-size: 11.5px;
  color: #3b6571;
}

.apply-steps p {
  margin: 0;
  font-size: 9.5px;
  line-height: 1.7;
  color: #8ba2aa;
}

.apply-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 16px;
  height: 34px;
  border-radius: 9px;
  background: #e9f8fb;
  color: #237b94;
  font-size: 10.5px;
  font-weight: 700;
  text-decoration: none;
  transition: background 0.2s ease;
}

.apply-btn:hover {
  background: #d9f2f7;
}

.friend-list-section {
  margin-top: 36px;
}

.friend-count {
  flex: none;
  font-size: 10px;
  color: #8ba2aa;
  padding: 5px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.6);
}

.friend-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 14px;
}

.friend-card {
  display: block;
  overflow: hidden;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.76);
  border: 1px solid rgba(255, 255, 255, 0.88);
  box-shadow: 0 10px 26px rgba(29, 97, 119, 0.1);
  text-decoration: none;
  transition:
    transform 0.26s ease,
    box-shadow 0.26s ease;
}

.friend-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(26, 92, 113, 0.16);
}

.friend-cover {
  position: relative;
  overflow: hidden;
}

.friend-cover :deep(svg) {
  transition: transform 0.5s ease;
}

.friend-card:hover .friend-cover :deep(svg) {
  transform: scale(1.05);
}

.friend-shade {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, transparent 45%, rgba(255, 255, 255, 0.3));
}

.friend-body {
  position: relative;
  padding: 0 14px 13px;
}

.friend-avatar {
  display: block;
  width: 50px;
  height: 50px;
  margin-top: -25px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid #fff;
  box-shadow: 0 6px 16px rgba(24, 89, 110, 0.16);
  position: relative;
  z-index: 2;
}

.friend-avatar :deep(.art-cover) {
  border-radius: 50%;
}

.friend-copy h3 {
  display: flex;
  align-items: center;
  gap: 3px;
  margin: 6px 0 3px;
  font-size: 12.5px;
  color: #285666;
}

.friend-copy h3 svg {
  opacity: 0.45;
}

.friend-copy p {
  margin: 0 0 5px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-size: 10px;
  line-height: 1.65;
  color: #7d959d;
  min-height: 33px;
}

.friend-date {
  font-size: 8.5px;
  color: #a2b4ba;
}

.friends-foot {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-top: 26px;
  padding: 20px 24px;
}

.foot-emoji {
  font-size: 18px;
  color: #88c6d4;
}

.friends-foot p {
  margin: 0;
  flex: 1;
  font-family: var(--font-serif);
  font-size: 11.5px;
  line-height: 1.8;
  color: #5f818c;
}

.friends-foot a {
  flex: none;
  font-size: 10px;
  color: #2e8ba2;
  text-decoration: none;
}

@media (max-width: 900px) {
  .info-layout {
    grid-template-columns: 1fr;
  }
  .fb-apply {
    right: 22px;
    top: auto;
    bottom: 18px;
    transform: none;
  }
  .fb-apply:hover {
    transform: scale(1.02);
  }
}

@media (max-width: 620px) {
  .friends-banner {
    margin-top: 82px;
    border-radius: 18px;
  }
  .friends-banner :deep(.art-cover) {
    aspect-ratio: 4 / 3 !important;
  }
  .fb-copy {
    padding: 0 20px 62px;
    justify-content: flex-end;
  }
  .fb-copy p:last-child {
    display: none;
  }
  .fb-copy h1 {
    font-size: 21px;
    letter-spacing: 1.5px;
  }
  .fb-apply {
    left: 20px;
    right: auto;
    bottom: 16px;
  }
  .fb-stamp {
    left: auto;
    right: 16px;
    bottom: 20px;
  }
  .friends-intro h2 {
    font-size: 18px;
  }
  .site-info,
  .apply-card {
    padding: 18px;
  }
  .info-value {
    font-size: 11px;
  }
  .copy-btn {
    width: 34px;
    height: 34px;
  }
  .friend-grid {
    grid-template-columns: 1fr 1fr;
    gap: 10px;
  }
  .friends-foot {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}

@media (max-width: 520px) {
  .friend-grid {
    grid-template-columns: 1fr;
  }
}
</style>
