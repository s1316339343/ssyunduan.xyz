<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { Eye, Info, LockKeyhole, RefreshCw, ShieldCheck } from 'lucide-vue-next'
import PageHeader from '../components/PageHeader.vue'
import { startVisitorCounter, readVisitorCounts, visitorCounterEnabled } from '../utils/visitorCounter'

const UNLOCK_KEY = 'yunduan.visits.unlocked'
const PASS_HASH = 'bd4586487368611bf653e85a8400cc13ea031d7415c0de61573dd89f679d2013'

const unlocked = ref(localStorage.getItem(UNLOCK_KEY) === '1')
const password = ref('')
const error = ref('')
const pv = ref('—')
const uv = ref('—')
const ready = ref(false)
const updatedAt = ref('')
const counterEnabled = visitorCounterEnabled()
let timer: number | undefined

const statusText = computed(() => {
  if (!counterEnabled) return '本地地址不加载第三方计数器，请在线上地址查看。'
  if (!ready.value) return '正在读取计数，或统计服务暂时不可用。'
  return '计数来自 Busuanzi 免费访客计数器，仅作趋势参考。'
})

async function sha256(value: string): Promise<string> {
  const bytes = new TextEncoder().encode(value)
  const digest = await crypto.subtle.digest('SHA-256', bytes)
  return Array.from(new Uint8Array(digest))
    .map((byte) => byte.toString(16).padStart(2, '0'))
    .join('')
}

async function unlock() {
  error.value = ''
  if (!password.value.trim()) {
    error.value = '请输入访问口令。'
    return
  }
  try {
    const hash = await sha256(password.value.trim())
    if (hash !== PASS_HASH) {
      error.value = '口令不正确。'
      return
    }
    unlocked.value = true
    localStorage.setItem(UNLOCK_KEY, '1')
    password.value = ''
    refresh()
  } catch {
    error.value = '当前浏览器环境不支持安全校验，请使用 HTTPS 访问。'
  }
}

function lockAgain() {
  unlocked.value = false
  localStorage.removeItem(UNLOCK_KEY)
}

function refresh() {
  startVisitorCounter()
  const counts = readVisitorCounts()
  pv.value = counts.pv
  uv.value = counts.uv
  ready.value = counts.ready
  if (counts.ready) updatedAt.value = new Date().toLocaleString()
}

onMounted(() => {
  startVisitorCounter()
  refresh()
  timer = window.setInterval(refresh, 1200)
})

onUnmounted(() => {
  if (timer) window.clearInterval(timer)
})
</script>

<template>
  <div class="page visits-page">
    <div class="container narrow">
      <PageHeader
        eyebrow="PRIVATE COUNTER"
        title="访问统计"
        subtitle="一个不放在导航里的入口，用来看看有没有人来过。"
      />

      <section v-if="!unlocked" class="lock-card glass-card">
        <span class="lock-icon"><LockKeyhole :size="24" /></span>
        <h2>仅站长查看</h2>
        <p>这里没有登录框，只有一个访问口令。输入正确后会在当前浏览器记住。</p>
        <form class="unlock-form" @submit.prevent="unlock">
          <input
            v-model="password"
            type="password"
            autocomplete="current-password"
            placeholder="输入访问口令"
            aria-label="访问口令"
          />
          <button class="btn btn-primary" type="submit">打开统计</button>
        </form>
        <p v-if="error" class="unlock-error">{{ error }}</p>
        <p class="lock-hint">
          <Info :size="12" />
          静态网站无法做真正的服务端鉴权，这里只是不公开入口加口令遮挡，不是安全边界。
        </p>
      </section>

      <template v-else>
        <div class="counter-grid">
          <article class="counter-card glass-card">
            <span class="counter-icon mint"><Eye :size="20" /></span>
            <strong>{{ pv }}</strong>
            <span>本站总访问</span>
            <small>每次打开页面计一次</small>
          </article>
          <article class="counter-card glass-card">
            <span class="counter-icon pink"><ShieldCheck :size="20" /></span>
            <strong>{{ uv }}</strong>
            <span>独立访客</span>
            <small>按浏览器粗略去重</small>
          </article>
        </div>

        <section class="counter-note glass-card">
          <div class="counter-note-head">
            <h2>这组数字怎么来的</h2>
            <button class="btn btn-soft" type="button" @click="refresh">
              <RefreshCw :size="14" /> 刷新
            </button>
          </div>
          <p>{{ statusText }}</p>
          <ul>
            <li>只统计公网地址，localhost 和局域网 IP 不计入。</li>
            <li>只能看到总量，无法知道具体是谁、看了哪一篇。</li>
            <li>如果数字一直是“—”，可能是统计脚本被网络或插件拦截。</li>
          </ul>
          <p class="updated">最后读取：{{ updatedAt || '等待中' }}</p>
          <div class="counter-actions">
            <RouterLink class="btn btn-ghost" to="/">回到首页</RouterLink>
            <button class="btn btn-ghost" type="button" @click="lockAgain">重新锁定</button>
          </div>
        </section>
      </template>
    </div>
  </div>
