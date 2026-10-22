<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Award,
  BookOpen,
  Camera,
  Eye,
  FileText,
  ImagePlus,
  Images,
  KeyRound,
  Link2,
  MessageCircle,
  Pencil,
  PenLine,
  Plus,
  RefreshCw,
  Save,
  ShieldCheck,
  Trash2,
  UserRound,
  X,
} from 'lucide-vue-next'
import PageHeader from '../components/PageHeader.vue'
import ArtCover from '../components/ArtCover.vue'
import { api } from '../api/client'
import { auth, isAdmin, logout } from '../stores/auth'
import { notify } from '../stores/toast'
import type {
  AboutData,
  AboutProfilePayload,
  Article,
  ArticlePayload,
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
} from '../types'
import { formatDate, relativeTime } from '../utils/format'

type TabKey = 'articles' | 'moments' | 'friends' | 'photos' | 'visits' | 'about' | 'messages' | 'account'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const saving = ref(false)
const error = ref('')
const activeTab = ref<TabKey>('articles')
const search = ref('')
const VISITS_WIDGET_URL = 'https://s1316339343.github.io/ssyunduan.xyz/visits.html'

const articles = ref<Article[]>([])
const moments = ref<Moment[]>([])
const friends = ref<Friend[]>([])
const photos = ref<Photo[]>([])
const changelog = ref<ChangelogEntry[]>([])
const messages = ref<Message[]>([])

const coverChoices = [
  'cloud', 'code', 'api', 'web', 'deploy', 'book', 'night', 'letter', 'hook', 'valley',
  'mountain', 'town', 'sea', 'forest', 'sunset', 'cafe', 'pen', 'cat', 'flower',
]

const tabs = [
  { key: 'articles' as const, label: '文章', icon: FileText },
  { key: 'moments' as const, label: '随笔', icon: PenLine },
  { key: 'friends' as const, label: '友链', icon: Link2 },
  { key: 'photos' as const, label: '相册', icon: Images },
  { key: 'visits' as const, label: '访问统计', icon: Eye },
  { key: 'about' as const, label: '关于我', icon: UserRound },
  { key: 'messages' as const, label: '留言', icon: MessageCircle },
  { key: 'account' as const, label: '账号安全', icon: KeyRound },
]

function localToday(): string {
  const date = new Date()
  const pad = (value: number) => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
}

function splitList(value: string): string[] {
  return value
    .split(/[,，\n]/)
    .map((item) => item.trim())
    .filter(Boolean)
}

const articleForm = reactive({
  id: null as number | null,
  title: '',
  slug: '',
  excerpt: '',
  content: '',
  category: '技术手记',
  tags: '',
  cover: 'cloud',
  publishedAt: localToday(),
  featured: false,
  published: true,
})
const articleEditing = ref(false)

const momentForm = reactive({
  id: null as number | null,
  content: '',
  images: '',
})
const momentEditing = ref(false)

const friendForm = reactive({
  id: null as number | null,
  name: '',
  url: '',
  description: '',
  avatar: 'cloud',
  art: 'cloud',
  date: localToday(),
})
const friendEditing = ref(false)

const photoForm = reactive({
  id: null as number | null,
  title: '',
  category: '风景',
  location: '',
  takenAt: localToday(),
  art: 'mountain',
  imageUrl: '',
})
const photoEditing = ref(false)
const photoUploading = ref(false)
const photoFileInput = ref<HTMLInputElement | null>(null)

const changelogForm = reactive({
  id: null as number | null,
  date: localToday(),
  version: 'v1.0.0',
  items: '',
})
const changelogEditing = ref(false)

const aboutProfile = reactive<AboutProfilePayload>({
  name: '宋爽',
  nickname: '云端',
  birthDate: '2003-10-14',
  hometown: '河北唐山',
  phone: '',
  email: '',
  status: '',
  intro: '',
  goal: '',
  collegeSchool: '河北工业职业技术大学',
  collegeMajor: '移动应用开发',
  collegePeriod: '2023.09 - 2026.06',
  collegeCourses: '',
  collegeExperience: '',
  bachelorSchool: '沧州交通学院',
  bachelorMajor: '物联网工程',
  bachelorPeriod: '2026.09 - 至今',
  bachelorCourses: '',
  bachelorExperience: '',
  skills: '',
})
const honors = ref<Honor[]>([])
const honorForm = reactive({
  id: null as number | null,
  title: '',
  issuer: '',
  awardedAt: '',
  imageUrl: '',
  description: '',
  priority: 0,
  visible: true,
})
const honorEditing = ref(false)
const honorUploading = ref(false)
const honorFileInput = ref<HTMLInputElement | null>(null)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirm: '',
})

const filteredArticles = computed(() => {
  const keyword = search.value.trim().toLowerCase()
  if (!keyword) return articles.value
  return articles.value.filter((article) =>
    `${article.title} ${article.category} ${article.tags.join(' ')}`.toLowerCase().includes(keyword),
  )
})

const categories = computed(() => {
  const fromArticles = articles.value.map((article) => article.category)
  return Array.from(new Set([...fromArticles, '技术手记', '生活随笔', '项目复盘']))
})

const stats = computed(() => ({
  articles: articles.value.length,
  drafts: articles.value.filter((article) => !article.published).length,
  moments: moments.value.length,
  messages: messages.value.length,
  photos: photos.value.length,
  friends: friends.value.length,
}))

function tabCount(key: TabKey): number {
  if (key === 'articles') return articles.value.length
  if (key === 'moments') return moments.value.length
  if (key === 'friends') return friends.value.length
  if (key === 'photos') return photos.value.length
  if (key === 'about') return honors.value.length
  if (key === 'messages') return messages.value.length
  return 0
}

async function loadAll() {
  if (!isAdmin.value) return
  loading.value = true
  error.value = ''
  try {
    const [articleData, momentData, friendData, photoData, messageData, aboutData] = await Promise.all([
      api.adminArticles(),
      api.adminMoments(),
      api.adminFriends(),
      api.adminPhotos(),
      api.messages(),
      api.adminAbout(),
    ])
    articles.value = articleData
    moments.value = momentData
    friends.value = friendData
    photos.value = photoData
    messages.value = messageData
    applyAbout(aboutData)
    openArticleFromQuery()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '内容加载失败。'
  } finally {
    loading.value = false
  }
}

function openArticleFromQuery() {
  const slug = typeof route.query.edit === 'string' ? route.query.edit : ''
  if (!slug) return
  const article = articles.value.find((item) => item.slug === slug)
  if (article) {
    activeTab.value = 'articles'
    editArticle(article)
  }
}

async function refreshArticles() {
  articles.value = await api.adminArticles()
}

async function refreshMoments() {
  moments.value = await api.adminMoments()
}

async function refreshFriends() {
  friends.value = await api.adminFriends()
}

async function refreshPhotos() {
  photos.value = await api.adminPhotos()
}

function applyAbout(data: AboutData) {
  Object.assign(aboutProfile, data.profile)
  honors.value = data.honors
}

async function refreshAbout() {
  applyAbout(await api.adminAbout())
}

async function saveAboutProfile() {
  if (!aboutProfile.name.trim()) {
    notify('姓名不能为空。', 'warn')
    return
  }
  saving.value = true
  try {
    const { id: _ignored, ...payload } = aboutProfile as AboutProfilePayload & { id?: number }
    await api.updateAbout(payload)
    notify('关于我页面已保存。')
    await refreshAbout()
  } catch (e) {
    notify(e instanceof Error ? e.message : '保存失败。', 'warn')
  } finally {
    saving.value = false
  }
}

function resetHonor() {
  Object.assign(honorForm, {
    id: null,
    title: '',
    issuer: '',
    awardedAt: '',
    imageUrl: '',
    description: '',
    priority: 0,
    visible: true,
  })
}

function newHonor() {
  resetHonor()
  honorEditing.value = true
}

function editHonor(honor: Honor) {
  Object.assign(honorForm, {
    id: honor.id,
    title: honor.title,
    issuer: honor.issuer || '',
    awardedAt: honor.awardedAt || '',
    imageUrl: honor.imageUrl || '',
    description: honor.description || '',
    priority: honor.priority,
    visible: honor.visible,
  })
  honorEditing.value = true
}

