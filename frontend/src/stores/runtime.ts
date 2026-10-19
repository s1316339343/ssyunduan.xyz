import { computed, ref } from 'vue'

export type BackendStatus = boolean | null

export const backendAvailable = ref<BackendStatus>(null)

export const isStaticMode = computed(() => backendAvailable.value === false)

export const STATIC_NOTICE =
  '目前此功能不可用：当前是 GitHub Pages 静态展示版，登录、注册、留言、上传和后台管理需要本地后端。'

let checking: Promise<boolean> | null = null

export function detectBackend(force = false): Promise<boolean> {
  if (!force && backendAvailable.value !== null) {
    return Promise.resolve(backendAvailable.value)
  }
  if (checking) return checking

  checking = (async () => {
    const controller = new AbortController()
    const timer = window.setTimeout(() => controller.abort(), 2500)
    try {
      const response = await fetch('/api/site', {
        headers: { Accept: 'application/json' },
        cache: 'no-store',
        signal: controller.signal,
      })
      const contentType = response.headers.get('content-type') || ''
      if (!response.ok || !contentType.includes('application/json')) {
        throw new Error('not a backend response')
      }
      const data = await response.json()
      backendAvailable.value = Boolean(data && typeof data.name === 'string')
    } catch {
      backendAvailable.value = false
    } finally {
      window.clearTimeout(timer)
      checking = null
    }
    return backendAvailable.value === true
  })()

  return checking
}
