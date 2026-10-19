const SCRIPT_ID = 'busuanzi-counter-script'
const HOLDER_ID = 'busuanzi-counter-holder'

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

export function startVisitorCounter(): void {
  if (started || !isPublicHost()) return
  started = true

  if (!document.getElementById(HOLDER_ID)) {
    const holder = document.createElement('div')
    holder.id = HOLDER_ID
    holder.style.display = 'none'
    holder.innerHTML = [
      '<span id="busuanzi_container_site_pv"><span id="busuanzi_value_site_pv">-</span></span>',
      '<span id="busuanzi_container_site_uv"><span id="busuanzi_value_site_uv">-</span></span>',
    ].join('')
    document.body.appendChild(holder)
  }

  if (!document.getElementById(SCRIPT_ID)) {
    const script = document.createElement('script')
    script.id = SCRIPT_ID
    script.async = true
    script.src = 'https://busuanzi.ibruce.info/busuanzi/2.3/busuanzi.pure.mini.js'
    script.referrerPolicy = 'no-referrer-when-downgrade'
    document.head.appendChild(script)
  }
}

export function readVisitorCounts(): { pv: string; uv: string; ready: boolean } {
  const pv = document.getElementById('busuanzi_value_site_pv')?.textContent?.trim() || ''
  const uv = document.getElementById('busuanzi_value_site_uv')?.textContent?.trim() || ''
  const ready = Boolean(pv && uv && pv !== '-' && uv !== '-')
  return { pv: ready ? pv : '—', uv: ready ? uv : '—', ready }
}

export function visitorCounterEnabled(): boolean {
  return isPublicHost()
}