async function saveHonor() {
  if (!honorForm.title.trim()) {
    notify('荣誉名称不能为空。', 'warn')
    return
  }
  saving.value = true
  const payload: HonorPayload = {
    title: honorForm.title.trim(),
    issuer: honorForm.issuer.trim(),
    awardedAt: honorForm.awardedAt.trim(),
    imageUrl: honorForm.imageUrl,
    description: honorForm.description.trim(),
    priority: Number(honorForm.priority) || 0,
    visible: honorForm.visible,
  }
  try {
    if (honorForm.id) await api.updateHonor(honorForm.id, payload)
    else await api.createHonor(payload)
    notify('荣誉已保存。')
    honorEditing.value = false
    resetHonor()
    await refreshAbout()
  } catch (e) {
    notify(e instanceof Error ? e.message : '保存失败。', 'warn')
  } finally {
    saving.value = false
  }
}

async function deleteHonor(honor: Honor) {
  if (!window.confirm(`确定删除「${honor.title}」吗？`)) return
  try {
    await api.deleteHonor(honor.id)
    notify('荣誉已删除。')
    await refreshAbout()
  } catch (e) {
    notify(e instanceof Error ? e.message : '删除失败。', 'warn')
  }
}

function chooseHonorImage() {
  honorFileInput.value?.click()
}

async function uploadHonorImage(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  if (file.size > 10 * 1024 * 1024) {
    notify('图片不能超过 10MB。', 'warn')
    input.value = ''
    return
  }
  honorUploading.value = true
  try {
    const result = await api.uploadImage(file)
    honorForm.imageUrl = result.url
    notify('证书图片上传成功。')
  } catch (e) {
    notify(e instanceof Error ? e.message : '图片上传失败。', 'warn')
  } finally {
    honorUploading.value = false
    input.value = ''
  }
}

function honorImageUrl(url: string | null): string {
  if (!url) return ''
  if (/^https?:\/\//.test(url)) return url
  return `${import.meta.env.BASE_URL}${url.replace(/^\//, '')}`
}

async function refreshChangelog() {
  changelog.value = await api.adminChangelog()
}

function resetArticle() {
  Object.assign(articleForm, {
    id: null,
    title: '',
    slug: '',
    excerpt: '',
    content: '',
    category: '技术手记',
    tags: '',
    cover: 'cloud',
    publishedAt: localToday(),
    featured: false,
    published: true,
  })
}

function newArticle() {
  resetArticle()
  articleEditing.value = true
}

function editArticle(article: Article) {
  Object.assign(articleForm, {
    id: article.id,
    title: article.title,
    slug: article.slug,
    excerpt: article.excerpt || '',
    content: article.content,
    category: article.category,
    tags: article.tags.join(', '),
    cover: article.cover || 'cloud',
    publishedAt: article.publishedAt,
    featured: article.featured,
    published: article.published,
  })
  articleEditing.value = true
}

function cancelArticle() {
  articleEditing.value = false
  resetArticle()
}

async function saveArticle(publish?: boolean) {
  if (publish !== undefined) articleForm.published = publish
  if (!articleForm.title.trim()) {
    notify('标题不能为空。', 'warn')
    return
  }
  if (!articleForm.content.trim()) {
    notify('正文不能为空。', 'warn')
    return
  }
  saving.value = true
  const payload: ArticlePayload = {
    title: articleForm.title.trim(),
    slug: articleForm.slug.trim(),
    excerpt: articleForm.excerpt.trim(),
    content: articleForm.content,
    category: articleForm.category.trim() || '未分类',
    tags: splitList(articleForm.tags),
    cover: articleForm.cover,
    publishedAt: articleForm.publishedAt || localToday(),
    featured: articleForm.featured,
    published: articleForm.published,
  }
  try {
    if (articleForm.id) {
      await api.updateArticle(articleForm.id, payload)
    } else {
      await api.createArticle(payload)
    }
    notify(articleForm.published ? '文章已保存并发布。' : '草稿已保存。')
    articleEditing.value = false
    resetArticle()
    await refreshArticles()
  } catch (e) {
    notify(e instanceof Error ? e.message : '保存失败。', 'warn')
  } finally {
    saving.value = false
  }
}

async function deleteArticle(article: Article) {
  if (!window.confirm(`确定删除《${article.title}》吗？删除后不可恢复。`)) return
  try {
    await api.deleteArticle(article.id)
    if (articleForm.id === article.id) cancelArticle()
    notify('文章已删除。')
    await refreshArticles()
  } catch (e) {
    notify(e instanceof Error ? e.message : '删除失败。', 'warn')
  }
}

function previewArticle(article: Article) {
  router.push({ name: 'article', params: { slug: article.slug } })
}

function resetMoment() {
  Object.assign(momentForm, { id: null, content: '', images: '' })
}

function newMoment() {
  resetMoment()
  momentEditing.value = true
}

function editMoment(moment: Moment) {
  Object.assign(momentForm, {
    id: moment.id,
    content: moment.content,
    images: moment.images.join(', '),
  })
  momentEditing.value = true
}

async function saveMoment() {
  if (!momentForm.content.trim()) {
    notify('随笔内容不能为空。', 'warn')
    return
  }
  saving.value = true
  const payload: MomentPayload = {
    content: momentForm.content.trim(),
    images: splitList(momentForm.images),
  }
  try {
    if (momentForm.id) await api.updateMoment(momentForm.id, payload)
    else await api.createMoment(payload)
    notify('随笔已保存。')
    momentEditing.value = false
    resetMoment()
    await refreshMoments()
  } catch (e) {
    notify(e instanceof Error ? e.message : '保存失败。', 'warn')
  } finally {
    saving.value = false
  }
}

async function deleteMoment(moment: Moment) {
  if (!window.confirm('确定删除这条随笔吗？')) return
  try {
    await api.deleteMoment(moment.id)
    notify('随笔已删除。')
    await refreshMoments()
  } catch (e) {
    notify(e instanceof Error ? e.message : '删除失败。', 'warn')
  }
}

function resetFriend() {
  Object.assign(friendForm, {
    id: null,
    name: '',
    url: '',
    description: '',
    avatar: 'cloud',
    art: 'cloud',
    date: localToday(),
  })
}

function newFriend() {
  resetFriend()
  friendEditing.value = true
}

function editFriend(friend: Friend) {
  Object.assign(friendForm, {
    id: friend.id,
    name: friend.name,
    url: friend.url,
    description: friend.description || '',
    avatar: friend.avatar || 'cloud',
    art: friend.art || 'cloud',
    date: friend.date || localToday(),
  })
  friendEditing.value = true
}

async function saveFriend() {
  if (!friendForm.name.trim() || !friendForm.url.trim()) {
    notify('友链名称和地址不能为空。', 'warn')
    return
  }
  saving.value = true
  const payload: FriendPayload = {
    name: friendForm.name.trim(),
    url: friendForm.url.trim(),
    description: friendForm.description.trim(),
    avatar: friendForm.avatar,
    art: friendForm.art,
    date: friendForm.date || localToday(),
  }
  try {
    if (friendForm.id) await api.updateFriend(friendForm.id, payload)
    else await api.createFriend(payload)
    notify('友链已保存。')
    friendEditing.value = false
    resetFriend()
    await refreshFriends()
  } catch (e) {
    notify(e instanceof Error ? e.message : '保存失败。', 'warn')
  } finally {
    saving.value = false
  }
}

async function deleteFriend(friend: Friend) {
  if (!window.confirm(`确定删除友链「${friend.name}」吗？`)) return
  try {
    await api.deleteFriend(friend.id)
    notify('友链已删除。')
    await refreshFriends()
  } catch (e) {
    notify(e instanceof Error ? e.message : '删除失败。', 'warn')
  }
}

function resetPhoto() {
  Object.assign(photoForm, {
    id: null,
    title: '',
    category: '风景',
    location: '',
    takenAt: localToday(),
    art: 'mountain',
    imageUrl: '',
  })
}

function newPhoto() {
  resetPhoto()
  photoEditing.value = true
}

function editPhoto(photo: Photo) {
  Object.assign(photoForm, {
    id: photo.id,
    title: photo.title,
    category: photo.category,
    location: photo.location || '',
    takenAt: photo.takenAt || localToday(),
    art: photo.art || 'mountain',
    imageUrl: photo.imageUrl || '',
  })
  photoEditing.value = true
}

function choosePhotoFile() {
  photoFileInput.value?.click()
}

async function uploadPhotoFile(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  if (file.size > 10 * 1024 * 1024) {
    notify('图片不能超过 10MB。', 'warn')
    input.value = ''
    return
  }
  photoUploading.value = true
  try {
    const result = await api.uploadImage(file)
    photoForm.imageUrl = result.url
    notify('图片上传成功，保存照片后就会展示在相册里。')
  } catch (e) {
    notify(e instanceof Error ? e.message : '图片上传失败。', 'warn')
  } finally {
    photoUploading.value = false
    input.value = ''
  }
}

function clearPhotoImage() {
  photoForm.imageUrl = ''
  if (photoFileInput.value) photoFileInput.value.value = ''
}

async function savePhoto() {
  if (!photoForm.title.trim()) {
    notify('照片标题不能为空。', 'warn')
    return
  }
  saving.value = true
  const payload: PhotoPayload = {
    title: photoForm.title.trim(),
    category: photoForm.category.trim() || '风景',
    location: photoForm.location.trim(),
    takenAt: photoForm.takenAt || localToday(),
    art: photoForm.art,
    imageUrl: photoForm.imageUrl,
  }
  try {
    if (photoForm.id) await api.updatePhoto(photoForm.id, payload)
    else await api.createPhoto(payload)
    notify('照片已保存。')
    photoEditing.value = false
    resetPhoto()
    await refreshPhotos()
  } catch (e) {
    notify(e instanceof Error ? e.message : '保存失败。', 'warn')
  } finally {
    saving.value = false
  }
}

async function deletePhoto(photo: Photo) {
  if (!window.confirm(`确定删除「${photo.title}」吗？`)) return
  try {
    await api.deletePhoto(photo.id)
    notify('照片已删除。')
    await refreshPhotos()
  } catch (e) {
    notify(e instanceof Error ? e.message : '删除失败。', 'warn')
  }
}

function resetChangelog() {
  Object.assign(changelogForm, { id: null, date: localToday(), version: 'v1.0.0', items: '' })
}

function newChangelog() {
  resetChangelog()
  changelogEditing.value = true
}

function editChangelog(entry: ChangelogEntry) {
  Object.assign(changelogForm, {
    id: entry.id,
    date: entry.date,
    version: entry.version || 'v1.0.0',
    items: entry.items.join('\n'),
  })
  changelogEditing.value = true
}

async function saveChangelog() {
  const items = changelogForm.items
    .split('\n')
    .map((item) => item.trim())
    .filter(Boolean)
  if (!items.length) {
    notify('至少写一条更新内容。', 'warn')
    return
  }
  saving.value = true
  const payload: ChangelogPayload = {
    date: changelogForm.date || localToday(),
    version: changelogForm.version.trim() || 'v1.0.0',
    items,
  }
  try {
    if (changelogForm.id) await api.updateChangelog(changelogForm.id, payload)
    else await api.createChangelog(payload)
    notify('更新日志已保存。')
    changelogEditing.value = false
    resetChangelog()
    await refreshChangelog()
  } catch (e) {
    notify(e instanceof Error ? e.message : '保存失败。', 'warn')
  } finally {
    saving.value = false
  }
}

async function deleteChangelog(entry: ChangelogEntry) {
  if (!window.confirm(`确定删除 ${entry.date} 的更新日志吗？`)) return
  try {
    await api.deleteChangelog(entry.id)
    notify('更新日志已删除。')
    await refreshChangelog()
  } catch (e) {
    notify(e instanceof Error ? e.message : '删除失败。', 'warn')
  }
}

async function deleteMessage(message: Message) {
  if (!window.confirm(`确定删除 ${message.nickname} 的这条留言吗？`)) return
  try {
    await api.deleteMessage(message.id)
    messages.value = messages.value.filter((item) => item.id !== message.id)
    notify('留言已删除。')
  } catch (e) {
    notify(e instanceof Error ? e.message : '删除失败。', 'warn')
  }
}

async function changePassword() {
  if (passwordForm.newPassword.length < 6) {
    notify('新密码至少 6 位。', 'warn')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirm) {
    notify('两次输入的新密码不一致。', 'warn')
    return
  }
  saving.value = true
  try {
    await api.changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
    })
    notify('密码已修改，请重新登录。')
    logout()
    router.push({ name: 'home' })
  } catch (e) {
    notify(e instanceof Error ? e.message : '修改失败。', 'warn')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  if (!isAdmin.value) {
    router.replace({ name: 'home' })
    return
  }
  loadAll()
})
</script>

