<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { Camera, ChevronLeft, ChevronRight, Image as ImageIcon, MapPin, X } from 'lucide-vue-next'
import ArtCover from '../components/ArtCover.vue'
import { api } from '../api/client'
import type { Photo } from '../types'
import { formatDate } from '../utils/format'

const photos = ref<Photo[]>([])
const loading = ref(true)
const error = ref('')
const activeCategory = ref('全部')
const activeIndex = ref(-1)

const categories = computed(() => Array.from(new Set(photos.value.map((p) => p.category))))
const counts = computed(() => {
  const map = new Map<string, number>()
  photos.value.forEach((p) => map.set(p.category, (map.get(p.category) || 0) + 1))
  return map
})

const filtered = computed(() =>
  activeCategory.value === '全部' ? photos.value : photos.value.filter((p) => p.category === activeCategory.value),
)

const activePhoto = computed(() => (activeIndex.value >= 0 ? filtered.value[activeIndex.value] : null))

function ratioFor(index: number): string {
  return index % 5 === 1 || index % 5 === 4 ? '3 / 4' : index % 5 === 2 ? '4 / 3' : '16 / 11'
}

function openPhoto(index: number) {
  activeIndex.value = index
}

function closePhoto() {
  activeIndex.value = -1
}

function move(delta: number) {
  if (!filtered.value.length) return
  activeIndex.value = (activeIndex.value + delta + filtered.value.length) % filtered.value.length
}

function onKeydown(event: KeyboardEvent) {
  if (activeIndex.value < 0) return
  if (event.key === 'Escape') closePhoto()
  if (event.key === 'ArrowLeft') move(-1)
  if (event.key === 'ArrowRight') move(1)
}

watch(activeIndex, (value) => {
  document.body.style.overflow = value >= 0 ? 'hidden' : ''
})

watch(activeCategory, () => {
  activeIndex.value = -1
})

onMounted(async () => {
  window.addEventListener('keydown', onKeydown)
  try {
    photos.value = await api.gallery()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '相册加载失败'
  } finally {
    loading.value = false
  }
})

onUnmounted(() => {
  window.removeEventListener('keydown', onKeydown)
  document.body.style.overflow = ''
})
</script>

<template>
  <div class="page gallery-page">
    <div class="container">
      <section class="gallery-banner">
        <ArtCover variant="valley" ratio="21 / 7" />
        <div class="banner-overlay"></div>
        <div class="banner-copy">
          <p class="banner-eyebrow"><Camera :size="12" /> 风月册 · VOL. 01</p>
          <h1>所有不经意的遇见，<br />都是光馈赠的散文诗</h1>
          <p class="banner-sub">山野入镜，清风翻页</p>
        </div>
        <span class="banner-count">{{ photos.length }} FRAMES</span>
      </section>

      <div class="gallery-toolbar">
        <div class="gallery-filters">
          <button type="button" class="chip" :class="{ active: activeCategory === '全部' }" @click="activeCategory = '全部'">
            全部 <span class="count">{{ photos.length }}</span>
          </button>
          <button
            v-for="category in categories"
            :key="category"
            type="button"
            class="chip"
            :class="{ active: activeCategory === category }"
            @click="activeCategory = category"
          >
            {{ category }} <span class="count">{{ counts.get(category) }}</span>
          </button>
        </div>
        <span class="gallery-note"><ImageIcon :size="13" /> 站长上传的照片，以及未上传时使用的原创 SVG 插画</span>
      </div>

      <div v-if="error" class="error-box">{{ error }}</div>

      <div v-if="loading" class="loading-block">
        正在冲洗胶卷<span class="loading-dots"><i></i><i></i><i></i></span>
      </div>

      <div v-else-if="filtered.length" class="gallery-masonry">
        <button
          v-for="(photo, index) in filtered"
          :key="photo.id"
          type="button"
          class="photo-card"
          :class="`shape-${index % 5}`"
          @click="openPhoto(index)"
        >
          <div class="photo-art" :style="photo.imageUrl ? { aspectRatio: ratioFor(index) } : undefined">
            <img v-if="photo.imageUrl" :src="photo.imageUrl" :alt="photo.title" loading="lazy" />
            <ArtCover v-else :variant="photo.art" :ratio="ratioFor(index)" />
            <span class="photo-shade"></span>
            <span class="photo-zoom">查看大图</span>
          </div>
          <div class="photo-info">
            <h3>{{ photo.title }}</h3>
            <div class="photo-meta">
              <span><MapPin :size="11" /> {{ photo.location }}</span>
              <span>{{ formatDate(photo.takenAt) }}</span>
            </div>
          </div>
        </button>
      </div>

      <div v-else-if="!error" class="empty-state glass-card">这个分类还没有照片。</div>

      <p class="gallery-end">— 镜头之外，还有很长的路要走 —</p>
    </div>

    <Teleport to="body">
      <Transition name="lightbox">
        <div v-if="activePhoto" class="lightbox" @click.self="closePhoto">
          <button type="button" class="lb-close" aria-label="关闭" @click="closePhoto"><X :size="22" /></button>
          <button type="button" class="lb-nav left" aria-label="上一张" @click.stop="move(-1)"><ChevronLeft :size="26" /></button>

          <figure class="lb-figure">
            <div class="lb-art">
              <img v-if="activePhoto.imageUrl" :src="activePhoto.imageUrl" :alt="activePhoto.title" />
              <ArtCover v-else :variant="activePhoto.art" ratio="16 / 10" />
            </div>
            <figcaption>
              <div>
                <h3>{{ activePhoto.title }}</h3>
                <p><MapPin :size="12" /> {{ activePhoto.location }} · {{ formatDate(activePhoto.takenAt) }}</p>
              </div>
              <span>{{ activeIndex + 1 }} / {{ filtered.length }}</span>
            </figcaption>
          </figure>

          <button type="button" class="lb-nav right" aria-label="下一张" @click.stop="move(1)"><ChevronRight :size="26" /></button>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<style scoped>
