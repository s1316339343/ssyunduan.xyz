<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { Eye, EyeOff, Lock, Mail, Smile, Sparkles, User, X } from 'lucide-vue-next'
import { api } from '../api/client'
import { acceptSession, auth, closeAuth } from '../stores/auth'
import { notify } from '../stores/toast'
import BrandMark from './BrandMark.vue'

const form = reactive({
  username: '',
  password: '',
  nickname: '',
  email: '',
})
const showPassword = ref(false)
const firstInput = ref<HTMLInputElement | null>(null)

const isRegister = computed(() => auth.mode === 'register')
const canSubmit = computed(() => {
  if (form.username.trim().length < 3 || form.password.length < 6) return false
  if (isRegister.value) {
    return form.nickname.trim().length > 0 && /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)
  }
  return true
})

function switchMode(mode: 'login' | 'register') {
  auth.mode = mode
  auth.error = ''
}

async function submit() {
  if (!canSubmit.value || auth.loading) return
  auth.loading = true
  auth.error = ''
  try {
    const result = isRegister.value
      ? await api.register({
          username: form.username.trim(),
          password: form.password,
          nickname: form.nickname.trim(),
          email: form.email.trim(),
        })
      : await api.login({ username: form.username.trim(), password: form.password })
    acceptSession(result)
    notify(isRegister.value ? `欢迎加入小窝，${result.user.nickname}！` : `欢迎回来，${result.user.nickname}。`)
    form.password = ''
  } catch (error) {
    auth.error = error instanceof Error ? error.message : '操作失败，请稍后再试。'
  } finally {
    auth.loading = false
  }
}

function onKeydown(event: KeyboardEvent) {
  if (event.key === 'Escape' && auth.open) closeAuth()
}

watch(
  () => auth.open,
  async (open) => {
    document.body.style.overflow = open ? 'hidden' : ''
    if (open) {
      auth.error = ''
      await nextTick()
      firstInput.value?.focus()
    }
  },
)

onMounted(() => document.addEventListener('keydown', onKeydown))
onUnmounted(() => {
  document.removeEventListener('keydown', onKeydown)
  document.body.style.overflow = ''
})
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="auth.open" class="modal-mask" @click.self="closeAuth">
        <div class="auth-dialog" role="dialog" aria-modal="true" aria-labelledby="auth-title">
          <button class="modal-close" type="button" aria-label="关闭" @click="closeAuth">
            <X :size="18" />
          </button>

          <aside class="auth-art" aria-hidden="true">
            <div class="auth-art-sky">
              <span class="auth-cloud c1"></span>
              <span class="auth-cloud c2"></span>
              <span class="auth-cloud c3"></span>
              <span class="auth-sun"></span>
            </div>
            <div class="auth-art-copy">
              <BrandMark light />
              <p class="auth-quote">“愿你在这里，<br />遇见一小片温柔的云。”</p>
              <p class="auth-from">— 云端手记</p>
            </div>
            <svg class="auth-hill" viewBox="0 0 420 160" preserveAspectRatio="none">
              <path d="M0 100 C80 60 150 110 230 88 C320 64 370 88 420 76 L420 160 L0 160Z" fill="#8fd35a" opacity="0.85" />
              <path d="M0 124 C100 96 180 130 280 114 C350 102 390 116 420 108 L420 160 L0 160Z" fill="#5eb83c" opacity="0.9" />
            </svg>
          </aside>

          <section class="auth-panel">
            <header class="auth-head">
              <p class="eyebrow"><Sparkles :size="13" /> WELCOME TO YUNDUAN NEST</p>
              <h2 id="auth-title">{{ isRegister ? '在小窝留个名字' : '欢迎回到云端' }}</h2>
              <p>{{ isRegister ? '注册后可以留言、收藏与记录心情。' : '登录后继续写下你的故事。' }}</p>
            </header>

            <div class="auth-tabs" role="tablist">
              <button type="button" :class="{ active: !isRegister }" @click="switchMode('login')">登录</button>
              <button type="button" :class="{ active: isRegister }" @click="switchMode('register')">注册</button>
            </div>

            <form class="auth-form" @submit.prevent="submit">
              <label class="auth-field">
                <span>用户名</span>
                <span class="input-wrap">
                  <User :size="16" />
                  <input
                    ref="firstInput"
                    v-model="form.username"
                    type="text"
                    autocomplete="username"
                    placeholder="3 位以上，字母或数字"
                    maxlength="24"
                  />
                </span>
              </label>

              <label v-if="isRegister" class="auth-field">
                <span>昵称</span>
                <span class="input-wrap">
                  <Smile :size="16" />
                  <input v-model="form.nickname" type="text" autocomplete="nickname" placeholder="想让大家怎么称呼你？" maxlength="20" />
                </span>
              </label>

              <label v-if="isRegister" class="auth-field">
                <span>邮箱</span>
                <span class="input-wrap">
                  <Mail :size="16" />
                  <input v-model="form.email" type="email" autocomplete="email" placeholder="用于找回账号，不会公开" maxlength="80" />
                </span>
              </label>

              <label class="auth-field">
                <span>密码</span>
                <span class="input-wrap">
                  <Lock :size="16" />
                  <input
                    v-model="form.password"
                    :type="showPassword ? 'text' : 'password'"
                    :autocomplete="isRegister ? 'new-password' : 'current-password'"
                    placeholder="至少 6 位"
                    maxlength="64"
                  />
                  <button type="button" class="eye-btn" :aria-label="showPassword ? '隐藏密码' : '显示密码'" @click="showPassword = !showPassword">
                    <EyeOff v-if="showPassword" :size="16" />
                    <Eye v-else :size="16" />
                  </button>
                </span>
              </label>

              <Transition name="err">
                <p v-if="auth.error" class="auth-error">{{ auth.error }}</p>
              </Transition>

              <button class="auth-submit" type="submit" :disabled="!canSubmit || auth.loading">
                <span v-if="auth.loading" class="spinner"></span>
                {{ auth.loading ? '正在连接云端…' : isRegister ? '创建我的小窝账号' : '进入小窝' }}
              </button>

              <p class="auth-demo">演示账号：<code>yunduan</code> / <code>yunduan123</code>（首次启动后端后可用）</p>
            </form>

            <p class="auth-switch">
              {{ isRegister ? '已经有账号了？' : '第一次来？' }}
              <button type="button" @click="switchMode(isRegister ? 'login' : 'register')">
                {{ isRegister ? '直接登录' : '注册一个' }}
              </button>
            </p>
          </section>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 100;
  display: grid;
  place-items: center;
  padding: 24px;
  background: rgba(9, 38, 45, 0.46);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
}