<template>
  <div class="page admin-page">
    <div class="container">
      <PageHeader
        eyebrow="STUDIO"
        title="内容管理"
        subtitle="这里是站长的编辑室。只有 yunduan 账号可以进入，普通注册用户只能浏览和留言。"
      />

      <div v-if="error" class="error-box">{{ error }}</div>

      <div v-if="loading" class="loading-block admin-loading">
        正在打开编辑室<span class="loading-dots"><i></i><i></i><i></i></span>
      </div>

      <div v-else class="admin-shell">
        <aside class="admin-nav glass-card">
          <div class="admin-identity">
            <span class="identity-icon"><ShieldCheck :size="18" /></span>
            <div>
              <strong>{{ auth.user?.nickname }}</strong>
              <span>@{{ auth.user?.username }} · 站长</span>
            </div>
          </div>

          <nav class="admin-tabs">
            <button
              v-for="tab in tabs"
              :key="tab.key"
              type="button"
              class="admin-tab"
              :class="{ active: activeTab === tab.key }"
              @click="activeTab = tab.key"
            >
              <component :is="tab.icon" :size="16" />
              <span>{{ tab.label }}</span>
              <em v-if="tabCount(tab.key)">{{ tabCount(tab.key) }}</em>
            </button>
          </nav>

          <div class="admin-stats">
            <div><b>{{ stats.articles }}</b><span>文章</span></div>
            <div><b>{{ stats.drafts }}</b><span>草稿</span></div>
            <div><b>{{ stats.messages }}</b><span>留言</span></div>
          </div>
        </aside>

        <main class="admin-main">
          <!-- 文章 -->
          <section v-if="activeTab === 'articles'">
            <div class="admin-toolbar">
              <div>
                <p class="eyebrow">ARTICLES</p>
                <h2>文章</h2>
                <p>发布、修改、下架或删除文章。草稿不会出现在公开博客里。</p>
              </div>
              <div class="toolbar-actions">
                <button class="btn btn-primary" type="button" @click="newArticle">
                  <Plus :size="14" /> 新建文章
                </button>
              </div>
            </div>

            <div v-if="articleEditing" class="editor-card glass-card">
              <div class="editor-head">
                <h3>{{ articleForm.id ? '编辑文章' : '新建文章' }}</h3>
                <button type="button" class="icon-close" aria-label="关闭编辑" @click="cancelArticle"><X :size="17" /></button>
              </div>

              <div class="form-grid">
                <label class="field span-2">
                  <span>标题 *</span>
                  <input v-model="articleForm.title" type="text" maxlength="200" placeholder="给这篇文章起个名字" />
                </label>
                <label class="field">
                  <span>Slug（URL 标识）</span>
                  <input v-model="articleForm.slug" type="text" maxlength="120" placeholder="留空则自动生成" />
                </label>
                <label class="field">
                  <span>分类 *</span>
                  <input v-model="articleForm.category" list="article-categories" maxlength="40" placeholder="技术手记" />
                  <datalist id="article-categories">
                    <option v-for="category in categories" :key="category" :value="category" />
                  </datalist>
                </label>
                <label class="field">
                  <span>发布日期</span>
                  <input v-model="articleForm.publishedAt" type="date" />
                </label>
                <label class="field">
                  <span>封面</span>
                  <select v-model="articleForm.cover">
                    <option v-for="cover in coverChoices" :key="cover" :value="cover">{{ cover }}</option>
                  </select>
                </label>
                <label class="field span-2">
                  <span>摘要</span>
                  <textarea v-model="articleForm.excerpt" rows="2" maxlength="500" placeholder="留空会从正文自动截取"></textarea>
                </label>
                <label class="field span-2">
                  <span>标签（用逗号分隔）</span>
                  <input v-model="articleForm.tags" type="text" placeholder="Vue3, 前端, 生活" />
                </label>
                <label class="field span-2">
                  <span>正文（支持 HTML）*</span>
                  <textarea v-model="articleForm.content" rows="14" placeholder="<p>写点什么吧…</p>"></textarea>
                </label>
              </div>

              <div class="form-options">
                <label class="check-line"><input v-model="articleForm.published" type="checkbox" /> 发布到公开博客</label>
                <label class="check-line"><input v-model="articleForm.featured" type="checkbox" /> 设为精选</label>
              </div>

              <div class="cover-preview">
                <span>封面预览</span>
                <ArtCover :variant="articleForm.cover" ratio="16 / 6" />
              </div>

              <div class="editor-actions">
                <button class="btn btn-ghost" type="button" @click="cancelArticle">取消</button>
                <button class="btn btn-soft" type="button" :disabled="saving" @click="saveArticle(false)">
                  <Save :size="14" /> 保存草稿
                </button>
                <button class="btn btn-primary" type="button" :disabled="saving" @click="saveArticle(true)">
                  <Save :size="14" /> 保存并发布
                </button>
              </div>
            </div>

            <div class="admin-list-toolbar">
              <h3>全部文章 <span>{{ articles.length }}</span></h3>
              <input v-model="search" type="search" placeholder="搜索标题 / 分类 / 标签" />
            </div>

            <div v-if="filteredArticles.length" class="admin-list">
              <article v-for="article in filteredArticles" :key="article.id" class="admin-row">
                <ArtCover :variant="article.cover || 'cloud'" ratio="16 / 10" class="admin-thumb" />
                <div class="admin-row-copy">
                  <div class="row-title-line">
                    <h3>{{ article.title }}</h3>
                    <span class="status-badge" :class="article.published ? 'published' : 'draft'">
                      {{ article.published ? '已发布' : '草稿' }}
                    </span>
                    <span v-if="article.featured" class="status-badge featured">精选</span>
                  </div>
                  <p>{{ article.category }} · {{ formatDate(article.publishedAt) }} · {{ article.views }} 次阅读 · {{ article.words }} 字</p>
                  <p class="row-tags">{{ article.tags.join(' · ') || '暂无标签' }}</p>
                </div>
                <div class="row-actions">
                  <button type="button" class="row-btn" @click="previewArticle(article)"><Eye :size="14" /> 预览</button>
                  <button type="button" class="row-btn" @click="editArticle(article)"><Pencil :size="14" /> 编辑</button>
                  <button type="button" class="row-btn danger" @click="deleteArticle(article)"><Trash2 :size="14" /> 删除</button>
                </div>
              </article>
            </div>
            <div v-else class="admin-empty glass-card">
              <BookOpen :size="24" />
              <h3>{{ search ? '没有匹配的文章' : '还没有文章' }}</h3>
              <p>{{ search ? '换个关键词试试。' : '点击右上角「新建文章」，写下第一篇。' }}</p>
            </div>
          </section>

          <!-- 随笔 -->
          <section v-else-if="activeTab === 'moments'">
            <div class="admin-toolbar">
              <div><p class="eyebrow">MOMENTS</p><h2>随笔</h2><p>记录短一点的心情，发布后会出现在随笔页。</p></div>
              <button class="btn btn-primary" type="button" @click="newMoment"><Plus :size="14" /> 新建随笔</button>
            </div>

            <div v-if="momentEditing" class="editor-card glass-card">
              <div class="editor-head"><h3>{{ momentForm.id ? '编辑随笔' : '新建随笔' }}</h3><button type="button" class="icon-close" @click="momentEditing = false; resetMoment()"><X :size="17" /></button></div>
              <div class="form-grid">
                <label class="field span-2"><span>内容 *</span><textarea v-model="momentForm.content" rows="5" placeholder="此刻在想什么…"></textarea></label>
                <label class="field span-2"><span>配图（封面标识，用逗号分隔）</span><input v-model="momentForm.images" type="text" placeholder="cloud, cafe" /><small>可选：cloud / code / api / book / night / valley / mountain / sea / forest / sunset / cafe / flower</small></label>
              </div>
              <div class="editor-actions"><button class="btn btn-ghost" type="button" @click="momentEditing = false; resetMoment()">取消</button><button class="btn btn-primary" type="button" :disabled="saving" @click="saveMoment"><Save :size="14" /> 保存</button></div>
            </div>

            <div v-if="moments.length" class="admin-list">
              <article v-for="moment in moments" :key="moment.id" class="admin-row moment-row">
                <div class="moment-mark">☁</div>
                <div class="admin-row-copy"><p class="moment-content">{{ moment.content }}</p><p>{{ relativeTime(moment.createdAt) }} · {{ moment.likes }} 赞 · {{ moment.comments }} 评论</p></div>
                <div class="row-actions"><button type="button" class="row-btn" @click="editMoment(moment)"><Pencil :size="14" /> 编辑</button><button type="button" class="row-btn danger" @click="deleteMoment(moment)"><Trash2 :size="14" /> 删除</button></div>
              </article>
            </div>
            <div v-else class="admin-empty glass-card"><PenLine :size="24" /><h3>还没有随笔</h3><p>写一条短一点的记录。</p></div>
          </section>

          <!-- 友链 -->
          <section v-else-if="activeTab === 'friends'">
            <div class="admin-toolbar">
              <div><p class="eyebrow">FRIENDS</p><h2>友链</h2><p>管理邻居们的小站。</p></div>
              <button class="btn btn-primary" type="button" @click="newFriend"><Plus :size="14" /> 新建友链</button>
            </div>

            <div v-if="friendEditing" class="editor-card glass-card">
              <div class="editor-head"><h3>{{ friendForm.id ? '编辑友链' : '新建友链' }}</h3><button type="button" class="icon-close" @click="friendEditing = false; resetFriend()"><X :size="17" /></button></div>
              <div class="form-grid">
                <label class="field"><span>名称 *</span><input v-model="friendForm.name" type="text" maxlength="60" /></label>
                <label class="field"><span>地址 *</span><input v-model="friendForm.url" type="url" maxlength="200" placeholder="https://" /></label>
                <label class="field span-2"><span>描述</span><input v-model="friendForm.description" type="text" maxlength="300" /></label>
                <label class="field"><span>头像标识</span><select v-model="friendForm.avatar"><option v-for="cover in coverChoices" :key="cover" :value="cover">{{ cover }}</option></select></label>
                <label class="field"><span>封面标识</span><select v-model="friendForm.art"><option v-for="cover in coverChoices" :key="cover" :value="cover">{{ cover }}</option></select></label>
                <label class="field"><span>加入日期</span><input v-model="friendForm.date" type="date" /></label>
              </div>
              <div class="editor-actions"><button class="btn btn-ghost" type="button" @click="friendEditing = false; resetFriend()">取消</button><button class="btn btn-primary" type="button" :disabled="saving" @click="saveFriend"><Save :size="14" /> 保存</button></div>
            </div>

            <div v-if="friends.length" class="admin-list">
              <article v-for="friend in friends" :key="friend.id" class="admin-row">
                <ArtCover :variant="friend.art || 'cloud'" ratio="16 / 10" class="admin-thumb" />
                <div class="admin-row-copy"><h3>{{ friend.name }}</h3><p>{{ friend.description }}</p><p>{{ friend.url }} · {{ formatDate(friend.date) }}</p></div>
                <div class="row-actions"><button type="button" class="row-btn" @click="editFriend(friend)"><Pencil :size="14" /> 编辑</button><button type="button" class="row-btn danger" @click="deleteFriend(friend)"><Trash2 :size="14" /> 删除</button></div>
              </article>
            </div>
            <div v-else class="admin-empty glass-card"><Link2 :size="24" /><h3>还没有友链</h3><p>添加第一位邻居。</p></div>
          </section>

          <!-- 相册 -->
          <section v-else-if="activeTab === 'photos'">
            <div class="admin-toolbar">
              <div><p class="eyebrow">GALLERY</p><h2>相册</h2><p>上传自己的照片，也可以继续使用原创 SVG 插画作为占位。</p></div>
              <button class="btn btn-primary" type="button" @click="newPhoto"><Plus :size="14" /> 新建照片</button>
            </div>

            <div v-if="photoEditing" class="editor-card glass-card">
              <div class="editor-head"><h3>{{ photoForm.id ? '编辑照片' : '新建照片' }}</h3><button type="button" class="icon-close" @click="photoEditing = false; resetPhoto()"><X :size="17" /></button></div>
              <div class="form-grid">
                <label class="field"><span>标题 *</span><input v-model="photoForm.title" type="text" maxlength="80" /></label>
                <label class="field"><span>分类 *</span><input v-model="photoForm.category" type="text" maxlength="40" placeholder="风景" /></label>
                <label class="field"><span>地点</span><input v-model="photoForm.location" type="text" maxlength="80" placeholder="云南 · 大理" /></label>
                <label class="field"><span>拍摄日期</span><input v-model="photoForm.takenAt" type="date" /></label>
                <label class="field"><span>没有上传图片时的插画</span><select v-model="photoForm.art"><option v-for="cover in coverChoices" :key="cover" :value="cover">{{ cover }}</option></select></label>
              </div>

              <div class="upload-panel">
                <div class="upload-preview">
                  <img v-if="photoForm.imageUrl" :src="photoForm.imageUrl" alt="待保存的照片预览" />
                  <div v-else class="upload-placeholder">
                    <ImagePlus :size="26" />
                    <span>还没有选择照片</span>
                    <small>不上传也可以，保存后会使用左侧选择的插画</small>
                  </div>
                  <span v-if="photoUploading" class="upload-loading">上传中…</span>
                </div>

                <div class="upload-actions">
                  <input
                    ref="photoFileInput"
                    type="file"
                    accept="image/jpeg,image/png,image/webp,image/gif"
                    class="upload-input"
                    @change="uploadPhotoFile"
                  />
                  <button class="btn btn-primary" type="button" :disabled="photoUploading" @click="choosePhotoFile">
                    <ImagePlus :size="14" /> {{ photoForm.imageUrl ? '重新选择照片' : '选择照片上传' }}
                  </button>
                  <button v-if="photoForm.imageUrl" class="btn btn-ghost" type="button" :disabled="photoUploading" @click="clearPhotoImage">
                    移除图片
                  </button>
                  <p>支持 JPG / PNG / WebP / GIF，最大 10MB。上传后会保存在服务器，并展示在相册页。</p>
                </div>
              </div>

              <div class="editor-actions"><button class="btn btn-ghost" type="button" @click="photoEditing = false; resetPhoto()">取消</button><button class="btn btn-primary" type="button" :disabled="saving || photoUploading" @click="savePhoto"><Save :size="14" /> 保存</button></div>
            </div>

            <div v-if="photos.length" class="admin-list">
              <article v-for="photo in photos" :key="photo.id" class="admin-row">
                <img v-if="photo.imageUrl" :src="photo.imageUrl" :alt="photo.title" class="admin-thumb admin-thumb-img" />
                <ArtCover v-else :variant="photo.art || 'mountain'" ratio="16 / 10" class="admin-thumb" />
                <div class="admin-row-copy"><h3>{{ photo.title }}</h3><p>{{ photo.category }} · {{ photo.location }} · {{ formatDate(photo.takenAt) }}</p></div>
                <div class="row-actions"><button type="button" class="row-btn" @click="editPhoto(photo)"><Pencil :size="14" /> 编辑</button><button type="button" class="row-btn danger" @click="deletePhoto(photo)"><Trash2 :size="14" /> 删除</button></div>
              </article>
            </div>
            <div v-else class="admin-empty glass-card"><Camera :size="24" /><h3>还没有照片</h3><p>添加第一张画面。</p></div>
          </section>

          <!-- 访问统计 -->
          <section v-else-if="activeTab === 'visits'">
            <div class="admin-toolbar">
              <div><p class="eyebrow">VISITORS</p><h2>访问统计</h2><p>读取 GitHub Pages 公网计数器，看看有没有人来过。</p></div>
              <a class="btn btn-ghost" :href="VISITS_WIDGET_URL" target="_blank" rel="noreferrer noopener">
                <Eye :size="14" /> 新窗口打开
              </a>
            </div>
            <div class="visits-embed glass-card">
              <iframe :src="VISITS_WIDGET_URL" title="访问统计" loading="lazy"></iframe>
            </div>
            <p class="visits-embed-hint">
              只统计公网 Pages 的访问，不包含 localhost；数据来自 Busuanzi，只能看总量，无法知道具体是谁。
            </p>
          </section>

          <!-- 关于我 -->
          <section v-else-if="activeTab === 'about'">
            <div class="admin-toolbar">
              <div><p class="eyebrow">ABOUT</p><h2>关于我</h2><p>编辑个人简历、教育背景和荣誉证书，保存后重新导出即可更新线上页面。</p></div>
              <div class="toolbar-actions">
                <button class="btn btn-ghost" type="button" @click="refreshAbout"><RefreshCw :size="14" /> 刷新</button>
                <button class="btn btn-primary" type="button" :disabled="saving" @click="saveAboutProfile"><Save :size="14" /> 保存资料</button>
              </div>
            </div>

            <div class="editor-card glass-card">
              <div class="editor-head"><h3>基本资料</h3></div>
              <div class="form-grid">
                <label class="field"><span>姓名 *</span><input v-model="aboutProfile.name" type="text" maxlength="80" /></label>
                <label class="field"><span>网名</span><input v-model="aboutProfile.nickname" type="text" maxlength="80" /></label>
                <label class="field"><span>出生日期</span><input v-model="aboutProfile.birthDate" type="text" maxlength="40" placeholder="2003-10-14" /></label>
                <label class="field"><span>籍贯</span><input v-model="aboutProfile.hometown" type="text" maxlength="80" /></label>
                <label class="field"><span>手机</span><input v-model="aboutProfile.phone" type="text" maxlength="40" /></label>
                <label class="field"><span>邮箱</span><input v-model="aboutProfile.email" type="text" maxlength="120" /></label>
                <label class="field span-2"><span>当前状态</span><input v-model="aboutProfile.status" type="text" maxlength="80" /></label>
                <label class="field span-2"><span>个人介绍</span><textarea v-model="aboutProfile.intro" rows="6"></textarea></label>
                <label class="field span-2"><span>方向 / 目标</span><textarea v-model="aboutProfile.goal" rows="2"></textarea></label>
                <label class="field span-2"><span>技术关键词（用顿号或逗号分隔）</span><input v-model="aboutProfile.skills" type="text" maxlength="500" /></label>
              </div>

              <h3 class="about-subhead">专科教育</h3>
              <div class="form-grid">
                <label class="field"><span>学校</span><input v-model="aboutProfile.collegeSchool" type="text" /></label>
                <label class="field"><span>专业</span><input v-model="aboutProfile.collegeMajor" type="text" /></label>
                <label class="field span-2"><span>时间</span><input v-model="aboutProfile.collegePeriod" type="text" placeholder="2023.09 - 2026.06" /></label>
                <label class="field span-2"><span>主修课程</span><textarea v-model="aboutProfile.collegeCourses" rows="2"></textarea></label>
                <label class="field span-2"><span>校内经历</span><textarea v-model="aboutProfile.collegeExperience" rows="3"></textarea></label>
              </div>

              <h3 class="about-subhead">本科教育</h3>
              <div class="form-grid">
                <label class="field"><span>学校</span><input v-model="aboutProfile.bachelorSchool" type="text" /></label>
                <label class="field"><span>专业</span><input v-model="aboutProfile.bachelorMajor" type="text" /></label>
                <label class="field span-2"><span>时间</span><input v-model="aboutProfile.bachelorPeriod" type="text" placeholder="2026.09 - 至今" /></label>
                <label class="field span-2"><span>主修课程</span><textarea v-model="aboutProfile.bachelorCourses" rows="2"></textarea></label>
                <label class="field span-2"><span>校内经历</span><textarea v-model="aboutProfile.bachelorExperience" rows="2"></textarea></label>
              </div>

              <div class="editor-actions">
                <button class="btn btn-primary" type="button" :disabled="saving" @click="saveAboutProfile"><Save :size="14" /> 保存关于我</button>
              </div>
            </div>

            <div class="admin-list-toolbar">
              <h3>荣誉与证书 <span>{{ honors.length }}</span></h3>
              <button class="btn btn-primary" type="button" @click="newHonor"><Plus :size="14" /> 新建荣誉</button>
            </div>

            <div v-if="honorEditing" class="editor-card glass-card">
              <div class="editor-head">
                <h3>{{ honorForm.id ? '编辑荣誉' : '新建荣誉' }}</h3>
                <button type="button" class="icon-close" @click="honorEditing = false; resetHonor()"><X :size="17" /></button>
              </div>
              <div class="form-grid">
                <label class="field span-2"><span>荣誉名称 *</span><input v-model="honorForm.title" type="text" maxlength="200" /></label>
                <label class="field"><span>颁发机构</span><input v-model="honorForm.issuer" type="text" maxlength="160" /></label>
                <label class="field"><span>获得时间</span><input v-model="honorForm.awardedAt" type="text" maxlength="40" placeholder="2024-08 / 2024-08-29" /></label>
                <label class="field"><span>展示优先级（越大越靠前）</span><input v-model.number="honorForm.priority" type="number" min="0" max="9999" /></label>
                <div class="field checkbox-field">
                  <span>在关于我页面展示</span>
                  <label class="inline-check"><input v-model="honorForm.visible" type="checkbox" /> 展示</label>
                </div>
                <label class="field span-2"><span>说明</span><textarea v-model="honorForm.description" rows="2"></textarea></label>
              </div>

              <div class="honor-upload">
                <div class="honor-upload-preview">
                  <img v-if="honorForm.imageUrl" :src="honorImageUrl(honorForm.imageUrl)" alt="荣誉证书预览" />
                  <span v-else><Award :size="26" /> 暂无证书图片</span>
                </div>
                <div class="honor-upload-actions">
                  <input ref="honorFileInput" type="file" accept="image/jpeg,image/png,image/webp,image/gif" class="upload-input" @change="uploadHonorImage" />
                  <button class="btn btn-soft" type="button" :disabled="honorUploading" @click="chooseHonorImage">
                    <ImagePlus :size="14" /> {{ honorForm.imageUrl ? '重新上传证书' : '上传证书图片' }}
                  </button>
                  <button v-if="honorForm.imageUrl" class="btn btn-ghost" type="button" @click="honorForm.imageUrl = ''">移除图片</button>
                  <p>JPG / PNG / WebP / GIF，最大 10MB。没有图片的荣誉会显示文字卡片。</p>
                </div>
              </div>

              <div class="editor-actions">
                <button class="btn btn-ghost" type="button" @click="honorEditing = false; resetHonor()">取消</button>
                <button class="btn btn-primary" type="button" :disabled="saving || honorUploading" @click="saveHonor"><Save :size="14" /> 保存荣誉</button>
              </div>
            </div>

            <div v-if="honors.length" class="admin-list">
              <article v-for="honor in honors" :key="honor.id" class="admin-row honor-admin-row">
                <img v-if="honor.imageUrl" :src="honorImageUrl(honor.imageUrl)" :alt="honor.title" class="honor-admin-thumb" />
                <div v-else class="honor-admin-placeholder"><Award :size="22" /></div>
                <div class="admin-row-copy">
                  <h3>{{ honor.title }}</h3>
                  <p>{{ honor.issuer }} · {{ honor.awardedAt }}</p>
                  <p class="row-tags">优先级 {{ honor.priority }} · {{ honor.visible ? '展示中' : '已隐藏' }}</p>
                </div>
                <div class="row-actions">
                  <button type="button" class="row-btn" @click="editHonor(honor)"><Pencil :size="14" /> 编辑</button>
                  <button type="button" class="row-btn danger" @click="deleteHonor(honor)"><Trash2 :size="14" /> 删除</button>
                </div>
              </article>
            </div>
            <div v-else class="admin-empty glass-card"><Award :size="24" /><h3>还没有荣誉</h3><p>添加第一张证书。</p></div>
          </section>

          <!-- 留言 -->
          <section v-else-if="activeTab === 'messages'">
            <div class="admin-toolbar">
              <div><p class="eyebrow">GUESTBOOK</p><h2>留言管理</h2><p>可以删除广告或不适内容。留言本身对所有人开放。</p></div>
              <button class="btn btn-ghost" type="button" @click="loadAll"><RefreshCw :size="14" /> 刷新</button>
            </div>

            <div v-if="messages.length" class="admin-list">
              <article v-for="message in messages" :key="message.id" class="admin-row message-row">
                <div class="message-avatar">{{ message.nickname.slice(0, 1) }}</div>
                <div class="admin-row-copy"><h3>{{ message.nickname }} <span v-if="message.admin" class="status-badge featured">站长</span></h3><p>{{ message.content }}</p><p>{{ message.location }} · {{ relativeTime(message.createdAt) }}</p></div>
                <div class="row-actions"><button type="button" class="row-btn danger" @click="deleteMessage(message)"><Trash2 :size="14" /> 删除</button></div>
              </article>
            </div>
            <div v-else class="admin-empty glass-card"><MessageCircle :size="24" /><h3>还没有留言</h3><p>等第一位访客留下足迹。</p></div>
          </section>

          <!-- 账号 -->
          <section v-else>
            <div class="admin-toolbar">
              <div><p class="eyebrow">ACCOUNT</p><h2>账号安全</h2><p>只有 yunduan 这个账号拥有管理权限。</p></div>
            </div>

            <div class="account-grid">
              <div class="account-card glass-card">
                <div class="account-head"><span class="identity-icon"><ShieldCheck :size="18" /></span><div><h3>站长账号</h3><p>@{{ auth.user?.username }}</p></div></div>
                <p class="account-desc">普通用户注册后只能浏览和留言，不会看到「内容管理」入口，后端也会拒绝他们的管理请求。</p>
                <div class="account-facts"><span>昵称：{{ auth.user?.nickname }}</span><span>角色：{{ auth.user?.role }}</span></div>
              </div>

              <div class="account-card glass-card">
                <div class="account-head"><span class="identity-icon"><KeyRound :size="18" /></span><div><h3>修改密码</h3><p>改完会退出所有登录</p></div></div>
                <div class="password-form">
                  <label class="field"><span>当前密码</span><input v-model="passwordForm.oldPassword" type="password" autocomplete="current-password" /></label>
                  <label class="field"><span>新密码（至少 6 位）</span><input v-model="passwordForm.newPassword" type="password" autocomplete="new-password" /></label>
                  <label class="field"><span>确认新密码</span><input v-model="passwordForm.confirm" type="password" autocomplete="new-password" /></label>
                  <button class="btn btn-primary" type="button" :disabled="saving" @click="changePassword"><Save :size="14" /> 修改密码</button>
                </div>
              </div>
            </div>
          </section>
        </main>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-page {
  padding-bottom: 56px;
  overflow-x: clip;
}

