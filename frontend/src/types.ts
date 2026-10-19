export interface SiteInfo {
  name: string
  title: string
  englishTitle: string
  author: string
  nickname: string
  avatar: string
  tagline: string
  bio: string
  domain: string
  email: string
  github: string
  location: string
  startDate: string
  icp: string
  police: string
  techStack: string[]
  socials: { label: string; url: string; icon: string }[]
}

export interface Article {
  id: number
  slug: string
  title: string
  excerpt: string
  category: string
  tags: string[]
  cover: string
  publishedAt: string
  updatedAt: string
  views: number
  words: number
  featured: boolean
  published: boolean
  content: string
}

export interface ArticlePayload {
  title: string
  slug: string
  excerpt: string
  content: string
  category: string
  tags: string[]
  cover: string
  publishedAt: string
  featured: boolean
  published: boolean
}

export interface MomentPayload {
  content: string
  images: string[]
}

export interface FriendPayload {
  name: string
  url: string
  description: string
  avatar: string
  art: string
  date: string
}

export interface PhotoPayload {
  title: string
  category: string
  location: string
  takenAt: string
  art: string
  imageUrl: string
}

export interface ChangelogPayload {
  date: string
  version: string
  items: string[]
}

export interface Moment {
  id: number
  content: string
  images: string[]
  createdAt: string
  likes: number
  comments: number
}

export interface Photo {
  id: number
  title: string
  category: string
  location: string
  takenAt: string
  art: string
  imageUrl?: string | null
}

export interface Friend {
  id: number
  name: string
  url: string
  description: string
  avatar: string
  art: string
  date: string
}

export interface Message {
  id: number
  nickname: string
  content: string
  location: string
  createdAt: string
  admin: boolean
  replyTo: string | null
}

export interface ChangelogEntry {
  id: number
  date: string
  version: string
  items: string[]
}

export interface VisitPoint {
  date: string
  pv: number
  uv: number
}

export interface MonthPoint {
  month: string
  count: number
}

export interface Stats {
  articles: number
  tags: number
  categories: number
  words: number
  todayVisits: number
  monthVisits: number
  totalVisits: number
  todayViews: number
  monthViews: number
  totalViews: number
  visitTrend: VisitPoint[]
  monthlyArticles: MonthPoint[]
}

export interface AuthUser {
  username: string
  nickname: string
  avatar: string
  role: string
}

export interface AuthResult {
  token: string
  user: AuthUser
}