.gallery-page {
  padding-bottom: 50px;
}

.gallery-banner {
  position: relative;
  margin-top: 92px;
  border-radius: 26px;
  overflow: hidden;
  box-shadow: 0 24px 60px rgba(22, 88, 108, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.75);
}

.gallery-banner :deep(.art-cover) {
  display: block;
}

.banner-overlay {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(90deg, rgba(8, 38, 36, 0.56), rgba(8, 38, 36, 0.06) 70%, rgba(8, 38, 36, 0.14)),
    linear-gradient(180deg, rgba(6, 32, 30, 0.08), rgba(6, 32, 30, 0.22));
}

.banner-copy {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0 52px;
  color: #f6fffb;
}

.banner-eyebrow {
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 0 0 10px;
  font-size: 10px;
  letter-spacing: 3px;
  color: rgba(232, 255, 249, 0.7);
}

.banner-copy h1 {
  margin: 0 0 12px;
  font-size: clamp(24px, 3.2vw, 38px);
  line-height: 1.45;
  letter-spacing: 3px;
  text-shadow: 0 4px 24px rgba(0, 0, 0, 0.24);
}

.banner-sub {
  margin: 0;
  font-family: var(--font-serif);
  font-size: 12px;
  letter-spacing: 3px;
  color: rgba(235, 255, 249, 0.72);
}

.banner-count {
  position: absolute;
  right: 24px;
  bottom: 18px;
  font-family: 'Times New Roman', serif;
  font-size: 9px;
  letter-spacing: 2px;
  color: rgba(237, 255, 250, 0.6);
}

.gallery-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin: 22px 0 20px;
}

.gallery-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 7px;
}

.gallery-note {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 9px;
  color: #90a7ae;
  text-align: right;
}

.gallery-masonry {
  columns: 3;
  column-gap: 16px;
}

.photo-card {
  display: block;
  width: 100%;
  margin: 0 0 16px;
  padding: 0;
  border: 1px solid rgba(255, 255, 255, 0.85);
  border-radius: 16px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.74);
  box-shadow: 0 10px 26px rgba(28, 96, 118, 0.1);
  break-inside: avoid;
  text-align: left;
  cursor: zoom-in;
  transition:
    transform 0.28s ease,
    box-shadow 0.28s ease;
}

.photo-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 18px 36px rgba(26, 92, 113, 0.16);
}

.photo-art {
  position: relative;
  overflow: hidden;
  background: #d9edf2;
}

.photo-art img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.6s ease;
}

.photo-art :deep(svg) {
  transition: transform 0.6s ease;
}