.admin-loading {
  margin-top: 10px;
}

.visits-embed {
  padding: 10px;
  overflow: hidden;
}

.visits-embed iframe {
  width: 100%;
  height: 430px;
  display: block;
  border: 0;
  border-radius: 12px;
  background: #effbfc;
}

.visits-embed-hint {
  margin: 10px 4px 0;
  font-size: 10.5px;
  line-height: 1.7;
  color: #8ba2aa;
}

.about-subhead {
  margin: 20px 0 10px;
  padding-top: 12px;
  border-top: 1px dashed rgba(48, 112, 131, 0.14);
  font-size: 13px;
  color: #245563;
}

.checkbox-field {
  align-content: start;
}

.inline-check {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-height: 42px;
  font-size: 12px;
  color: #3d6471;
}

.inline-check input {
  width: 17px;
  height: 17px;
}

.honor-upload {
  display: grid;
  grid-template-columns: 210px minmax(0, 1fr);
  gap: 14px;
  margin-top: 14px;
  padding: 12px;
  border-radius: 12px;
  background: rgba(244, 251, 252, 0.8);
  border: 1px solid rgba(218, 238, 243, 0.9);
}

.honor-upload-preview {
  height: 145px;
  display: grid;
  place-items: center;
  overflow: hidden;
  border-radius: 9px;
  border: 1px dashed rgba(86, 161, 178, 0.35);
  background: #f3fbfc;
  color: #92aeb6;
}

