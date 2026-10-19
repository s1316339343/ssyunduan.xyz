import { reactive } from 'vue'
import { api } from '../api/client'
import type { SiteInfo } from '../types'

const fallback: SiteInfo = {
  name: '云端の小窝',
  title: '把日子过成诗，把代码写成歌',
  englishTitle: 'YUNDUAN NEST',
  author: '宋爽',
  nickname: '云端',
  avatar: '',
  tagline: '在云端，记录人间的烟火。',
  bio: '宋爽，网名云端。一个在代码与文字之间来回散步的人。',
  domain: 'yunduan.xyz',
  email: 'hello@yunduan.xyz',
  github: 'https://github.com/',
  location: '中国',
  startDate: '2026-08-01',
  icp: '备案号待填写',
  police: '公网安备待填写',
  techStack: ['Vue 3', 'TypeScript', 'Spring Boot', 'H2'],
  socials: [],
}

export const siteState = reactive<{
  data: SiteInfo
  loaded: boolean
  loading: boolean
}>({
  data: fallback,
  loaded: false,
  loading: false,
})

let inflight: Promise<void> | null = null

export function loadSite(): Promise<void> {
  if (siteState.loaded) return Promise.resolve()
  if (inflight) return inflight
  siteState.loading = true
  inflight = api
    .site()
    .then((data) => {
      siteState.data = { ...fallback, ...data }
      siteState.loaded = true
    })
    .catch(() => {
      /* 后端未启动时保留默认信息，页面依旧可浏览 */
    })
    .finally(() => {
      siteState.loading = false
      inflight = null
    })
  return inflight
}
