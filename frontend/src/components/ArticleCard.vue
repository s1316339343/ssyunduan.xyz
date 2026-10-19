<script setup lang="ts">
import { CalendarDays, Eye, MessageSquareText } from 'lucide-vue-next'
import type { Article } from '../types'
import ArtCover from './ArtCover.vue'
import { formatDate } from '../utils/format'

withDefaults(defineProps<{ article: Article; layout?: 'row' | 'tile'; reverse?: boolean }>(), {
  layout: 'row',
  reverse: false,
})
</script>

<template>
  <article class="post-card" :class="[`layout-${layout}`, { reverse }]">
    <RouterLink :to="`/blog/${article.slug}`" class="post-cover" :aria-label="`阅读《${article.title}》`">
      <ArtCover :variant="article.cover" />
      <span class="cover-shade"></span>
      <span class="cover-category">{{ article.category }}</span>
    </RouterLink>

    <div class="post-body">
      <div class="post-topline">
        <span class="post-cat">{{ article.category }}</span>
        <span v-if="article.featured" class="post-featured">精选</span>
      </div>

      <h2 class="post-title">
        <RouterLink :to="`/blog/${article.slug}`">{{ article.title }}</RouterLink>
      </h2>

      <p class="post-excerpt">{{ article.excerpt }}</p>

      <div class="post-meta">
        <span><CalendarDays :size="13" /> {{ formatDate(article.publishedAt) }}</span>
        <span><Eye :size="13" /> {{ article.views }} 次阅读</span>
        <span><MessageSquareText :size="13" /> {{ article.words }} 字</span>
      </div>

      <div class="post-tags">
        <RouterLink
          v-for="tag in article.tags.slice(0, 3)"
          :key="tag"
          :to="{ name: 'blog', query: { tag } }"
          class="post-tag"
        >
          # {{ tag }}
        </RouterLink>
        <span class="post-arrow">阅读全文 <span>→</span></span>
      </div>
    </div>
  </article>
</template>

<style scoped>
.post-card {
  position: relative;
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 0;
  overflow: hidden;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.74);
  border: 1px solid rgba(255, 255, 255, 0.82);
  box-shadow: 0 12px 34px rgba(34, 105, 128, 0.1);
  transition:
    transform 0.28s ease,
    box-shadow 0.28s ease;
}

.post-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 20px 44px rgba(29, 97, 120, 0.16);
}

.post-card.reverse {
  grid-template-columns: 1fr 280px;
}

.post-card.reverse .post-cover {
  order: 2;
}

.post-cover {
  position: relative;
  display: block;
  overflow: hidden;
  min-height: 210px;
  text-decoration: none;
}

.post-cover :deep(.art-cover) {
  height: 100%;
  aspect-ratio: auto !important;
}

.post-cover :deep(svg) {
  transition: transform 0.6s ease;
}

.post-card:hover .post-cover :deep(svg) {
  transform: scale(1.045);
}

.cover-shade {
  position: absolute;
  inset: 0;
  background: linear-gradient(120deg, rgba(6, 30, 38, 0.16), transparent 55%);
  pointer-events: none;
}

.cover-category {
  position: absolute;
  left: 14px;
  bottom: 14px;
  padding: 4px 9px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.85);
  color: #2a5e6f;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 1px;
  backdrop-filter: blur(6px);
}

.post-body {
  display: flex;
  flex-direction: column;
  padding: 22px 26px 18px;
  min-width: 0;
}

.post-topline {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.post-cat {
  font-size: 10px;
  letter-spacing: 2px;
  font-weight: 700;
  color: #2f93af;
  text-transform: uppercase;
}

.post-featured {
  padding: 1px 7px;
  border-radius: 999px;
  background: #fff3d6;
  color: #b98524;
  font-size: 9px;
  font-weight: 700;
  letter-spacing: 1px;
}

.post-title {
  margin: 0 0 9px;
  font-size: 20px;
  line-height: 1.45;
  letter-spacing: 0.4px;
  color: #183f4c;
}

.post-title a {
  color: inherit;
  text-decoration: none;
  background-image: linear-gradient(#2fa8c9, #2fa8c9);
  background-size: 0 2px;
  background-repeat: no-repeat;
  background-position: 0 100%;
  transition: background-size 0.3s ease;
}

.post-title a:hover {
  background-size: 100% 2px;
}

.post-excerpt {
  margin: 0 0 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-size: 12.5px;
  line-height: 1.8;
  color: #64808a;
}

.post-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin-top: auto;
  font-size: 10.5px;
  color: #8aa2aa;
}

.post-meta span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.post-tags {
  display: flex;
  align-items: center;
  gap: 7px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed rgba(54, 121, 142, 0.16);
}

.post-tag {
  padding: 2px 7px;
  border-radius: 6px;
  background: #f0f8fa;
  color: #5b8a98;
  font-size: 9.5px;
  text-decoration: none;
  transition: all 0.2s ease;
}

.post-tag:hover {
  background: #dff2f7;
  color: #1f7b94;
}

.post-arrow {
  margin-left: auto;
  font-size: 10.5px;
  color: #3e8ba1;
  font-weight: 600;
}

.post-arrow span {
  display: inline-block;
  transition: transform 0.2s ease;
}

.post-card:hover .post-arrow span {
  transform: translateX(3px);
}

.layout-tile {
  grid-template-columns: 1fr;
}

.layout-tile.reverse {
  grid-template-columns: 1fr;
}

.layout-tile .post-cover {
  order: 0;
  min-height: 0;
  aspect-ratio: 16 / 9;
}

.layout-tile .post-cover :deep(.art-cover) {
  aspect-ratio: 16 / 9 !important;
}

.layout-tile .post-body {
  padding: 18px 20px 16px;
}

.layout-tile .post-title {
  font-size: 16.5px;
}

.layout-tile .post-excerpt {
  font-size: 11.5px;
  -webkit-line-clamp: 2;
}

@media (max-width: 720px) {
  .post-card,
  .post-card.reverse {
    grid-template-columns: 1fr;
  }
  .post-card.reverse .post-cover {
    order: 0;
  }
  .post-cover {
    min-height: 0;
    aspect-ratio: 16 / 9;
  }
  .post-body {
    padding: 18px 18px 16px;
  }
  .post-title {
    font-size: 18px;
  }
}
</style>
