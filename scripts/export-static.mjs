import { cpSync, existsSync, mkdirSync, rmSync, writeFileSync } from 'node:fs'
import { resolve } from 'node:path'
import { fileURLToPath } from 'node:url'

const apiBase = (process.env.EXPORT_API_BASE || 'http://127.0.0.1:8080/api').replace(/\/$/, '')
const root = resolve(fileURLToPath(new URL('.', import.meta.url)), '..')
const dataDir = resolve(root, 'frontend/public/data')
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

async function main() {
  console.log(`从 ${apiBase} 导出静态内容...`)
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
