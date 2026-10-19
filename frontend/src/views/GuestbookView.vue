<script setup lang="ts">
import { computed, nextTick, onMounted, ref } from 'vue'
import {
  AlertCircle,
  Info,
  MapPin,
  MessageCircle,
  Send,
  ShieldCheck,
  Sparkles,
} from 'lucide-vue-next'
import PageHeader from '../components/PageHeader.vue'
import PondScene from '../components/PondScene.vue'
import AvatarArt from '../components/AvatarArt.vue'
import { api } from '../api/client'
import { auth, openAuth } from '../stores/auth'
import { siteState } from '../stores/site'
import { isStaticMode, STATIC_NOTICE } from '../stores/runtime'
import { notify } from '../stores/toast'
import type { Message } from '../types'
import { relativeTime } from '../utils/format'

const messages = ref<Message[]>([])
const loading = ref(true)
const error = ref('')
const content = ref('')
const nickname = ref('')
const submitting = ref(false)
const replyTo = ref('')
const inputEl = ref<HTMLTextAreaElement | null>(null)
const nicknameEl = ref<HTMLInputElement | null>(null)
const site = computed(() => siteState.data)

const danmaku = computed(() => messages.value.slice(0, 8))
const remaining = computed(() => 300 - content.value.length)
const canSubmit = computed(
  () => !isStaticMode.value && content.value.trim().length > 0 && content.value.length <= 300 && !submitting.value,
)

const rows = [12, 24, 40, 56, 70, 34, 18, 48]

function setReply(name: string) {
  replyTo.value = name
  content.value = content.value.startsWith(`@${name}`) ? content.value : `@${name} ${content.value}`
  nextTick(() => inputEl.value?.focus())
}

async function submit() {
  if (isStaticMode.value) {
    notify(STATIC_NOTICE, 'warn', 7000)
    return
  }
  if (!canSubmit.value) return
  if (!auth.user && !nickname.value.trim()) {
    notify('先留个称呼吧，或者登录后再留言。', 'warn')
    document.querySelector('.compose-card')?.scrollIntoView({ behavior: 'smooth', block: 'center' })
    nextTick(() => nicknameEl.value?.focus())
    return
  }
  submitting.value = true
  try {
    const message = await api.createMessage({
      nickname: auth.user?.nickname || nickname.value.trim() || '匿名网友',
      content: content.value.trim(),
    })
    messages.value = [message, ...messages.value]
    content.value = ''
    replyTo.value = ''
    notify('留言已经挂在云端啦，谢谢你。')
  } catch (e) {
    notify(e instanceof Error ? e.message : '留言失败，请稍后再试。', 'warn')
  } finally {
    submitting.value = false
  }
}

function requestLogin() {
  if (isStaticMode.value) {
    notify(STATIC_NOTICE, 'warn', 7000)
    return
  }
  openAuth('login')
}

