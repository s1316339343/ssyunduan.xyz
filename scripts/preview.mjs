import http from 'node:http'
import { createReadStream, existsSync, statSync } from 'node:fs'
import { extname, join, normalize, resolve } from 'node:path'

const root = resolve(process.cwd(), 'frontend/dist')
const port = Number(process.env.PORT || 4173)
const apiTarget = { host: '127.0.0.1', port: Number(process.env.API_PORT || 8080) }

const mime = {
  '.html': 'text/html; charset=utf-8',
  '.js': 'text/javascript; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.svg': 'image/svg+xml',
  '.json': 'application/json; charset=utf-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.webp': 'image/webp',
  '.ico': 'image/x-icon',
  '.woff2': 'font/woff2',
}

function proxy(req, res) {
  const upstream = http.request(
    {
      host: apiTarget.host,
      port: apiTarget.port,
      path: req.url,
      method: req.method,
      headers: { ...req.headers, host: `${apiTarget.host}:${apiTarget.port}` },
    },
    (upstreamRes) => {
      res.writeHead(upstreamRes.statusCode || 502, upstreamRes.headers)
      upstreamRes.pipe(res)
    },
  )
  upstream.on('error', (error) => {
    res.writeHead(502, { 'content-type': 'application/json; charset=utf-8' })
    res.end(JSON.stringify({ message: `后端未启动：${error.message}` }))
  })
  req.pipe(upstream)
}

const server = http.createServer((req, res) => {
  if (req.url?.startsWith('/api/')) {
    proxy(req, res)
    return
  }

  const requestPath = decodeURIComponent((req.url || '/').split('?')[0])
  let filePath = normalize(join(root, requestPath))
  if (!filePath.startsWith(root)) {
    res.writeHead(403)
    res.end('forbidden')
    return
  }
  if (existsSync(filePath) && statSync(filePath).isDirectory()) {
    filePath = join(filePath, 'index.html')
  }
  if (!existsSync(filePath) || !statSync(filePath).isFile()) {
    if (extname(requestPath)) {
      res.writeHead(404)
      res.end('not found')
      return
    }
    filePath = join(root, 'index.html')
  }

  const type = mime[extname(filePath)] || 'application/octet-stream'
  res.writeHead(200, { 'content-type': type, 'cache-control': 'no-cache' })
  createReadStream(filePath).pipe(res)
})

server.listen(port, '127.0.0.1', () => {
  console.log(`preview ready at http://127.0.0.1:${port}`)
  console.log(`api proxy -> http://${apiTarget.host}:${apiTarget.port}`)
})
