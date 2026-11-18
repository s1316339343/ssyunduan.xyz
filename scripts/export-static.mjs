import { cpSync, existsSync, mkdirSync, readFileSync, rmSync, writeFileSync } from 'node:fs'
import { resolve } from 'node:path'
import { fileURLToPath } from 'node:url'

const apiBase = (process.env.EXPORT_API_BASE || 'http://127.0.0.1:8080/api').replace(/\/$/, '')
const root = resolve(fileURLToPath(new URL('.', import.meta.url)), '..')
const dataDir = resolve(root, 'frontend/public/data')
const visitsPath = resolve(dataDir, 'visits.json')
const uploadsSource = resolve(root, 'backend/data/uploads')
const uploadsTarget = resolve(root, 'frontend/public/uploads')

const endpoints = {
  site: '/site',
  articles: '/articles',
  moments: '/moments',
  gallery: '/gallery',
  friends: '/friends',
  messages: '/messages',
  about: '/about',
  stats: '/stats',
}

async function fetchJson(path) {
  const response = await fetch(`${apiBase}${path}`, {
    headers: { Accept: 'application/json' },
    cache: 'no-store',
  })
  if (!response.ok) {
    throw new Error(`请求 ${apiBase}${path} 失败：HTTP ${response.status}`)
  }
  return response.json()
}

async function fetchCounter(key) {
  for (let attempt = 0; attempt < 4; attempt += 1) {
    try {
      const response = await fetch(
        `https://abacus.jasoncameron.dev/get/ssyunduan-xyz/${key}?t=${Date.now()}`,
        { cache: 'no-store' },
      )
      if (!response.ok) throw new Error(`HTTP ${response.status}`)
      const data = await response.json()
      if (data?.value != null) return Number(data.value)
    } catch {
      /* 免费计数服务偶发 500，保留旧快照 */
    }
    await new Promise((resolve) => setTimeout(resolve, 600 * (attempt + 1)))
  }
  throw new Error('计数器暂时不可用')
}

async function main() {
  console.log(`从 ${apiBase} 导出静态内容...`)
  let previousVisits = null
  if (existsSync(visitsPath)) {
    try {
      previousVisits = JSON.parse(readFileSync(visitsPath, 'utf8'))
    } catch {
      previousVisits = null
    }
  }
  const exported = {}
  for (const [name, path] of Object.entries(endpoints)) {
    exported[name] = await fetchJson(path)
    console.log(`  ✓ ${name}`)
  }

  rmSync(dataDir, { recursive: true, force: true })
  mkdirSync(dataDir, { recursive: true })
  for (const [name, data] of Object.entries(exported)) {
    writeFileSync(resolve(dataDir, `${name}.json`), `${JSON.stringify(data, null, 2)}\n`, 'utf8')
  }

  let visits = previousVisits
  try {
    const [pv, uv] = await Promise.all([fetchCounter('site-pv'), fetchCounter('site-uv')])
    visits = { pv, uv, updatedAt: new Date().toISOString() }
    console.log(`  ✓ 访问快照：${pv} PV / ${uv} UV`)
  } catch {
    console.log('  · 计数器接口暂时不可用，保留上一次访问快照')
  }
  if (visits) {
    writeFileSync(visitsPath, `${JSON.stringify(visits, null, 2)}\n`, 'utf8')
  }

  rmSync(uploadsTarget, { recursive: true, force: true })
  if (existsSync(uploadsSource)) {
    cpSync(uploadsSource, uploadsTarget, { recursive: true })
    console.log('  ✓ 上传图片')
  } else {
    mkdirSync(uploadsTarget, { recursive: true })
    console.log('  · 没有上传图片')
  }

  console.log('')
  console.log(`完成：${dataDir}`)
  console.log('现在可以构建静态站点，或直接把 frontend/public 下的内容提交到 GitHub。')
}

main().catch((error) => {
  console.error('')
  console.error(`导出失败：${error.message}`)
  console.error('请先运行 start-onejar.cmd，确认 http://127.0.0.1:8080 可以访问。')
  process.exitCode = 1
})
