import type {
  AboutData,
  AboutProfile,
  AboutProfilePayload,
  Article,
  ArticlePayload,
  AuthResult,
  AuthUser,
  ChangelogEntry,
  ChangelogPayload,
  Friend,
  FriendPayload,
  Honor,
  HonorPayload,
  Message,
  Moment,
  MomentPayload,
  Photo,
  PhotoPayload,
  SiteInfo,
  Stats,
} from '../types'
import { backendAvailable, detectBackend, STATIC_NOTICE } from '../stores/runtime'

const TOKEN_KEY = 'yunduan.token'
const BASE_URL = import.meta.env.BASE_URL.endsWith('/') ? import.meta.env.BASE_URL : `${import.meta.env.BASE_URL}/`
const STATIC_DATA_MISSING = '静态内容还没有导出。请先在本地运行 export-pages.cmd，再把生成的数据提交到 GitHub。'

export function getToken(): string {
  const token = sessionStorage.getItem(TOKEN_KEY) || ''
  // 旧版本把 Token 放在 localStorage；升级后清掉，避免“看起来登录了、实际没有权限”。
  localStorage.removeItem(TOKEN_KEY)
  return token
}

export function setToken(token: string) {
  if (token) sessionStorage.setItem(TOKEN_KEY, token)
  else sessionStorage.removeItem(TOKEN_KEY)
}

export class ApiError extends Error {
  status: number
  constructor(message: string, status: number) {
    super(message)
    this.status = status
  }
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers)
  if (options.body && !(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json')
  }
  const token = getToken()
  if (token) headers.set('Authorization', `Bearer ${token}`)

  let res: Response
  try {
    res = await fetch(`/api${path}`, { ...options, headers })
  } catch {
    throw new ApiError('无法连接后端服务，请确认 Spring Boot 已在 8080 端口启动。', 0)
  }

  if (!res.ok) {
    let message = `请求失败（${res.status}）`
    try {
      const data = await res.json()
      if (data?.message) message = data.message
    } catch {
      /* ignore */
    }
    throw new ApiError(message, res.status)
  }

  if (res.status === 204) return undefined as T
  return (await res.json()) as T
}

async function backendUp(): Promise<boolean> {
  if (backendAvailable.value === null) await detectBackend()
  return backendAvailable.value === true
}

async function ensureWritable(): Promise<void> {
  if (!(await backendUp())) {
    throw new ApiError(STATIC_NOTICE, 503)
  }
}

async function mutate<T>(path: string, options: RequestInit = {}): Promise<T> {
  await ensureWritable()
  return request<T>(path, options)
}

const staticCache = new Map<string, Promise<unknown>>()

function staticJson<T>(name: string): Promise<T> {
  if (!staticCache.has(name)) {
    const task = fetch(`${BASE_URL}data/${name}.json`, { cache: 'no-store' })
      .then(async (response) => {
        if (!response.ok) throw new ApiError(STATIC_DATA_MISSING, 503)
        return (await response.json()) as T
      })
      .catch((error) => {
        if (error instanceof ApiError) throw error
        throw new ApiError(STATIC_DATA_MISSING, 503)
      })
    staticCache.set(name, task)
  }
  return staticCache.get(name) as Promise<T>
}

function filterArticles(all: Article[], params: { category?: string; tag?: string; q?: string }): Article[] {
  const query = params.q?.trim().toLowerCase() || ''
  return all.filter((article) => {
    if (params.category && article.category !== params.category) return false
    if (params.tag && !article.tags.includes(params.tag)) return false
    if (!query) return true
    const text = `${article.title} ${article.excerpt} ${article.content} ${article.category} ${article.tags.join(' ')}`.toLowerCase()
    return text.includes(query)
  })
}