.honor-upload-preview img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

.honor-upload-preview span {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  font-size: 10px;
}

.honor-upload-actions {
  display: flex;
  flex-wrap: wrap;
  align-content: center;
  align-items: center;
  gap: 7px;
}

.honor-upload-actions p {
  flex-basis: 100%;
  margin: 0;
  max-width: 380px;
  font-size: 9.5px;
  line-height: 1.7;
  color: #8fa6ad;
}

.honor-admin-row {
  align-items: flex-start;
}

.honor-admin-thumb {
  width: 96px;
  height: 68px;
  flex: none;
  object-fit: contain;
  border-radius: 8px;
  background: #f3fbfc;
  border: 1px solid #e0eef2;
}

.honor-admin-placeholder {
  width: 96px;
  height: 68px;
  flex: none;
  display: grid;
  place-items: center;
  border-radius: 8px;
  background: #eef9f7;
  color: #76aaa2;
}

@media (max-width: 620px) {
  .visits-embed {
    padding: 6px;
  }
  .visits-embed iframe {
    height: 500px;
  }
}

.admin-shell {
  display: grid;
  grid-template-columns: 218px minmax(0, 1fr);
  gap: 20px;
  align-items: start;
  width: 100%;
  min-width: 0;
}

.admin-nav {
  position: sticky;
  top: 86px;
  padding: 18px 14px;
  min-width: 0;
}

