<script setup lang="ts">
withDefaults(defineProps<{ wave?: boolean }>(), { wave: true })

const drops = Array.from({ length: 64 }, (_, i) => ({
  x: (i * 127 + 40) % 1600,
  y: (i * 211 + 30) % 980,
  len: 12 + ((i * 13) % 30),
  delay: ((i * 7) % 43) / 10,
  opacity: 0.12 + ((i * 11) % 24) / 100,
}))

const goldPatches = [
  { cx: 330, cy: 330, rx: 150, ry: 80, r: -18, c: '#c9b184', o: 0.24 },
  { cx: 520, cy: 520, rx: 120, ry: 60, r: 26, c: '#b7a06f', o: 0.18 },
  { cx: 250, cy: 640, rx: 170, ry: 70, r: -8, c: '#d8c49a', o: 0.16 },
  { cx: 1180, cy: 300, rx: 130, ry: 60, r: 22, c: '#b8a273', o: 0.14 },
  { cx: 1330, cy: 560, rx: 170, ry: 74, r: -14, c: '#d5be8c', o: 0.17 },
  { cx: 1040, cy: 700, rx: 120, ry: 48, r: 10, c: '#c3ac7c', o: 0.14 },
  { cx: 610, cy: 800, rx: 140, ry: 52, r: -22, c: '#d9c79d', o: 0.12 },
  { cx: 880, cy: 200, rx: 90, ry: 40, r: 8, c: '#a9c7b0', o: 0.1 },
]

const tealPatches = [
  { cx: 760, cy: 520, rx: 330, ry: 190, c: '#1c7567', o: 0.38 },
  { cx: 480, cy: 400, rx: 250, ry: 150, c: '#12655a', o: 0.32 },
  { cx: 1120, cy: 620, rx: 260, ry: 160, c: '#0e5a51', o: 0.36 },
  { cx: 900, cy: 300, rx: 220, ry: 120, c: '#258878', o: 0.3 },
]
</script>

