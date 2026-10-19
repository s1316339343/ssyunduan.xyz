<script setup lang="ts">
import { computed } from 'vue'
import { Github, Info, Mail, MapPin, Rss } from 'lucide-vue-next'
import BrandMark from './BrandMark.vue'
import { siteState } from '../stores/site'
import { isStaticMode } from '../stores/runtime'

const site = computed(() => siteState.data)
const year = new Date().getFullYear()
</script>

<template>
  <footer class="site-footer">
    <div class="footer-inner">
      <div class="footer-brand">
        <BrandMark />
        <p class="footer-tagline">{{ site.tagline }}</p>
        <p class="footer-bio">{{ site.bio }}</p>
      </div>

      <div class="footer-col">
        <h3>逛逛小窝</h3>
        <div class="footer-links">
          <RouterLink to="/blog">博文</RouterLink>
          <RouterLink to="/moments">随笔</RouterLink>
          <RouterLink to="/gallery">相册</RouterLink>
          <RouterLink to="/friends">友链</RouterLink>
          <RouterLink to="/guestbook">留言板</RouterLink>
          <RouterLink to="/about">关于我</RouterLink>
        </div>
      </div>

      <div class="footer-col">
        <h3>找到我</h3>
        <ul class="footer-contact">
          <li><MapPin :size="14" /> {{ site.location }}</li>
          <li><Mail :size="14" /> {{ site.email }}</li>
          <li><Github :size="14" /> github.com / yunduan</li>
          <li><Rss :size="14" /> {{ site.domain }}</li>
        </ul>
      </div>
    </div>

    <div class="footer-bottom">
      <p>© {{ year }} {{ site.name }} · 由 {{ site.author }}（{{ site.nickname }}）设计与维护</p>
      <p class="footer-stack">Vue 3 + Spring Boot · 把生活留在云端</p>
    </div>
    <p v-if="isStaticMode" class="footer-static-note">
      <Info :size="12" />
      静态展示版：登录、注册、留言、上传和后台管理暂不可用。
    </p>
  </footer>
</template>

<style scoped>
.site-footer {
  position: relative;
  z-index: 1;
  max-width: 1240px;
  margin: 24px auto 0;
  padding: 0 22px 34px;
}

.footer-inner {
  display: grid;
  grid-template-columns: 1.6fr 1fr 1fr;
  gap: 36px;
  padding: 30px 32px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.42);
  border: 1px solid rgba(255, 255, 255, 0.62);
  backdrop-filter: blur(18px);
  box-shadow: 0 16px 40px rgba(35, 107, 130, 0.08);
}

.footer-brand :deep(.brand-name) {
  font-size: 18px;
}

.footer-tagline {
  margin: 14px 0 5px;
  font-size: 13px;
  font-weight: 700;
  color: #275a6a;
}

.footer-bio {
  margin: 0;
  max-width: 330px;
  font-size: 12px;
  line-height: 1.8;
  color: #557580;
}

.footer-col h3 {
  margin: 0 0 13px;
  font-size: 12px;
  letter-spacing: 2px;
  color: #2d6577;
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(3, auto);
  justify-content: start;
  gap: 9px 18px;
}

.footer-links a {
  font-size: 12.5px;
  color: #4f707b;
  text-decoration: none;
  transition: color 0.2s ease;
}

.footer-links a:hover {
  color: var(--accent-deep);
}

.footer-contact {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 9px;
}

.footer-contact li {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 12.5px;
  color: #4f707b;
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  padding: 18px 8px 0;
  font-size: 11.5px;
  color: rgba(35, 83, 98, 0.72);
}

.footer-bottom p {
  margin: 0;
}

.footer-stack {
  letter-spacing: 0.5px;
}

.footer-static-note {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  margin: 10px auto 0;
  padding: 6px 10px;
  width: fit-content;
  max-width: 100%;
  border-radius: 999px;
  background: rgba(255, 248, 229, 0.75);
  border: 1px solid rgba(224, 194, 128, 0.5);
  color: #8f6b2c;
  font-size: 10px;
  line-height: 1.5;
  text-align: center;
}

@media (max-width: 860px) {
  .footer-inner {
    grid-template-columns: 1fr 1fr;
    gap: 26px;
    padding: 26px;
  }
  .footer-brand {
    grid-column: 1 / -1;
  }
}

@media (max-width: 560px) {
  .site-footer {
    padding: 0 14px 24px;
  }
  .footer-inner {
    grid-template-columns: 1fr;
    padding: 22px;
  }
  .footer-bottom {
    flex-direction: column;
    gap: 6px;
  }
}
</style>
