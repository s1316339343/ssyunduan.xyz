const COUNTER_NAMESPACE = 'ssyunduan-xyz'
const PV_KEY = 'site-pv'
const UV_KEY = 'site-uv'
const SEEN_KEY = 'yunduan.visitor.seen'

let started = false

function isPublicHost(): boolean {
  const host = window.location.hostname
  if (!host) return false
  if (host === 'localhost' || host === '127.0.0.1' || host === '::1') return false
  if (host.startsWith('192.168.') || host.startsWith('10.')) return false
  if (/^172\.(1[6-9]|2\d|3[0-1])\./.test(host)) return false
  if (host.endsWith('.local')) return false
  return true
}

function jsonp(url: string): Promise<number> {
  return new Promise((resolve, reject) => {
    const callbackName = `__yunduan_counter_${Date.now()}_${Math.floor(Math.random() * 100000)}`
    const script = document.createElement('script')
    const timer = window.setTimeout(() => {
      cleanup()
      reject(new Error('counter timeout'))
    }, 8000)

    function cleanup() {
      window.clearTimeout(timer)
      delete (window as unknown as Record<string, unknown>)[callbackName]
      script.remove()
    }

    ;(window as unknown as Record<string, unknown>)[callbackName] = (data: { value?: number }) => {
      cleanup()
      resolve(Number(data?.value) || 0)
    }
    script.src = `${url}${url.includes('?') ? '&' : '?'}callback=${callbackName}`
    script.async = true
    script.onerror = () => {
      cleanup()
      reject(new Error('counter failed'))
    }
    document.head.appendChild(script)
  })
}

function hit(key: string): Promise<number> {
  return jsonp(`https://abacus.jasoncameron.dev/hit/${COUNTER_NAMESPACE}/${key}`)
}

export function startVisitorCounter(): void {
  if (started || !isPublicHost()) return
  started = true

  // 每次打开公网页面记一次浏览。
  hit(PV_KEY).catch(() => undefined)

  // 同一个浏览器只记一次独立访客，之后靠 localStorage 去重。
  try {
    if (!localStorage.getItem(SEEN_KEY)) {
      hit(UV_KEY)
        .then(() => localStorage.setItem(SEEN_KEY, '1'))
        .catch(() => undefined)
    }
  } catch {
    /* 隐私模式禁用 localStorage 时只统计 PV */
  }
}

export function visitorCounterEnabled(): boolean {
  return isPublicHost()
}