.auth-dialog {
  position: relative;
  width: min(820px, 100%);
  display: grid;
  grid-template-columns: 0.92fr 1.08fr;
  border-radius: 26px;
  overflow: hidden;
  background: #fbfeff;
  box-shadow: 0 40px 100px rgba(5, 35, 45, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.modal-close {
  position: absolute;
  top: 14px;
  right: 14px;
  z-index: 3;
  width: 34px;
  height: 34px;
  display: grid;
  place-items: center;
  border: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.75);
  color: #4d707c;
  cursor: pointer;
  backdrop-filter: blur(8px);
}

.auth-art {
  position: relative;
  overflow: hidden;
  min-height: 520px;
  background: linear-gradient(160deg, #165d70, #0c3f4d 55%, #082f3b);
  padding: 30px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.auth-art-sky {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.auth-sun {
  position: absolute;
  top: 54px;
  right: 48px;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 245, 194, 0.9), rgba(255, 245, 194, 0) 68%);
}

.auth-cloud {
  position: absolute;
  display: block;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  filter: blur(2px);
}
.auth-cloud.c1 {
  width: 190px;
  height: 54px;
  top: 90px;
  left: -30px;
}
.auth-cloud.c2 {
  width: 150px;
  height: 42px;
  top: 160px;
  right: -26px;
  background: rgba(255, 255, 255, 0.1);
}
.auth-cloud.c3 {
  width: 100px;
  height: 30px;
  top: 260px;
  left: 40px;
  background: rgba(255, 255, 255, 0.08);
}

.auth-hill {
  position: absolute;
  inset: auto 0 0;
  width: 100%;
  height: 160px;
}

.auth-art-copy {
  position: relative;
  z-index: 2;
  color: #f2fffb;
}

.auth-quote {
  margin: 26px 0 8px;
  font-family: 'STKaiti', 'KaiTi', 'Microsoft YaHei', serif;
  font-size: 22px;
  line-height: 1.75;
  letter-spacing: 1.5px;
  text-shadow: 0 4px 18px rgba(0, 0, 0, 0.2);
}

.auth-from {
  margin: 0;
  font-size: 11px;
  letter-spacing: 2px;
  opacity: 0.62;
}

.auth-panel {
  padding: 38px 38px 30px;
}

.eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin: 0 0 10px;
  font-size: 9.5px;
  font-weight: 700;
  letter-spacing: 1.6px;
  color: #6f98a5;
}

.auth-head h2 {
  margin: 0 0 7px;
  font-size: 25px;
  letter-spacing: 1px;
  color: #123f4d;
}

.auth-head p {
  margin: 0;
  font-size: 12px;
  color: #71909b;
}

.auth-tabs {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4px;
  margin: 20px 0 18px;
  padding: 4px;
  border-radius: 12px;
  background: #eef7fa;
}

.auth-tabs button {
  border: 0;
  border-radius: 9px;
  padding: 9px;
  background: transparent;
  color: #6b8b96;
  font-size: 13px;
  font-weight: 650;
  cursor: pointer;
  transition: all 0.2s ease;
}

.auth-tabs button.active {
  background: #fff;
  color: #15566b;
  box-shadow: 0 5px 14px rgba(32, 97, 117, 0.12);
}

.auth-form {
  display: grid;
  gap: 12px;
}

.auth-field {
  display: grid;
  gap: 6px;
}

.auth-field > span:first-child {
  font-size: 11.5px;
  font-weight: 650;
  color: #547581;
}

.input-wrap {
  display: flex;
  align-items: center;
  gap: 9px;
  height: 42px;
  padding: 0 12px;
  border: 1px solid #e0eef2;
  border-radius: 11px;
  background: #f8fcfd;
  color: #8fa9b2;
  transition: all 0.2s ease;
}

.input-wrap:focus-within {
  border-color: #78c8dd;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(88, 187, 213, 0.12);
}

.input-wrap input {
  flex: 1;
  min-width: 0;
  border: 0;
  outline: 0;
  background: transparent;
  font-size: 13px;
  color: #1f4856;
}

.input-wrap input::placeholder {
  color: #a8bcc3;
}

.eye-btn {
  border: 0;
  background: transparent;
  color: #92aab2;
  display: grid;
  place-items: center;
  cursor: pointer;
  padding: 2px;
}

.auth-error {
  margin: 0;
  padding: 9px 11px;
  border-radius: 9px;
  background: #fff2f0;
  color: #c2554a;
  font-size: 11.5px;
  line-height: 1.6;
}

.auth-submit {
  height: 44px;
  margin-top: 2px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: 0;
  border-radius: 12px;
  background: linear-gradient(135deg, #2fa8c9, #1c7f9e);
  color: #fff;
  font-size: 13.5px;
  font-weight: 700;
  letter-spacing: 1px;
  cursor: pointer;
  box-shadow: 0 12px 26px rgba(31, 133, 162, 0.28);
  transition: all 0.2s ease;
}

.auth-submit:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 16px 32px rgba(31, 133, 162, 0.34);
}

.auth-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  box-shadow: none;
}