.admin-identity {
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 0 6px 14px;
  border-bottom: 1px dashed rgba(48, 112, 131, 0.16);
}

.identity-icon {
  width: 34px;
  height: 34px;
  flex: none;
  display: grid;
  place-items: center;
  border-radius: 10px;
  background: #e2f8f1;
  color: #2f9b83;
}

.admin-identity strong {
  display: block;
  font-size: 13px;
  color: #205361;
}

.admin-identity span {
  display: block;
  font-size: 9px;
  color: #8ba2aa;
}

.admin-tabs {
  display: grid;
  gap: 3px;
  margin: 12px 0;
}

.admin-tab {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  padding: 9px 10px;
  border: 0;
  border-radius: 9px;
  background: transparent;
  color: #557884;
  font-size: 12px;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
}

.admin-tab:hover {
  background: #f1fafc;
  color: #1e7b94;
}

.admin-tab.active {
  background: #e3f7fa;
  color: #176d86;
  font-weight: 700;
}

.admin-tab em {
  margin-left: auto;
  padding: 1px 6px;
  border-radius: 999px;
  background: rgba(47, 168, 201, 0.12);
  color: #2b8ca4;
  font-size: 9px;
  font-style: normal;
}

.admin-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 5px;
  padding-top: 12px;
  border-top: 1px dashed rgba(48, 112, 131, 0.16);
}