onMounted(async () => {
  try {
    messages.value = await api.messages()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '留言加载失败'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page guestbook-page">
    <div class="container narrow">
      <PageHeader
        align="center"
        eyebrow="GUESTBOOK"
        title="留言板"
        subtitle="说点什么，留下你的足迹。每一句话，都会成为这片小窝的一部分。"
      />
    </div>

    <div class="container">
      <section class="danmaku-stage">
        <PondScene />
        <div class="stage-veil"></div>

        <div class="stage-label">
          <span class="stage-title"><MessageCircle :size="14" /> 弹幕</span>
          <span class="stage-count">{{ messages.length }}</span>
        </div>

        <div class="danmaku-layer" aria-hidden="true">
          <div
            v-for="(message, index) in danmaku"
            :key="message.id"
            class="danmaku-item"
            :class="{ admin: message.admin }"
            :style="{
              top: `${rows[index % rows.length]}%`,
              animationDuration: `${24 + (index % 5) * 5}s`,
              animationDelay: `${-index * 4.2}s`,
            }"
          >
            <span class="dm-avatar">{{ message.admin ? '云' : message.nickname.slice(0, 1) }}</span>
            <span class="dm-name">{{ message.nickname }}</span>
            <span class="dm-text">{{ message.content }}</span>
          </div>
        </div>

        <div class="stage-cat-caption">雨落在池塘，猫在等一句晚安。</div>

        <form class="danmaku-form" @submit.prevent="submit">
          <span class="dm-form-icon"><Send :size="14" /></span>
          <input
            v-model="content"
            type="text"
            maxlength="300"
            :disabled="isStaticMode"
            :placeholder="isStaticMode ? '静态展示模式：留言暂不可用' : '发送一条弹幕，留下此刻的心情…'"
            aria-label="弹幕内容"
          />
          <button type="submit" :disabled="!canSubmit">发送</button>
        </form>
      </section>

      <section class="rules-card">
        <span class="rules-icon"><AlertCircle :size="17" /></span>
        <div>
          <h3>留言规则</h3>
          <ul>
            <li>请文明发言，禁止发布违法、违规、广告类内容。</li>
            <li>留言内容支持 300 字以内，可回复他人留言。</li>
            <li>系统会自动记录留言 IP 及地理位置信息用于安全审核。</li>
            <li>遵守社区规范，共同维护友好的交流环境。</li>
          </ul>
        </div>
      </section>

      <section class="compose-card glass-card">
        <div class="compose-avatar">
          <AvatarArt v-if="auth.user" :size="46" />
          <span v-else class="guest-avatar">客</span>
        </div>

        <div class="compose-main">
          <div v-if="isStaticMode" class="static-feature-note">
            <Info :size="15" />
            <span>目前此功能不可用：当前是 GitHub Pages 静态展示版，留言和登录需要本地后端。</span>
          </div>
          <div class="compose-top">
            <div v-if="!auth.user" class="guest-name">
              <input
                ref="nicknameEl"
                v-model="nickname"
                type="text"
                maxlength="20"
                :disabled="isStaticMode"
                placeholder="你的称呼（选填）"
                aria-label="昵称"
              />
              <button type="button" class="login-link" @click="requestLogin">登录</button>
            </div>
            <span v-else class="compose-user">
              {{ auth.user.nickname }}
              <em v-if="auth.user.role === 'ADMIN'"><ShieldCheck :size="11" /> 站长</em>
            </span>
            <span class="char-count" :class="{ warn: remaining < 30 }">{{ content.length }}/300</span>
          </div>

          <textarea
            ref="inputEl"
            v-model="content"
            maxlength="300"
            rows="2"
            :disabled="isStaticMode"
            :placeholder="isStaticMode ? '静态展示模式：留言暂不可用' : '说点什么，留下你的足迹…'"
            aria-label="留言内容"
          ></textarea>

          <div class="compose-foot">
            <span class="compose-tip">
              <Sparkles :size="12" />
              {{ isStaticMode ? '这是只读的静态展示版' : replyTo ? `正在回复 @${replyTo}` : '温柔一点，云会记得。' }}
            </span>
            <button type="button" class="btn btn-primary" :disabled="!canSubmit" @click="submit">
              {{ submitting ? '发送中…' : '发布留言' }}
            </button>
          </div>
        </div>
      </section>

      <section class="message-section">
        <div class="message-head">
          <h2>全部留言 <span>· {{ messages.length }} 条</span></h2>
          <span class="message-order">按时间倒序</span>
        </div>

        <div v-if="error" class="error-box">{{ error }}</div>

        <div v-if="loading" class="loading-block">
          正在读大家的留言<span class="loading-dots"><i></i><i></i><i></i></span>
        </div>

        <div v-else-if="messages.length" class="message-list glass-card">
          <article v-for="message in messages" :key="message.id" class="message-item">
            <div class="message-avatar">
              <AvatarArt v-if="message.admin" :size="42" />
              <span v-else class="guest-avatar small">{{ message.nickname.slice(0, 1) }}</span>
            </div>

            <div class="message-body">
              <header class="message-meta">
                <span class="message-name">{{ message.nickname }}</span>
                <span v-if="message.admin" class="admin-badge"><ShieldCheck :size="10" /> 站长</span>
                <span class="message-location"><MapPin :size="10" /> {{ message.location }}</span>
                <time>{{ relativeTime(message.createdAt) }}</time>
                <button v-if="!isStaticMode" type="button" class="reply-btn" @click="setReply(message.nickname)">回复</button>
              </header>
              <p class="message-text">{{ message.content }}</p>
            </div>
          </article>
        </div>

        <div v-else-if="!error" class="empty-state glass-card">还没有留言，来坐第一排吧。</div>
      </section>

      <p class="guestbook-foot">
        云端の小窝 · {{ site.domain }} · 感谢每一个留下足迹的你
      </p>
    </div>
  </div>
</template>

<style scoped>
.guestbook-page {
  padding-bottom: 52px;
}

.danmaku-stage {
  position: relative;
  height: 440px;
  margin-top: 4px;
  border-radius: 24px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 24px 60px rgba(5, 42, 38, 0.28);
  background: #062925;
}

.stage-veil {
  position: absolute;
  inset: 0;
  z-index: 1;
  background:
    linear-gradient(180deg, rgba(4, 30, 28, 0.34), transparent 22%, transparent 70%, rgba(4, 30, 28, 0.5)),
    radial-gradient(ellipse at 50% 45%, transparent 30%, rgba(3, 24, 22, 0.28));
  pointer-events: none;
}

.stage-label {
  position: absolute;
  top: 15px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 3;
  display: flex;
  align-items: center;
  gap: 7px;
  color: rgba(235, 255, 249, 0.8);
  font-size: 11px;
  letter-spacing: 2px;
}

.stage-title {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.stage-count {
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  display: inline-grid;
  place-items: center;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.15);
  font-size: 9px;
}

.danmaku-layer {
  position: absolute;
  inset: 0;
  z-index: 2;
  overflow: hidden;
  pointer-events: none;
}

.danmaku-item {
  position: absolute;
  left: 100%;
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 5px 11px 5px 5px;
  border-radius: 999px;
  background: rgba(7, 38, 35, 0.52);
  border: 1px solid rgba(205, 255, 241, 0.16);
  color: rgba(240, 255, 251, 0.88);
  font-size: 11px;
  white-space: nowrap;
  backdrop-filter: blur(8px);
  animation-name: danmakuMove;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
  will-change: transform;
}

.danmaku-stage:hover .danmaku-item {
  animation-play-state: paused;
}

.dm-avatar {
  width: 22px;
  height: 22px;
  flex: none;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: linear-gradient(135deg, #8fe3d3, #3fa9c4);
  color: #0a3a34;
  font-size: 9px;
  font-weight: 750;
}

.danmaku-item.admin .dm-avatar {
  background: linear-gradient(135deg, #ffe4a8, #e6b85f);
  color: #614310;
}

.dm-name {
  font-size: 9px;
  color: rgba(200, 241, 230, 0.62);
}

.danmaku-item.admin .dm-name {
  color: #ffdf9d;
}

.dm-text {
  font-size: 10.5px;
}

.stage-cat-caption {
  position: absolute;
  left: 50%;
  bottom: 74px;
  transform: translateX(-50%);
  z-index: 3;
  font-family: var(--font-serif);
  font-size: 10px;
  letter-spacing: 2px;
  color: rgba(219, 251, 242, 0.45);
  white-space: nowrap;
}

.danmaku-form {
  position: absolute;
  left: 50%;
  bottom: 18px;
  transform: translateX(-50%);
  z-index: 4;
  width: min(540px, calc(100% - 32px));
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 5px 5px 5px 13px;
  border-radius: 999px;
  background: rgba(9, 43, 39, 0.55);
  border: 1px solid rgba(212, 255, 243, 0.22);
  backdrop-filter: blur(16px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.2);
}

.dm-form-icon {
  color: rgba(209, 255, 242, 0.62);
  display: grid;
  place-items: center;
  flex: none;
}

.danmaku-form input {
  flex: 1;
  min-width: 0;
  height: 32px;
  border: 0;
  outline: 0;
  background: transparent;
  color: #effffa;
  font-size: 11.5px;
}

.danmaku-form input::placeholder {
  color: rgba(211, 249, 239, 0.45);
}

.danmaku-form button {
  flex: none;
  height: 32px;
  padding: 0 16px;
  border: 0;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.9);
  color: #175f5a;
  font-size: 10.5px;
  font-weight: 750;
  cursor: pointer;
  transition: background 0.2s ease;
}

.danmaku-form button:hover {
  background: #fff;
}

.danmaku-form button:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.rules-card {
  display: flex;
  gap: 14px;
  margin-top: 16px;
  padding: 19px 22px;
  border-radius: 17px;
  background: linear-gradient(110deg, rgba(255, 248, 225, 0.94), rgba(255, 252, 240, 0.78));
  border: 1px solid rgba(250, 235, 197, 0.9);
  box-shadow: 0 10px 26px rgba(150, 116, 40, 0.07);
}

.rules-icon {
  flex: none;
  width: 32px;
  height: 32px;
  display: grid;
  place-items: center;
  border-radius: 9px;
  background: #fff1c7;
  color: #c79132;
}

.rules-card h3 {
  margin: 1px 0 5px;
  font-size: 12.5px;
  color: #6e5220;
}

.rules-card ul {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 3px;
}

.rules-card li {
  position: relative;
  padding-left: 11px;
  font-size: 10px;
  line-height: 1.7;
  color: #927a4b;
}

.rules-card li::before {
  content: '·';
  position: absolute;
  left: 2px;
  color: #cba85e;
}

.compose-card {
  display: flex;
  gap: 14px;
  margin-top: 16px;
  padding: 18px 20px;
}

.compose-avatar {
  flex: none;
  padding-top: 2px;
}

.guest-avatar {
  width: 46px;
  height: 46px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: linear-gradient(135deg, #d9f3ef, #9fdce0);
  color: #2c7b7b;
  font-family: var(--font-serif);
  font-size: 18px;
  border: 2px solid rgba(255, 255, 255, 0.9);
}

.guest-avatar.small {
  width: 42px;
  height: 42px;
  font-size: 15px;
}

.compose-main {
  flex: 1;
  min-width: 0;
}

.static-feature-note {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  margin-bottom: 10px;
  padding: 8px 10px;
  border-radius: 9px;
  background: #fff8e5;
  border: 1px solid #f0dfb8;
  color: #8f6b2c;
  font-size: 11px;
  line-height: 1.65;
}

.static-feature-note svg {
  flex: none;
  margin-top: 1px;
}

.compose-main textarea:disabled,
.guest-name input:disabled,
.danmaku-form input:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.compose-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 6px;
}

.guest-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.guest-name input {
  width: 150px;
  border: 0;
  border-bottom: 1px dashed rgba(59, 124, 141, 0.24);
  outline: 0;
  background: transparent;
  padding: 3px 1px;
  font-size: 10.5px;
  color: #3a6572;
}

.login-link {
  border: 0;
  background: transparent;
  color: #2c8ba3;
  font-size: 10px;
  font-weight: 700;
  cursor: pointer;
  padding: 0;
}

.compose-user {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  font-weight: 700;
  color: #336573;
}

.compose-user em {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  padding: 1px 5px;
  border-radius: 4px;
  background: #fff1d4;
  color: #af7d22;
  font-size: 8px;
  font-style: normal;
  font-weight: 650;
}

.char-count {
  font-size: 9px;
  color: #9aadb3;
}

.char-count.warn {
  color: #d98a4b;
}

.compose-main textarea {
  width: 100%;
  min-height: 52px;
  resize: vertical;
  border: 0;
  outline: 0;
  background: #f6fbfc;
  border-radius: 10px;
  padding: 10px 12px;
  font-size: 12px;
  line-height: 1.7;
  color: #2c5360;
}

.compose-main textarea::placeholder {
  color: #a6b8be;
}

.compose-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 8px;
}

.compose-tip {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 9.5px;
  color: #95a9b0;
}

.compose-foot .btn {
  min-height: 32px;
  padding: 0 16px;
  font-size: 10.5px;
}

.compose-foot .btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.message-section {
  margin-top: 28px;
}

.message-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
  padding: 0 4px;
}

.message-head h2 {
  margin: 0;
  font-size: 17px;
  color: #225160;
}

.message-head h2 span {
  font-size: 10px;
  font-weight: 400;
  color: #9aadb3;
}

.message-order {
  font-size: 9px;
  color: #a0b2b8;
}

.message-list {
  padding: 6px 26px;
}

.message-item {
  display: flex;
  gap: 13px;
  padding: 17px 0;
  border-bottom: 1px dashed rgba(48, 112, 131, 0.14);
}

.message-item:last-child {
  border-bottom: 0;
}

.message-avatar {
  flex: none;
}

.message-body {
  flex: 1;
  min-width: 0;
}

.message-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 7px;
  font-size: 9px;
  color: #97aab1;
}

