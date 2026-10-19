import { createRouter, createWebHistory } from 'vue-router'
import { nextTick } from 'vue'
import { auth, isAdmin, openAuth } from '../stores/auth'
import { detectBackend, isStaticMode, STATIC_NOTICE } from '../stores/runtime'
import { notify } from '../stores/toast'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: () => import('../views/HomeView.vue'), meta: { title: '首页' } },
    { path: '/blog', name: 'blog', component: () => import('../views/BlogView.vue'), meta: { title: '博客' } },
    {
      path: '/blog/:slug',
      name: 'article',
      component: () => import('../views/ArticleView.vue'),
      meta: { title: '文章' },
    },
    { path: '/stats', name: 'stats', component: () => import('../views/StatsView.vue'), meta: { title: '统计' } },
    { path: '/moments', name: 'moments', component: () => import('../views/MomentsView.vue'), meta: { title: '随笔' } },
    { path: '/gallery', name: 'gallery', component: () => import('../views/GalleryView.vue'), meta: { title: '相册' } },
    { path: '/friends', name: 'friends', component: () => import('../views/FriendsView.vue'), meta: { title: '友链' } },
    { path: '/changelog', name: 'changelog', component: () => import('../views/ChangelogView.vue'), meta: { title: '日志' } },
    { path: '/guestbook', name: 'guestbook', component: () => import('../views/GuestbookView.vue'), meta: { title: '留言板' } },
    { path: '/about', name: 'about', component: () => import('../views/AboutView.vue'), meta: { title: '关于我' } },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/AdminView.vue'),
      meta: { title: '内容管理', requiresAdmin: true },
    },
    { path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('../views/NotFoundView.vue'), meta: { title: '迷路了' } },
  ],
  scrollBehavior(to, _from, savedPosition) {
    if (savedPosition) return savedPosition
    if (to.hash) return { el: to.hash, top: 90, behavior: 'smooth' }
    return { top: 0, behavior: 'smooth' }
  },
})

router.beforeEach(async (to) => {
  const requiresAdmin = Boolean((to.meta as { requiresAdmin?: boolean }).requiresAdmin)
  if (requiresAdmin) await detectBackend()
  if (requiresAdmin && isStaticMode.value) {
    notify(STATIC_NOTICE, 'warn', 7000)
    return { name: 'home' }
  }
  if (requiresAdmin && !isAdmin.value) {
    if (!auth.user) {
      openAuth('login')
      notify('请先用站长账号 yunduan 登录。', 'warn')
    } else {
      notify('只有站长账号 yunduan 可以管理内容。', 'warn')
    }
    return { name: 'home' }
  }
  return true
})

router.afterEach(async (to) => {
  await nextTick()
  const suffix = to.meta.title && to.name !== 'home' ? `${String(to.meta.title)} · ` : ''
  document.title = `${suffix}云端の小窝`
})

export default router
