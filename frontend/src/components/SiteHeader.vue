<script setup lang="ts">
import { onMounted, onUnmounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  BarChart3,
  BookOpen,
  Clock3,
  Github,
  Heart,
  Home,
  Images,
  Link2,
  LogIn,
  LogOut,
  Menu,
  MessageCircle,
  PenLine,
  Search,
  Settings,
  UserPlus,
  X,
} from 'lucide-vue-next'
import BrandMark from './BrandMark.vue'
import AvatarArt from './AvatarArt.vue'
import { auth, isAdmin, logout, openAuth } from '../stores/auth'
import { isStaticMode, STATIC_NOTICE } from '../stores/runtime'
import { notify } from '../stores/toast'

const route = useRoute()
const router = useRouter()
const menuOpen = ref(false)
const searchOpen = ref(false)
const query = ref('')
const userMenu = ref(false)
const searchInput = ref<HTMLInputElement | null>(null)

const nav = [
  { label: '主页', to: '/', icon: Home, color: '#2fa8c9' },
  { label: '博文', to: '/blog', icon: BookOpen, color: '#ef8f6a' },
  { label: '统计', to: '/stats', icon: BarChart3, color: '#57b98b' },
  { label: '随笔', to: '/moments', icon: PenLine, color: '#4f9fdb' },
  { label: '相册', to: '/gallery', icon: Images, color: '#c48b6a' },
  { label: '友链', to: '/friends', icon: Link2, color: '#de7d8e' },
  { label: '日志', to: '/changelog', icon: Clock3, color: '#7b9ed6' },
  { label: '留言板', to: '/guestbook', icon: MessageCircle, color: '#3fbf9c' },
  { label: '关于我', to: '/about', icon: Heart, color: '#e58aa6' },
]

function isActive(to: string) {
  if (to === '/') return route.path === '/'
  return route.path === to || route.path.startsWith(`${to}/`)
}

function submitSearch() {
  const q = query.value.trim()
  searchOpen.value = false
  menuOpen.value = false
  router.push({ name: 'blog', query: q ? { q } : {} })
}

function toggleSearch() {
  searchOpen.value = !searchOpen.value
  if (searchOpen.value) {
    window.setTimeout(() => searchInput.value?.focus(), 60)
  }
}

function startAuth(mode: 'login' | 'register') {
  if (isStaticMode.value) {
    notify(STATIC_NOTICE, 'warn', 7000)
    return
  }
  openAuth(mode)
}

function showStaticInfo() {
  notify(STATIC_NOTICE, 'warn', 7000)
}

function signOut() {
  logout()
  userMenu.value = false
  notify('已退出登录，期待下次再见。', 'info')
}

function onDocumentClick(event: MouseEvent) {
  const target = event.target as HTMLElement
  if (!target.closest('.user-area')) userMenu.value = false
  if (!target.closest('.search-area')) searchOpen.value = false
}

watch(
  () => route.fullPath,
  () => {
    menuOpen.value = false
    searchOpen.value = false
    userMenu.value = false
  },
)

onMounted(() => document.addEventListener('click', onDocumentClick))
onUnmounted(() => document.removeEventListener('click', onDocumentClick))
</script>

