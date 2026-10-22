import { computed, reactive } from 'vue'
import { api, getToken, setToken } from '../api/client'
import type { AuthUser } from '../types'

const USER_KEY = 'yunduan.user'

function loadUser(): AuthUser | null {
  try {
    const raw = sessionStorage.getItem(USER_KEY)
    // 清理旧版本 localStorage 里遗留的登录态。
    localStorage.removeItem(USER_KEY)
    return raw ? (JSON.parse(raw) as AuthUser) : null
  } catch {
    return null
  }
}

export const auth = reactive({
  open: false,
  mode: 'login' as 'login' | 'register',
  user: loadUser() as AuthUser | null,
  token: getToken(),
  loading: false,
  error: '',
})

export const isAdmin = computed(
  () => auth.user?.role === 'ADMIN' && auth.user?.username === 'yunduan',
)

export function openAuth(mode: 'login' | 'register' = 'login') {
  auth.mode = mode
  auth.error = ''
  auth.open = true
}

export function closeAuth() {
  auth.open = false
  auth.loading = false
  auth.error = ''
}

export function acceptSession(result: { token: string; user: AuthUser }) {
  auth.token = result.token
  auth.user = result.user
  setToken(result.token)
  sessionStorage.setItem(USER_KEY, JSON.stringify(result.user))
  closeAuth()
}

export function logout() {
  auth.user = null
  auth.token = ''
  setToken('')
  sessionStorage.removeItem(USER_KEY)
  localStorage.removeItem(USER_KEY)
}

export async function restoreSession() {
  if (!auth.token) {
    auth.user = null
    sessionStorage.removeItem(USER_KEY)
    return
  }
  try {
    auth.user = await api.me()
    sessionStorage.setItem(USER_KEY, JSON.stringify(auth.user))
  } catch {
    // 浏览器关掉后 sessionStorage 会清空；如果后端重启导致旧 Token 失效，这里也会退出。
    logout()
  }
}

export async function refreshMe() {
  await restoreSession()
}