<template>
  <div class="pond-scene" aria-hidden="true">
    <svg class="pond-svg" viewBox="0 0 1600 1000" preserveAspectRatio="xMidYMid slice">
      <defs>
        <radialGradient id="pondBase" cx="50%" cy="42%" r="72%">
          <stop offset="0" stop-color="#0d5149" />
          <stop offset="0.45" stop-color="#083b37" />
          <stop offset="1" stop-color="#031d1c" />
        </radialGradient>
        <radialGradient id="catGlow" cx="50%" cy="50%" r="50%">
          <stop offset="0" stop-color="#b9f8e2" stop-opacity="0.24" />
          <stop offset="0.55" stop-color="#74d8c2" stop-opacity="0.08" />
          <stop offset="1" stop-color="#74d8c2" stop-opacity="0" />
        </radialGradient>
        <filter id="organic" x="-30%" y="-30%" width="160%" height="160%">
          <feTurbulence type="fractalNoise" baseFrequency="0.012 0.02" numOctaves="3" seed="9" result="noise" />
          <feDisplacementMap in="SourceGraphic" in2="noise" scale="70" xChannelSelector="R" yChannelSelector="G" />
          <feGaussianBlur stdDeviation="3" />
        </filter>
        <filter id="soft" x="-40%" y="-40%" width="180%" height="180%">
          <feGaussianBlur stdDeviation="18" />
        </filter>
        <linearGradient id="waveGrad" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0" stop-color="#ffffff" />
          <stop offset="1" stop-color="#ecf8f6" />
        </linearGradient>
        <linearGradient id="rainFade" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0" stop-color="#dffff4" stop-opacity="0" />
          <stop offset="0.4" stop-color="#dffff4" stop-opacity="0.55" />
          <stop offset="1" stop-color="#dffff4" stop-opacity="0" />
        </linearGradient>
      </defs>

      <rect width="1600" height="1000" fill="url(#pondBase)" />

      <g filter="url(#organic)">
        <ellipse
          v-for="(p, i) in tealPatches"
          :key="`t${i}`"
          :cx="p.cx"
          :cy="p.cy"
          :rx="p.rx"
          :ry="p.ry"
          :fill="p.c"
          :opacity="p.o"
        />
        <ellipse
          v-for="(p, i) in goldPatches"
          :key="`g${i}`"
          :cx="p.cx"
          :cy="p.cy"
          :rx="p.rx"
          :ry="p.ry"
          :fill="p.c"
          :opacity="p.o"
          :transform="`rotate(${p.r} ${p.cx} ${p.cy})`"
        />
      </g>

      <!-- 水面的光 -->
      <ellipse cx="800" cy="470" rx="520" ry="360" fill="url(#catGlow)" />

      <!-- 波纹 -->
      <g fill="none" stroke="#d7fff1" stroke-width="1.6" opacity="0.22">
        <ellipse cx="800" cy="560" rx="130" ry="26" />
        <ellipse cx="800" cy="560" rx="210" ry="42" opacity="0.6" />
        <ellipse cx="800" cy="560" rx="310" ry="62" opacity="0.35" />
        <ellipse cx="420" cy="300" rx="90" ry="18" opacity="0.4" />
        <ellipse cx="1200" cy="740" rx="110" ry="22" opacity="0.35" />
        <ellipse cx="300" cy="760" rx="140" ry="24" opacity="0.3" />
        <ellipse cx="1300" cy="330" rx="80" ry="16" opacity="0.3" />
      </g>

      <!-- 飘落的叶影 -->
      <g fill="#d8bd8b" opacity="0.35" filter="url(#soft)">
        <ellipse cx="220" cy="380" rx="16" ry="7" transform="rotate(-20 220 380)" />
        <ellipse cx="1450" cy="620" rx="20" ry="8" transform="rotate(24 1450 620)" />
        <ellipse cx="1100" cy="180" rx="12" ry="6" transform="rotate(12 1100 180)" />
      </g>

      <!-- 黑猫 -->
      <g transform="translate(800 342) scale(0.52)">
        <g class="cat">
          <ellipse cx="0" cy="128" rx="92" ry="20" fill="#d9fff2" opacity="0.12" />
          <path d="M-42 120 C-58 72 -54 24 -22 -6 C-8 -20 8 -20 22 -6 C54 24 58 72 42 120 C26 142 -26 142 -42 120Z" fill="#061b19" />
          <path d="M30 96 C88 108 104 58 76 34 C68 27 60 28 58 38 C90 60 66 88 32 78Z" fill="#061b19" />
          <path d="M-30 -22 L-40 -72 L-4 -44Z" fill="#061b19" />
          <path d="M30 -22 L40 -72 L4 -44Z" fill="#061b19" />
          <path d="M-28 -30 L-34 -58 L-12 -42Z" fill="#12544a" opacity="0.75" />
          <path d="M28 -30 L34 -58 L12 -42Z" fill="#12544a" opacity="0.75" />
          <ellipse cx="0" cy="-18" rx="42" ry="36" fill="#061b19" />
          <ellipse cx="0" cy="-6" rx="17" ry="12" fill="#0d302b" />
          <ellipse cx="-15" cy="-20" rx="4.4" ry="5.6" fill="#f4d27e" />
          <ellipse cx="15" cy="-20" rx="4.4" ry="5.6" fill="#f4d27e" />
          <ellipse cx="-15" cy="-20" rx="1.5" ry="4.6" fill="#1a1a12" />
          <ellipse cx="15" cy="-20" rx="1.5" ry="4.6" fill="#1a1a12" />
          <path d="M-4 0 L4 0 L0 5Z" fill="#d9a9a2" />
          <path d="M0 5 C-2 10 -6 10 -8 7 M0 5 C2 10 6 10 8 7" stroke="#8f7f74" stroke-width="1.2" fill="none" stroke-linecap="round" />
          <g stroke="#c9fff0" stroke-width="1" opacity="0.4" stroke-linecap="round">
            <path d="M-18 4 L-44 1" />
            <path d="M-18 8 L-42 11" />
            <path d="M18 4 L44 1" />
            <path d="M18 8 L42 11" />
          </g>
          <path d="M-38 34 C-54 50 -48 88 -20 100" stroke="#061b19" stroke-width="12" fill="none" stroke-linecap="round" />
        </g>
      </g>

      <!-- 雨 -->
      <g class="rain">
        <line
          v-for="(d, i) in drops"
          :key="i"
          :x1="d.x"
          :y1="d.y"
          :x2="d.x - 5"
          :y2="d.y + d.len"
          stroke="url(#rainFade)"
          stroke-width="1.2"
          :opacity="d.opacity"
          :style="{ animationDelay: `${d.delay}s` }"
        />
      </g>

      <!-- 萤火 -->
      <g class="fireflies" fill="#ffe9a8">
        <circle cx="520" cy="640" r="2.2" />
        <circle cx="1080" cy="430" r="1.8" />
        <circle cx="650" cy="240" r="1.6" />
        <circle cx="1250" cy="220" r="2" />
        <circle cx="380" cy="500" r="1.7" />
      </g>

      <!-- 底部白浪 -->
      <g v-if="wave">
        <path
          d="M0 880 C 180 838 340 884 520 896 C 700 908 820 862 1000 872 C 1180 882 1320 928 1440 918 C 1510 912 1560 900 1600 892 L1600 1000 L0 1000Z"
          fill="url(#waveGrad)"
        />
        <path
          d="M0 902 C 190 866 330 906 510 916 C 700 926 820 886 1000 894 C 1190 902 1330 946 1460 934 C 1520 928 1570 918 1600 912"
          fill="none"
          stroke="#ffffff"
          stroke-width="3"
          opacity="0.65"
        />
      </g>
    </svg>
    <div class="pond-vignette"></div>
  </div>