function staticImageUrl(url: string | null | undefined): string | null | undefined {
  if (!url || /^https?:\/\//.test(url) || url.startsWith('data:')) return url
  if (url.startsWith('/uploads/')) {
    return `${BASE_URL}uploads/${url.slice('/uploads/'.length)}`
  }
  return url
}

export const api = {
  site: async () =>
    (await backendUp()) ? request<SiteInfo>('/site') : staticJson<SiteInfo>('site'),

  articles: async (params: { category?: string; tag?: string; q?: string } = {}) => {
    if (!(await backendUp())) {
      const all = await staticJson<Article[]>('articles')
      return filterArticles(all, params)
    }
    const query = new URLSearchParams()
    if (params.category) query.set('category', params.category)
    if (params.tag) query.set('tag', params.tag)
    if (params.q) query.set('q', params.q)
    const suffix = query.toString() ? `?${query.toString()}` : ''
    return request<Article[]>(`/articles${suffix}`)
  },

  article: async (slug: string) => {
    if (!(await backendUp())) {
      const all = await staticJson<Article[]>('articles')
      const article = all.find((item) => item.slug === slug)
      if (!article) throw new ApiError('没有找到这篇文章。', 404)
      return article
    }
    return request<Article>(`/articles/${encodeURIComponent(slug)}`)
  },

  moments: async () =>
    (await backendUp()) ? request<Moment[]>('/moments') : staticJson<Moment[]>('moments'),

  gallery: async () => {
    if (!(await backendUp())) {
      const photos = await staticJson<Photo[]>('gallery')
      return photos.map((photo) => ({ ...photo, imageUrl: staticImageUrl(photo.imageUrl) }))
    }
    return request<Photo[]>('/gallery')
  },

  friends: async () =>
    (await backendUp()) ? request<Friend[]>('/friends') : staticJson<Friend[]>('friends'),

  messages: async () =>
    (await backendUp()) ? request<Message[]>('/messages') : staticJson<Message[]>('messages'),

  changelog: async () =>
    (await backendUp()) ? request<ChangelogEntry[]>('/changelog') : staticJson<ChangelogEntry[]>('changelog'),

  stats: async () =>
    (await backendUp()) ? request<Stats>('/stats') : staticJson<Stats>('stats'),

  about: async () =>
    (await backendUp()) ? request<AboutData>('/about') : staticJson<AboutData>('about'),

  login: (payload: { username: string; password: string }) =>
    mutate<AuthResult>('/auth/login', { method: 'POST', body: JSON.stringify(payload) }),

  register: (payload: { username: string; password: string; nickname: string; email: string }) =>
    mutate<AuthResult>('/auth/register', { method: 'POST', body: JSON.stringify(payload) }),

  me: async () => {
    await ensureWritable()
    return request<AuthUser>('/auth/me')
  },

  createMessage: (payload: { nickname: string; content: string }) =>
    mutate<Message>('/messages', { method: 'POST', body: JSON.stringify(payload) }),

  trackVisit: async () => {
    if (!(await backendUp())) return { ok: false }
    return request<{ ok: boolean }>('/visits/track', { method: 'POST' }).catch(() => ({ ok: false }))
  },

  adminArticles: () => mutate<Article[]>('/admin/articles'),
  createArticle: (payload: ArticlePayload) =>
    mutate<Article>('/admin/articles', { method: 'POST', body: JSON.stringify(payload) }),
  updateArticle: (id: number, payload: ArticlePayload) =>
    mutate<Article>(`/admin/articles/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  deleteArticle: (id: number) => mutate<{ ok: boolean }>(`/admin/articles/${id}`, { method: 'DELETE' }),

  adminMoments: () => mutate<Moment[]>('/admin/moments'),
  createMoment: (payload: MomentPayload) =>
    mutate<Moment>('/admin/moments', { method: 'POST', body: JSON.stringify(payload) }),
  updateMoment: (id: number, payload: MomentPayload) =>
    mutate<Moment>(`/admin/moments/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  deleteMoment: (id: number) => mutate<{ ok: boolean }>(`/admin/moments/${id}`, { method: 'DELETE' }),

  adminFriends: () => mutate<Friend[]>('/admin/friends'),
  createFriend: (payload: FriendPayload) =>
    mutate<Friend>('/admin/friends', { method: 'POST', body: JSON.stringify(payload) }),
  updateFriend: (id: number, payload: FriendPayload) =>
    mutate<Friend>(`/admin/friends/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  deleteFriend: (id: number) => mutate<{ ok: boolean }>(`/admin/friends/${id}`, { method: 'DELETE' }),

  adminPhotos: () => mutate<Photo[]>('/admin/photos'),
  createPhoto: (payload: PhotoPayload) =>
    mutate<Photo>('/admin/photos', { method: 'POST', body: JSON.stringify(payload) }),
  updatePhoto: (id: number, payload: PhotoPayload) =>
    mutate<Photo>(`/admin/photos/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  deletePhoto: (id: number) => mutate<{ ok: boolean }>(`/admin/photos/${id}`, { method: 'DELETE' }),
  uploadImage: async (file: File) => {
    await ensureWritable()
    const form = new FormData()
    form.append('file', file)
    return request<{ url: string }>('/admin/uploads', { method: 'POST', body: form })
  },

  adminChangelog: () => mutate<ChangelogEntry[]>('/admin/changelog'),
  createChangelog: (payload: ChangelogPayload) =>
    mutate<ChangelogEntry>('/admin/changelog', { method: 'POST', body: JSON.stringify(payload) }),
  updateChangelog: (id: number, payload: ChangelogPayload) =>
    mutate<ChangelogEntry>(`/admin/changelog/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  deleteChangelog: (id: number) => mutate<{ ok: boolean }>(`/admin/changelog/${id}`, { method: 'DELETE' }),

  adminAbout: () => mutate<AboutData>('/admin/about'),
  updateAbout: (payload: AboutProfilePayload) =>
    mutate<AboutProfile>('/admin/about', { method: 'PUT', body: JSON.stringify(payload) }),
  createHonor: (payload: HonorPayload) =>
    mutate<Honor>('/admin/about/honors', { method: 'POST', body: JSON.stringify(payload) }),
  updateHonor: (id: number, payload: HonorPayload) =>
    mutate<Honor>(`/admin/about/honors/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  deleteHonor: (id: number) => mutate<{ ok: boolean }>(`/admin/about/honors/${id}`, { method: 'DELETE' }),

  deleteMessage: (id: number) => mutate<{ ok: boolean }>(`/admin/messages/${id}`, { method: 'DELETE' }),

  changePassword: (payload: { oldPassword: string; newPassword: string }) =>
    mutate<{ ok: boolean }>('/admin/password', { method: 'PUT', body: JSON.stringify(payload) }),
}
