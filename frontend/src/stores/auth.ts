import { computed, reactive } from 'vue'
import { api, getToken, setToken } from '../api/client'
import type { AuthUser } from '../types'

const USER_KEY = 'yunduan.user'

function loadUser(): AuthUser | null {
  try {
    const raw = localStorage.getItem(USER_KEY)
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
  localStorage.setItem(USER_KEY, JSON.stringify(result.user))
  closeAuth()
}

export function logout() {
  auth.user = null
  auth.token = ''
  setToken('')
  localStorage.removeItem(USER_KEY)
}

export async function refreshMe() {
  if (!auth.token) return
  try {
    auth.user = await api.me()
    localStorage.setItem(USER_KEY, JSON.stringify(auth.user))
  } catch {
    logout()
  }
}