.spinner {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.35);
  border-top-color: #fff;
  animation: spin 0.8s linear infinite;
}

.auth-demo {
  margin: 0;
  text-align: center;
  font-size: 10.5px;
  color: #9bb0b7;
}

.auth-demo code {
  padding: 1px 4px;
  border-radius: 4px;
  background: #eef6f8;
  color: #5f7f89;
}

.auth-switch {
  margin: 16px 0 0;
  text-align: center;
  font-size: 11.5px;
  color: #829ca5;
}

.auth-switch button {
  border: 0;
  background: transparent;
  color: #2184a1;
  font-size: 11.5px;
  font-weight: 700;
  cursor: pointer;
  padding: 0 2px;
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.24s ease;
}
.modal-enter-active .auth-dialog,
.modal-leave-active .auth-dialog {
  transition: transform 0.24s ease;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
.modal-enter-from .auth-dialog,
.modal-leave-to .auth-dialog {
  transform: translateY(16px) scale(0.98);
}

.err-enter-active,
.err-leave-active {
  transition: all 0.2s ease;
}
.err-enter-from,
.err-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 720px) {
  .auth-dialog {
    grid-template-columns: 1fr;
    max-height: calc(100vh - 40px);
    max-height: calc(100dvh - 40px);
    overflow: auto;
    -webkit-overflow-scrolling: touch;
  }
  .auth-art {
    min-height: 150px;
    padding: 22px;
    justify-content: center;
  }
  .auth-quote {
    margin: 14px 0 0;
    font-size: 16px;
  }
  .auth-from,
  .auth-hill {
    display: none;
  }
  .auth-panel {
    padding: 26px 22px 24px;
  }
  .auth-head h2 {
    font-size: 21px;
  }
}
</style>