.admin-stats div {
  text-align: center;
}

.admin-stats b {
  display: block;
  font-size: 17px;
  color: #1e6d84;
}

.admin-stats span {
  font-size: 8px;
  color: #9aadb3;
}

.admin-main {
  min-width: 0;
}

.admin-toolbar {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin: 0 0 16px;
}

.admin-toolbar h2 {
  margin: 4px 0 2px;
  font-size: 22px;
  color: #184756;
}

.admin-toolbar p:last-child {
  margin: 0;
  font-size: 10.5px;
  color: #819aa3;
}

.toolbar-actions {
  display: flex;
  gap: 8px;
  flex: none;
}

.editor-card {
  padding: 22px 24px;
  margin-bottom: 18px;
}

.editor-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.editor-head h3 {
  margin: 0;
  font-size: 15px;
  color: #245563;
}

.icon-close {
  border: 0;
  background: #f2fafb;
  color: #75939c;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  cursor: pointer;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 11px;
}

.field {
  display: grid;
  gap: 5px;
  min-width: 0;
}

.field.span-2 {
  grid-column: 1 / -1;
}

.field > span {
  font-size: 10px;
  font-weight: 650;
  color: #5f808b;
}

.field input,
.field textarea,
.field select {
  width: 100%;
  min-width: 0;
  padding: 8px 10px;
  border: 1px solid #dfeef2;
  border-radius: 8px;
  outline: 0;
  background: #fafdfe;
  color: #2d5360;
  font-size: 12px;
  line-height: 1.6;
}

.field input:focus,
.field textarea:focus,
.field select:focus {
  border-color: #7bcbdd;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(88, 187, 213, 0.1);
}

.field textarea {
  resize: vertical;
  font-family: var(--font-sans);
}

.field textarea:last-child {
  font-family: 'SFMono-Regular', Consolas, monospace;
  font-size: 11.5px;
}

.field small {
  font-size: 9px;
  color: #9aadb3;
}

.form-options {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin: 14px 0;
}

.check-line {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 10.5px;
  color: #557884;
  cursor: pointer;
}

.cover-preview {
  max-width: 360px;
  margin-bottom: 14px;
}

.cover-preview > span {
  display: block;
  margin-bottom: 5px;
  font-size: 9px;
  color: #93a8af;
}

.cover-preview :deep(.art-cover) {
  border-radius: 10px;
}

.editor-actions {
  display: flex;
  justify-content: flex-end;
  flex-wrap: wrap;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px dashed rgba(48, 112, 131, 0.16);
}

.editor-actions .btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.admin-list-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.admin-list-toolbar h3 {
  margin: 0;
  font-size: 13px;
  color: #346270;
}

.admin-list-toolbar h3 span {
  font-size: 9px;
  color: #9aadb3;
  font-weight: 400;
}

.admin-list-toolbar input {
  width: 220px;
  max-width: 45vw;
  padding: 7px 10px;
  border: 1px solid #dfeef2;
  border-radius: 8px;
  outline: 0;
  background: rgba(255, 255, 255, 0.7);
  font-size: 10.5px;
  color: #315c69;
}

.admin-list {
  display: grid;
  gap: 9px;
}

.admin-row {
  display: flex;
  align-items: center;
  gap: 13px;
  padding: 11px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.76);
  border: 1px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 6px 18px rgba(31, 99, 120, 0.06);
}

.admin-thumb {
  width: 98px;
  flex: none;
  border-radius: 9px;
  overflow: hidden;
}

.admin-thumb-img {
  height: 62px;
  object-fit: cover;
  display: block;
  border: 1px solid rgba(255, 255, 255, 0.85);
  box-shadow: 0 4px 12px rgba(31, 99, 120, 0.1);
}

.upload-panel {
  display: grid;
  grid-template-columns: minmax(0, 280px) minmax(0, 1fr);
  gap: 16px;
  margin-top: 14px;
  padding: 14px;
  border-radius: 13px;
  background: rgba(244, 251, 252, 0.85);
  border: 1px solid rgba(218, 238, 243, 0.9);
}

.upload-preview {
  position: relative;
  min-height: 150px;
  border-radius: 10px;
  overflow: hidden;
  background: #e8f5f7;
  border: 1px dashed rgba(86, 161, 178, 0.35);
}

.upload-preview img {
  width: 100%;
  height: 100%;
  min-height: 150px;
  max-height: 220px;
  object-fit: cover;
  display: block;
}

.upload-placeholder {
  min-height: 150px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  color: #78a0aa;
  text-align: center;
  padding: 16px;
}

.upload-placeholder svg {
  color: #8ac5d0;
}

.upload-placeholder span {
  font-size: 11px;
  font-weight: 650;
  color: #4c7d89;
}

.upload-placeholder small {
  font-size: 9px;
  line-height: 1.6;
  color: #9ab2b9;
}

.upload-loading {
  position: absolute;
  inset: 0;
  display: grid;
  place-items: center;
  background: rgba(8, 44, 48, 0.55);
  color: #effffb;
  font-size: 11px;
  letter-spacing: 1px;
}

.upload-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 7px;
  justify-content: center;
}

.upload-input {
  display: none;
}

.upload-actions .btn {
  min-height: 34px;
  font-size: 10.5px;
}

.upload-actions p {
  margin: 0;
  max-width: 360px;
  font-size: 9px;
  line-height: 1.7;
  color: #8fa6ad;
}

.admin-row-copy {
  flex: 1;
  min-width: 0;
}

.row-title-line {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
}

.admin-row-copy h3 {
  margin: 0;
  font-size: 12.5px;
  color: #285666;
}