<template>
  <header class="site-header">
    <div class="nav-shell">
      <RouterLink class="logo-link" to="/" aria-label="回到首页">
        <BrandMark />
      </RouterLink>

      <button v-if="isStaticMode" class="static-pill hide-sm" type="button" @click="showStaticInfo">
        静态展示
      </button>

      <nav class="nav-desktop" aria-label="主导航">
        <RouterLink
          v-for="item in nav"
          :key="item.to"
          :to="item.to"
          class="nav-link"
          :class="{ active: isActive(item.to) }"
        >
          <component :is="item.icon" class="nav-icon" :size="15" :stroke-width="2.2" :style="{ color: item.color }" />
          <span>{{ item.label }}</span>
        </RouterLink>
      </nav>

      <div class="nav-actions">
        <div class="search-area">
          <button class="icon-btn" type="button" aria-label="搜索文章" @click.stop="toggleSearch">
            <Search :size="18" />
          </button>
          <form v-if="searchOpen" class="search-pop" @submit.prevent="submitSearch">
            <Search :size="16" class="search-pop-icon" />
            <input
              ref="searchInput"
              v-model="query"
              type="search"
              placeholder="搜索文章、标签…"
              aria-label="搜索关键词"
            />
            <button type="submit" class="search-go">搜索</button>
          </form>
        </div>

        <a
          class="icon-btn hide-sm"
          href="https://github.com/"
          target="_blank"
          rel="noreferrer noopener"
          aria-label="GitHub"
        >
          <Github :size="18" />
        </a>

        <template v-if="auth.user">
          <div class="user-area">
            <button class="user-btn" type="button" @click.stop="userMenu = !userMenu">
              <AvatarArt :size="30" :ring="false" />
              <span>{{ auth.user.nickname }}</span>
            </button>
            <Transition name="pop">
              <div v-if="userMenu" class="user-pop">
                <p class="user-pop-name">{{ auth.user.nickname }}</p>
                <p class="user-pop-role">{{ isAdmin ? '站长' : '小窝访客' }}</p>
                <RouterLink v-if="isAdmin" to="/admin" class="user-pop-admin" @click="userMenu = false">
                  <Settings :size="15" /> 内容管理
                </RouterLink>
                <button type="button" class="user-pop-out" @click="signOut">
                  <LogOut :size="15" /> 退出登录
                </button>
              </div>
            </Transition>
          </div>
        </template>
        <template v-else>
          <button class="text-btn" type="button" @click="startAuth('login')">
            <LogIn :size="15" /> 登录
          </button>
          <button class="register-btn" type="button" @click="startAuth('register')">
            <UserPlus :size="15" /> 注册
          </button>
        </template>

        <button class="icon-btn menu-btn" type="button" aria-label="打开菜单" @click="menuOpen = !menuOpen">
          <X v-if="menuOpen" :size="20" />
          <Menu v-else :size="20" />
        </button>
      </div>
    </div>

    <Transition name="drawer">
      <div v-if="menuOpen" class="mobile-drawer">
        <RouterLink v-for="item in nav" :key="item.to" :to="item.to" class="drawer-link" :class="{ active: isActive(item.to) }">
          <component :is="item.icon" :size="18" :style="{ color: item.color }" />
          <span>{{ item.label }}</span>
        </RouterLink>
        <RouterLink v-if="isAdmin" to="/admin" class="drawer-link admin-drawer-link">
          <Settings :size="18" /> <span>内容管理</span>
        </RouterLink>
        <p v-if="isStaticMode" class="drawer-static-note">静态展示模式：登录、留言、上传和后台管理暂不可用。</p>
        <div class="drawer-auth">
          <template v-if="auth.user">
            <span class="drawer-user"><AvatarArt :size="28" :ring="false" /> {{ auth.user.nickname }}</span>
            <button class="text-btn" type="button" @click="signOut">退出</button>
          </template>
          <template v-else>
            <button class="text-btn" type="button" @click="startAuth('login')">登录</button>
            <button class="register-btn" type="button" @click="startAuth('register')">注册</button>
          </template>
        </div>
      </div>
    </Transition>
  </header>
</template>

<style scoped>
.site-header {
  position: fixed;
  inset: 0 0 auto;
  z-index: 50;
  background: linear-gradient(180deg, rgba(250, 254, 255, 0.88), rgba(247, 253, 255, 0.62));
  border-bottom: 1px solid rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(20px) saturate(1.2);
  -webkit-backdrop-filter: blur(20px) saturate(1.2);
  box-shadow: 0 10px 40px rgba(39, 107, 133, 0.08);
}

.nav-shell {
  max-width: 1380px;
  margin: 0 auto;
  height: 66px;
  padding: 0 22px;
  display: flex;
  align-items: center;
  gap: 14px;
}

.logo-link {
  display: inline-flex;
  text-decoration: none;
  flex: none;
}

.static-pill {
  flex: none;
  padding: 4px 8px;
  border: 1px solid rgba(201, 147, 56, 0.35);
  border-radius: 999px;
  background: rgba(255, 246, 222, 0.8);
  color: #9a6a22;
  font-size: 9.5px;
  font-weight: 700;
  letter-spacing: 0.3px;
  cursor: pointer;
}

.static-pill:hover {
  background: #fff5da;
}

.nav-desktop {
  display: flex;
  align-items: center;
  gap: 1px;
  margin: 0 auto;
}

.nav-link {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 8px 10px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
  color: #335765;
  text-decoration: none;
  transition:
    background 0.22s ease,
    color 0.22s ease,
    transform 0.22s ease;
  white-space: nowrap;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.7);
  color: #0e3d4d;
  transform: translateY(-1px);
}

.nav-link.active {
  background: rgba(255, 255, 255, 0.92);
  color: #0d3a49;
  box-shadow: 0 6px 16px rgba(43, 118, 143, 0.14);
}

.nav-icon {
  flex: none;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: none;
}

.icon-btn {
  width: 34px;
  height: 34px;
  display: inline-grid;
  place-items: center;
  border: 0;
  border-radius: 10px;
  background: transparent;
  color: #3b606d;
  cursor: pointer;
  transition: background 0.2s ease;
}

.icon-btn:hover {
  background: rgba(255, 255, 255, 0.85);
}

.search-area {
  position: relative;
}