.message-name {
  font-size: 11.5px;
  font-weight: 750;
  color: #285868;
}

.admin-badge {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  padding: 1px 5px;
  border-radius: 4px;
  background: #fff1d4;
  color: #aa7820;
  font-size: 8px;
  font-weight: 650;
}

.message-location {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  padding: 1px 6px;
  border-radius: 999px;
  background: #f1f9fa;
  color: #7f9da6;
}

.message-meta time {
  margin-left: 2px;
}

.reply-btn {
  margin-left: auto;
  border: 0;
  background: transparent;
  color: #3d8fa5;
  font-size: 9px;
  cursor: pointer;
  padding: 0;
}

.message-text {
  margin: 6px 0 0;
  white-space: pre-line;
  font-size: 12px;
  line-height: 1.85;
  color: #466772;
}

.guestbook-foot {
  margin: 24px 0 0;
  text-align: center;
  font-family: var(--font-serif);
  font-size: 9.5px;
  letter-spacing: 1.5px;
  color: #96abb2;
}

@keyframes danmakuMove {
  from {
    transform: translateX(0);
  }
  to {
    transform: translateX(calc(-100vw - 100%));
  }
}

@media (prefers-reduced-motion: reduce) {
  .danmaku-item {
    animation: none;
    left: 18px;
    right: auto;
    width: auto;
    max-width: calc(100% - 36px);
    white-space: normal;
    transform: none;
  }
  .dm-text {
    white-space: normal;
  }
  .danmaku-item:nth-child(n + 5) {
    display: none;
  }
}

@media (max-width: 620px) {
  .danmaku-stage {
    height: 380px;
    border-radius: 18px;
  }
  .danmaku-item {
    font-size: 10px;
  }
  .stage-cat-caption {
    display: none;
  }
  .danmaku-form {
    bottom: 14px;
  }
  .rules-card li {
    font-size: 11.5px;
    line-height: 1.75;
  }
  .rules-card {
    padding: 16px;
  }
  .compose-card {
    padding: 15px;
    gap: 10px;
  }
  .compose-foot {
    flex-wrap: wrap;
  }
  .message-list {
    padding: 4px 16px;
  }
  .message-item {
    gap: 10px;
  }
  .reply-btn {
    margin-left: 0;
  }
}
</style>
