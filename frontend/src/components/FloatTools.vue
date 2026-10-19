<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowUp, LayoutGrid, X } from 'lucide-vue-next'

const progress = ref(0)
const panel = ref(false)
const route = useRoute()
const router = useRouter()

const links = [
  { label: '主页', to: '/' },
  { label: '博文', to: '/blog' },
  { label: '统计', to: '/stats' },
  { label: '随笔', to: '/moments' },
  { label: '相册', to: '/gallery' },
  { label: '友链', to: '/friends' },
  { label: '日志', to: '/changelog' },
  { label: '留言板', to: '/guestbook' },
  { label: '关于我', to: '/about' },
]

function onScroll() {
  const height = document.documentElement.scrollHeight - window.innerHeight
  progress.value = height > 0 ? Math.min(100, Math.round((window.scrollY / height) * 100)) : 0
}

function backTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function go(to: string) {
  panel.value = false
  router.push(to)
}

onMounted(() => {
  window.addEventListener('scroll', onScroll, { passive: true })
  onScroll()
})
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<template>
  <div class="float-tools" :class="{ 'panel-open': panel }">
    <Transition name="panel">
      <nav v-if="panel" class="quick-panel" aria-label="快捷导航">
        <div class="quick-head">
          <span>去往小窝的某一页</span>
          <button type="button" aria-label="关闭快捷导航" @click="panel = false"><X :size="14" /></button>
        </div>
        <button
          v-for="item in links"
          :key="item.to"
          type="button"
          class="quick-link"
          :class="{ active: route.path === item.to }"
          @click="go(item.to)"
        >
          {{ item.label }}
        </button>
      </nav>
    </Transition>

    <button class="progress-btn" type="button" aria-label="回到顶部" @click="backTop">
      <svg viewBox="0 0 44 44">
        <circle cx="22" cy="22" r="19" fill="none" stroke="rgba(40,100,120,.12)" stroke-width="3" />
        <circle
          cx="22"
          cy="22"
          r="19"
          fill="none"
          stroke="#2fa8c9"
          stroke-width="3"
          stroke-linecap="round"
          :stroke-dasharray="`${(progress / 100) * 119.4} 119.4`"
          transform="rotate(-90 22 22)"
        />
      </svg>
      <span>{{ progress }}%</span>
    </button>

    <button class="tool-btn" type="button" aria-label="回到顶部" @click="backTop">
      <ArrowUp :size="17" />
    </button>
    <button class="tool-btn" type="button" aria-label="快捷导航" @click="panel = !panel">
      <LayoutGrid :size="17" />
    </button>
  </div>
</template>

<style scoped>
.float-tools {
  position: fixed;
  right: 18px;
  bottom: 26px;
  z-index: 40;
  display: grid;
  justify-items: center;
  gap: 8px;
}

.progress-btn {
  position: relative;
  width: 50px;
  height: 50px;
  border: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
  box-shadow: 0 10px 26px rgba(31, 96, 118, 0.16);
  cursor: pointer;
}

.progress-btn svg {
  width: 44px;
  height: 44px;
}

.progress-btn span {
  position: absolute;
  inset: 0;
  display: grid;
  place-items: center;
  font-size: 9px;
  font-weight: 800;
  color: #327184;
}

.tool-btn {
  width: 38px;
  height: 38px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.72);
  color: #3b6a79;
  backdrop-filter: blur(12px);
  box-shadow: 0 8px 22px rgba(31, 96, 118, 0.12);
  cursor: pointer;
  transition: all 0.2s ease;
}

.tool-btn:hover {
  background: #fff;
  transform: translateY(-1px);
}

.quick-panel {
  position: absolute;
  right: 52px;
  bottom: -2px;
  width: 190px;
  padding: 10px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 22px 50px rgba(23, 81, 103, 0.22);
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 5px;
}

.quick-head {
  grid-column: 1 / -1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 2px 4px 8px;
  font-size: 10.5px;
  color: #75929c;
}

.quick-head button {
  border: 0;
  background: transparent;
  color: #8aa1a9;
  cursor: pointer;
  padding: 0;
}

.quick-link {
  border: 0;
  border-radius: 9px;
  padding: 8px 6px;
  background: #f4fafc;
  color: #3d6471;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
}

.quick-link:hover,
.quick-link.active {
  background: #e2f4f8;
  color: #176d86;
}

.panel-enter-active,
.panel-leave-active {
  transition: all 0.22s ease;
}
.panel-enter-from,
.panel-leave-to {
  opacity: 0;
  transform: translateX(10px) scale(0.97);
}

@media (max-width: 700px) {
  .float-tools {
    right: 10px;
    bottom: calc(12px + env(safe-area-inset-bottom));
    gap: 6px;
  }
  .progress-btn {
    display: none;
  }
  .tool-btn {
    width: 34px;
    height: 34px;
  }
  .quick-panel {
    right: 40px;
    width: 178px;
  }
}
</style>