.search-pop {
  position: absolute;
  top: calc(100% + 14px);
  right: 0;
  width: 310px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 8px 8px 12px;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid rgba(255, 255, 255, 0.9);
  border-radius: 14px;
  box-shadow: 0 20px 50px rgba(25, 82, 104, 0.2);
  z-index: 5;
}

.search-pop-icon {
  color: #7d9aa4;
  flex: none;
}

.search-pop input {
  min-width: 0;
  flex: 1;
  border: 0;
  outline: 0;
  background: transparent;
  font-size: 13px;
  color: var(--ink);
}

.search-go {
  border: 0;
  border-radius: 9px;
  padding: 7px 12px;
  background: var(--accent-deep);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
}

.text-btn,
.register-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  height: 34px;
  padding: 0 12px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 650;
  cursor: pointer;
  transition: all 0.2s ease;
}

.text-btn {
  border: 1px solid transparent;
  background: transparent;
  color: #335765;
}

.text-btn:hover {
  background: rgba(255, 255, 255, 0.8);
}

.register-btn {
  border: 1px solid rgba(255, 255, 255, 0.85);
  background: rgba(255, 255, 255, 0.55);
  color: #21576a;
  box-shadow: 0 6px 16px rgba(34, 105, 128, 0.1);
}

.register-btn:hover {
  background: #fff;
  transform: translateY(-1px);
}

.user-area {
  position: relative;
}

.user-btn {
  display: inline-flex;
  align-items: center;
  flex: none;
  gap: 7px;
  max-width: 132px;
  padding: 3px 9px 3px 3px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.55);
  color: #28596a;
  font-size: 12px;
  font-weight: 650;
  cursor: pointer;
}

.user-btn span {
  min-width: 0;
  max-width: 52px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-pop {
  position: absolute;
  top: calc(100% + 12px);
  right: 0;
  width: 180px;
  padding: 14px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.97);
  border: 1px solid rgba(255, 255, 255, 0.85);
  box-shadow: 0 22px 50px rgba(24, 81, 103, 0.22);
}

.user-pop-name {
  margin: 0;
  font-size: 14px;
  font-weight: 750;
  color: var(--ink);
}

.user-pop-role {
  margin: 3px 0 10px;
  font-size: 11px;
  color: var(--muted);
}

.user-pop-admin,
.user-pop-out {
  width: 100%;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 10px;
  border: 0;
  border-radius: 9px;
  font-size: 12px;
  text-decoration: none;
  cursor: pointer;
}

.user-pop-admin {
  margin-bottom: 5px;
  background: #e7f7fb;
  color: #1f7892;
  font-weight: 650;
}

.user-pop-out {
  background: #f3f9fb;
  color: #44636e;
}

.admin-drawer-link {
  margin-top: 4px;
  color: #1f7892;
  background: #eaf8fb;
}

.menu-btn {
  display: none;
}

.mobile-drawer {
  display: none;
  padding: 10px 18px 20px;
  background: rgba(250, 254, 255, 0.98);
  border-top: 1px solid rgba(26, 91, 113, 0.08);
  max-height: calc(100vh - 66px);
  overflow: auto;
}

.drawer-link {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 11px 12px;
  border-radius: 10px;
  color: #335765;
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
}

.drawer-link.active {
  background: #edf8fb;
  color: #0d3a49;
}

.drawer-auth {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-top: 10px;
  padding-top: 14px;
  border-top: 1px dashed rgba(44, 109, 130, 0.18);
}

.drawer-user {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 650;
  color: #2e5d6d;
}

.drawer-static-note {
  margin: 10px 0 0;
  padding: 9px 11px;
  border-radius: 9px;
  background: #fff8e5;
  border: 1px solid #f0dfb8;
  color: #97702d;
  font-size: 10.5px;
  line-height: 1.6;
}

@media (max-width: 1180px) {
  .nav-desktop {
    display: none;
  }
  .menu-btn {
    display: inline-grid;
  }
  .mobile-drawer {
    display: block;
  }
  .nav-actions {
    margin-left: auto;
  }
}

@media (max-width: 560px) {
  .nav-shell {
    padding: 0 12px;
    gap: 6px;
    height: 60px;
  }
  .hide-sm {
    display: none;
  }
  .nav-actions {
    gap: 3px;
  }
  .text-btn,
  .register-btn {
    padding: 0 8px;
  }
  .search-pop {
    width: min(300px, calc(100vw - 28px));
    right: -48px;
  }
}

@media (max-width: 430px) {
  .brand-sub {
    display: none;
  }
  .nav-shell {
    padding: 0 10px;
  }
  .text-btn {
    padding: 0 6px;
  }
  .register-btn {
    padding: 0 9px;
  }
  .user-btn {
    gap: 4px;
    max-width: 92px;
    padding-right: 5px;
  }
  .user-btn span {
    max-width: 42px;
  }
}
</style>
