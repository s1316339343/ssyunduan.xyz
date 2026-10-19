<script setup lang="ts">
import { onMounted, watch } from 'vue'
import { api } from './api/client'
import { loadSite } from './stores/site'
import { detectBackend, isStaticMode, STATIC_NOTICE } from './stores/runtime'
import { notify } from './stores/toast'
import SkyScene from './components/SkyScene.vue'
import SiteHeader from './components/SiteHeader.vue'
import SiteFooter from './components/SiteFooter.vue'
import AuthModal from './components/AuthModal.vue'
import ToastStack from './components/ToastStack.vue'
import FloatTools from './components/FloatTools.vue'

let staticNoticeShown = false

watch(isStaticMode, (staticMode) => {
  if (staticMode && !staticNoticeShown) {
    staticNoticeShown = true
    notify(STATIC_NOTICE, 'warn', 7000)
  }
})

onMounted(async () => {
  await detectBackend()
  loadSite()
  if (!sessionStorage.getItem('yunduan.visit')) {
    sessionStorage.setItem('yunduan.visit', '1')
    api.trackVisit()
  }
})
</script>

<template>
  <SkyScene />
  <SiteHeader />
  <main>
    <RouterView v-slot="{ Component, route }">
      <component :is="Component" :key="route.path" class="route-view" />
    </RouterView>
  </main>
  <SiteFooter />
  <FloatTools />
  <AuthModal />
  <ToastStack />
</template>