.photo-card:hover .photo-art img,
.photo-card:hover .photo-art :deep(svg) {
  transform: scale(1.04);
}

.photo-shade {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, transparent 55%, rgba(7, 36, 43, 0.3));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.photo-card:hover .photo-shade {
  opacity: 1;
}

.photo-zoom {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%) scale(0.92);
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.9);
  color: #25677a;
  font-size: 9px;
  letter-spacing: 1px;
  opacity: 0;
  transition: all 0.3s ease;
}

.photo-card:hover .photo-zoom {
  opacity: 1;
  transform: translate(-50%, -50%) scale(1);
}

.photo-info {
  padding: 12px 14px 13px;
}

.photo-info h3 {
  margin: 0 0 3px;
  font-size: 12.5px;
  color: #25525f;
}

.photo-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  font-size: 9px;
  color: #91a7ae;
}

.photo-meta span {
  display: inline-flex;
  align-items: center;
  gap: 3px;
}

.gallery-end {
  margin: 30px 0 0;
  text-align: center;
  font-family: var(--font-serif);
  font-size: 10.5px;
  letter-spacing: 2px;
  color: #8fa7ae;
}

.lightbox {
  position: fixed;
  inset: 0;
  z-index: 110;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 70px;
  background: rgba(5, 28, 34, 0.82);
  backdrop-filter: blur(12px);
}

.lb-close {
  position: absolute;
  top: 22px;
  right: 24px;
  width: 40px;
  height: 40px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  color: #f0fffb;
  cursor: pointer;
}

.lb-nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 46px;
  height: 46px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  color: rgba(245, 255, 252, 0.9);
  cursor: pointer;
  transition: background 0.2s ease;
}

.lb-nav:hover {
  background: rgba(255, 255, 255, 0.2);
}

.lb-nav.left {
  left: 18px;
}
.lb-nav.right {
  right: 18px;
}

.lb-figure {
  width: min(980px, 100%);
  margin: 0;
}

.lb-art {
  border-radius: 18px;
  overflow: hidden;
  background: #071f23;
  box-shadow: 0 30px 80px rgba(0, 0, 0, 0.45);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.lb-art img {
  display: block;
  width: 100%;
  max-height: 76vh;
  object-fit: contain;
}

.lb-figure figcaption {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  margin-top: 14px;
  color: #eafff9;
}

.lb-figure h3 {
  margin: 0 0 3px;
  font-size: 16px;
  letter-spacing: 1px;
}

.lb-figure p {
  display: flex;
  align-items: center;
  gap: 5px;
  margin: 0;
  font-size: 10px;
  color: rgba(223, 250, 242, 0.65);
}

.lb-figure figcaption > span {
  font-family: 'Times New Roman', serif;
  font-size: 11px;
  letter-spacing: 2px;
  color: rgba(223, 250, 242, 0.6);
}

.lightbox-enter-active,
.lightbox-leave-active {
  transition: opacity 0.24s ease;
}
.lightbox-enter-active .lb-figure,
.lightbox-leave-active .lb-figure {
  transition: transform 0.24s ease;
}
.lightbox-enter-from,
.lightbox-leave-to {
  opacity: 0;
}
.lightbox-enter-from .lb-figure,
.lightbox-leave-to .lb-figure {
  transform: scale(0.97);
}

@media (max-width: 900px) {
  .gallery-masonry {
    columns: 2;
  }
  .banner-copy {
    padding: 0 30px;
  }
}

@media (max-width: 620px) {
  .filter-chip {
    min-height: 36px;
    padding: 0 14px;
  }
  .gallery-note {
    font-size: 10px;
    line-height: 1.6;
  }
  .gallery-banner {
    margin-top: 82px;
    border-radius: 18px;
  }
  .gallery-banner :deep(.art-cover) {
    aspect-ratio: 4 / 3 !important;
  }
  .banner-copy {
    padding: 0 20px;
  }
  .banner-copy h1 {
    font-size: 21px;
    letter-spacing: 1.5px;
  }
  .banner-count {
    right: 14px;
    bottom: 12px;
  }
  .gallery-toolbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  .gallery-masonry {
    columns: 1;
  }
  .lightbox {
    padding: 60px 14px calc(20px + env(safe-area-inset-bottom));
  }
  .lb-nav.left {
    left: 8px;
  }
  .lb-nav.right {
    right: 8px;
  }
}
</style>