</template>

<style scoped>
.pond-scene {
  position: absolute;
  inset: 0;
  overflow: hidden;
  background: #042421;
}

.pond-svg {
  width: 100%;
  height: 100%;
  display: block;
}

.pond-vignette {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at 50% 38%, rgba(2, 24, 23, 0) 0%, rgba(2, 24, 23, 0.18) 48%, rgba(2, 20, 19, 0.66) 100%),
    linear-gradient(180deg, rgba(1, 18, 17, 0.45), rgba(1, 18, 17, 0) 30%, rgba(1, 18, 17, 0.1) 70%, rgba(1, 18, 17, 0.5));
  pointer-events: none;
}

.rain line {
  animation: rainfall 1.6s linear infinite;
}

.cat {
  animation: breathe 7s ease-in-out infinite;
  transform-box: fill-box;
  transform-origin: center bottom;
}

.fireflies circle {
  animation: blink 4.5s ease-in-out infinite;
}
.fireflies circle:nth-child(2) {
  animation-delay: 1.2s;
}
.fireflies circle:nth-child(3) {
  animation-delay: 2.1s;
}
.fireflies circle:nth-child(4) {
  animation-delay: 0.7s;
}
.fireflies circle:nth-child(5) {
  animation-delay: 2.8s;
}

@keyframes rainfall {
  from {
    transform: translateY(-180px);
    opacity: 0;
  }
  20% {
    opacity: 1;
  }
  to {
    transform: translateY(140px);
    opacity: 0;
  }
}

@keyframes breathe {
  0%,
  100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-4px) scale(1.008);
  }
}

@keyframes blink {
  0%,
  100% {
    opacity: 0.15;
  }
  45%,
  55% {
    opacity: 0.9;
  }
}

@media (prefers-reduced-motion: reduce) {
  .rain line,
  .cat,
  .fireflies circle {
    animation: none;
  }
}
</style>