.admin-row-copy p {
  margin: 3px 0 0;
  font-size: 9.5px;
  line-height: 1.5;
  color: #8ba2aa;
}

.row-tags {
  color: #6f9aa5 !important;
}

.status-badge {
  padding: 1px 6px;
  border-radius: 999px;
  font-size: 8px;
  font-weight: 650;
  background: #eef9f4;
  color: #389576;
}

.status-badge.draft {
  background: #fff5e3;
  color: #b38335;
}

.status-badge.featured {
  background: #ffeaf0;
  color: #c46b80;
}

.row-actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 4px;
  flex: none;
}

.row-btn {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  padding: 5px 7px;
  border: 1px solid transparent;
  border-radius: 6px;
  background: #f3fafc;
  color: #4d7c89;
  font-size: 9px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.row-btn:hover {
  background: #e5f5f9;
  color: #1d7b94;
}

.row-btn.danger {
  background: #fff4f2;
  color: #b16a5f;
}

.row-btn.danger:hover {
  background: #ffe8e3;
  color: #b64c3e;
}

.admin-empty {
  padding: 48px 24px;
  text-align: center;
  color: #8ba2aa;
}

.admin-empty svg {
  color: #a7cbd4;
}

.admin-empty h3 {
  margin: 7px 0 3px;
  font-size: 14px;
  color: #4b7682;
}

.admin-empty p {
  margin: 0;
  font-size: 10.5px;
}

.moment-row {
  align-items: flex-start;
}

.moment-mark {
  width: 34px;
  height: 34px;
  flex: none;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: #e6f8f3;
  color: #4a9e8a;
  font-size: 15px;
}

.moment-content {
  color: #3d616d !important;
  font-size: 11.5px !important;
  white-space: pre-line;
}

.changelog-row {
  align-items: flex-start;
}

.log-date {
  flex: none;
  width: 88px;
  font-family: 'Times New Roman', serif;
  font-size: 10.5px;
  color: #a07a45;
  padding-top: 2px;
}

.message-row {
  align-items: flex-start;
}

.message-avatar {
  width: 34px;
  height: 34px;
  flex: none;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: linear-gradient(135deg, #d9f3ef, #9fdce0);
  color: #2c7b7b;
  font-family: var(--font-serif);
  font-size: 14px;
}

.message-row .admin-row-copy p:nth-of-type(1) {
  color: #4b6c77;
  font-size: 11px;
  line-height: 1.7;
}

.account-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.account-card {
  padding: 22px 24px;
}

.account-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.account-head h3 {
  margin: 0 0 1px;
  font-size: 14px;
  color: #2a5b69;
}

.account-head p {
  margin: 0;
  font-size: 9px;
  color: #93a8af;
}

.account-desc {
  margin: 0 0 12px;
  font-size: 10.5px;
  line-height: 1.8;
  color: #6f8a93;
}

.account-facts {
  display: flex;
  flex-wrap: wrap;
  gap: 7px 14px;
  padding-top: 10px;
  border-top: 1px dashed rgba(48, 112, 131, 0.14);
  font-size: 9px;
  color: #8ba2aa;
}

.password-form {
  display: grid;
  gap: 9px;
}

.password-form .btn {
  justify-self: start;
  margin-top: 4px;
}

@media (max-width: 900px) {
  .admin-shell {
    grid-template-columns: minmax(0, 1fr);
    gap: 14px;
    width: 100%;
  }
  .admin-nav {
    position: static;
    padding: 12px;
    min-width: 0;
    overflow: hidden;
  }
  .admin-tabs {
    display: flex;
    width: 100%;
    min-width: 0;
    overflow-x: auto;
    gap: 3px;
    padding-bottom: 3px;
    scrollbar-width: none;
    -webkit-overflow-scrolling: touch;
  }
  .admin-tabs::-webkit-scrollbar {
    display: none;
  }
  .admin-tab {
    width: auto;
    min-height: 42px;
    flex: 0 0 auto;
    padding: 10px 12px;
    font-size: 13px;
    white-space: nowrap;
  }
  .admin-tab em {
    margin-left: 3px;
  }
  .admin-stats {
    display: none;
  }
  .account-grid {
    grid-template-columns: minmax(0, 1fr);
  }
  .admin-toolbar {
    align-items: stretch;
    flex-direction: column;
    gap: 10px;
  }
  .admin-toolbar .btn,
  .toolbar-actions {
    width: 100%;
  }
  .admin-toolbar .btn {
    min-height: 44px;
    justify-content: center;
  }
  .toolbar-actions {
    flex-direction: column;
  }
  .form-grid {
    grid-template-columns: minmax(0, 1fr);
  }
  .field.span-2 {
    grid-column: auto;
  }
  .field > span {
    font-size: 11px;
  }
  .field input,
  .field textarea,
  .field select {
    min-height: 44px;
    padding: 10px 12px;
    font-size: 16px;
  }
  .field textarea {
    min-height: 120px;
  }
  .field textarea:last-child {
    font-size: 15px;
    line-height: 1.75;
  }
  .editor-card {
    padding: 16px;
  }
  .admin-row {
    flex-wrap: wrap;
    align-items: flex-start;
    min-width: 0;
    width: 100%;
    padding: 10px;
    gap: 10px;
  }
  .admin-thumb {
    width: 84px;
  }
  .admin-thumb-img {
    width: 84px;
    height: 56px;
  }
  .admin-row-copy {
    flex: 1 1 calc(100% - 100px);
    overflow-wrap: anywhere;
  }
  .row-actions {
    width: 100%;
    padding-top: 8px;
    border-top: 1px dashed rgba(48, 112, 131, 0.12);
  }
  .row-btn {
    min-height: 38px;
    padding: 8px 12px;
    font-size: 11px;
  }
  .admin-list-toolbar {
    align-items: stretch;
    flex-direction: column;
    gap: 8px;
  }
  .admin-list-toolbar input {
    width: 100%;
    max-width: none;
    min-height: 44px;
    font-size: 16px;
  }
  .upload-panel,
  .honor-upload {
    grid-template-columns: minmax(0, 1fr);
  }
  .upload-preview,
  .upload-preview img,
  .honor-upload-preview {
    min-height: 180px;
    height: 180px;
  }
  .upload-actions .btn {
    width: 100%;
    justify-content: center;
    min-height: 42px;
  }
  .editor-actions .btn {
    min-height: 44px;
  }
  :global(.float-tools) {
    display: none !important;
  }
}

@media (max-width: 620px) {
  .admin-page {
    padding-bottom: 36px;
  }
  .admin-tabs {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 4px;
    overflow: visible;
  }
  .admin-tab {
    width: 100%;
    min-height: 40px;
    justify-content: flex-start;
    gap: 5px;
    padding: 8px 6px;
    font-size: 11.5px;
  }
  .admin-tab svg {
    width: 14px;
    height: 14px;
  }
  .admin-tab em {
    display: none;
  }
  .admin-toolbar h2 {
    font-size: 21px;
  }
  .admin-toolbar p:last-child {
    font-size: 11px;
    line-height: 1.7;
  }
  .admin-identity strong {
    font-size: 14px;
  }
  .admin-identity span {
    font-size: 10px;
  }
  .admin-row-copy h3 {
    font-size: 13px;
    line-height: 1.5;
  }
  .admin-row-copy p {
    font-size: 10.5px;
    line-height: 1.65;
  }
  .status-badge {
    font-size: 9.5px;
  }
  .form-options label {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    min-height: 34px;
    font-size: 12px;
  }
  .form-options input {
    width: 18px;
    height: 18px;
  }
  .field small {
    font-size: 10px;
    line-height: 1.65;
  }
  .log-date {
    width: 70px;
    font-size: 11px;
  }
  .account-card {
    padding: 18px;
  }
  .editor-actions {
    gap: 8px;
  }
  .editor-actions .btn {
    flex: 1 1 calc(50% - 4px);
    min-width: 0;
    padding: 0 8px;
    justify-content: center;
    white-space: nowrap;
  }
  .editor-actions .btn-primary {
    flex-basis: 100%;
  }
  .upload-actions p {
    font-size: 10px;
  }
}

@media (max-width: 370px) {
  .admin-tab {
    gap: 4px;
    padding: 8px 4px;
    font-size: 11px;
  }
}
</style>