</template>

<style scoped>
.visits-page {
  padding-bottom: 60px;
}

.lock-card {
  max-width: 560px;
  margin: 8px auto 0;
  padding: 30px;
  text-align: center;
}

.lock-icon {
  width: 54px;
  height: 54px;
  margin: 0 auto 14px;
  display: grid;
  place-items: center;
  border-radius: 18px;
  background: #e8f8fb;
  color: #247f96;
}

.lock-card h2 {
  margin: 0 0 7px;
  font-size: 22px;
  color: #1d4f5e;
}

.lock-card > p {
  margin: 0 auto 18px;
  max-width: 420px;
  font-size: 12px;
  line-height: 1.9;
  color: #71909a;
}

.unlock-form {
  display: flex;
  gap: 8px;
  max-width: 390px;
  margin: 0 auto;
}

.unlock-form input {
  min-width: 0;
  flex: 1;
  height: 44px;
  padding: 0 13px;
  border: 1px solid #d8ebef;
  border-radius: 10px;
  background: #fbfeff;
  color: #2d5663;
  outline: none;
}

.unlock-form input:focus {
  border-color: #7bcbdd;
  box-shadow: 0 0 0 3px rgba(88, 187, 213, 0.12);
}

.unlock-form .btn {
  flex: none;
}

.unlock-error {
  margin: 12px 0 0 !important;
  color: #b25a4c !important;
  font-size: 12px !important;
}

.lock-hint {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  gap: 5px;
  margin-top: 16px !important;
  font-size: 10px !important;
  color: #9aadb3 !important;
}

.lock-hint svg {
  flex: none;
  margin-top: 2px;
}

.counter-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  margin: 8px auto 16px;
  max-width: 720px;
}

.counter-card {
  padding: 22px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.counter-icon {
  width: 40px;
  height: 40px;
  display: grid;
  place-items: center;
  border-radius: 12px;
  margin-bottom: 8px;
}

.counter-icon.mint {
  background: #e4f8f2;
  color: #2b9a82;
}

.counter-icon.pink {
  background: #fff0f3;
  color: #c66b80;
}

.counter-card strong {
  font-family: 'Times New Roman', serif;
  font-size: 38px;
  line-height: 1.1;
  color: #1a6074;
}

.counter-card > span:not(.counter-icon) {
  margin-top: 4px;
  font-size: 12px;
  font-weight: 700;
  color: #3d6a76;
}

.counter-card small {
  margin-top: 4px;
  font-size: 10px;
  color: #9aadb3;
}

.counter-note {
  max-width: 720px;
  margin: 0 auto;
  padding: 24px;
}

.counter-note-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.counter-note h2 {
  margin: 0;
  font-size: 18px;
  color: #1d4f5e;
}

.counter-note > p {
  margin: 12px 0;
  font-size: 12px;
  line-height: 1.9;
  color: #6f8a93;
}

.counter-note ul {
  margin: 0;
  padding-left: 20px;
  font-size: 11.5px;
  line-height: 1.9;
  color: #6f8a93;
}

.counter-note .updated {
  margin-top: 14px;
  padding-top: 10px;
  border-top: 1px dashed rgba(48, 112, 131, 0.16);
  font-size: 10px;
  color: #9aadb3;
}

.counter-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 12px;
}

@media (max-width: 520px) {
  .lock-card {
    padding: 22px 18px;
  }
  .unlock-form {
    flex-direction: column;
  }
  .unlock-form .btn {
    width: 100%;
  }
  .counter-grid {
    grid-template-columns: 1fr;
  }
  .counter-note {
    padding: 18px;
  }
  .counter-note-head {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
