<script setup lang="ts">
const blades = Array.from({ length: 26 }, (_, i) => {
  const x = 12 + i * 63
  const h = 26 + ((i * 37) % 46)
  return { x, h, lean: ((i * 17) % 18) - 9, delay: (i % 7) * 0.35 }
})

const petals = [
  { x: 240, y: 240, r: 3, d: 0 },
  { x: 440, y: 180, r: 2.2, d: 3 },
  { x: 720, y: 300, r: 2.6, d: 6 },
  { x: 980, y: 210, r: 3.2, d: 2 },
  { x: 1320, y: 520, r: 2.4, d: 4 },
  { x: 1480, y: 410, r: 3, d: 7 },
  { x: 620, y: 540, r: 2, d: 5 },
  { x: 880, y: 600, r: 2.8, d: 1 },
]
</script>

<template>
  <div class="sky-scene" aria-hidden="true">
    <svg class="sky-svg" viewBox="0 0 1600 900" preserveAspectRatio="xMidYMid slice">
      <defs>
        <linearGradient id="skyGrad" x1="0" y1="0" x2="0.2" y2="1">
          <stop offset="0" stop-color="#6fc5e9" />
          <stop offset="0.55" stop-color="#a9e0f4" />
          <stop offset="0.82" stop-color="#e2f7fb" />
          <stop offset="1" stop-color="#f5fbf4" />
        </linearGradient>
        <radialGradient id="sunGlow" cx="50%" cy="50%" r="50%">
          <stop offset="0" stop-color="#fffbe8" stop-opacity="0.95" />
          <stop offset="0.45" stop-color="#fff3c4" stop-opacity="0.45" />
          <stop offset="1" stop-color="#fff3c4" stop-opacity="0" />
        </radialGradient>
        <linearGradient id="hillGrad" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0" stop-color="#c8ed93" />
          <stop offset="1" stop-color="#8fd35a" />
        </linearGradient>
        <linearGradient id="grassGrad" x1="0" y1="0" x2="0.3" y2="1">
          <stop offset="0" stop-color="#a6df62" />
          <stop offset="1" stop-color="#5eb83c" />
        </linearGradient>
        <linearGradient id="canopyGrad" x1="0" y1="0" x2="0.4" y2="1">
          <stop offset="0" stop-color="#fffdf2" />
          <stop offset="1" stop-color="#f4e4b8" />
        </linearGradient>
        <filter id="softBlur" x="-30%" y="-30%" width="160%" height="160%">
          <feGaussianBlur stdDeviation="9" />
        </filter>
        <filter id="tinyBlur" x="-20%" y="-20%" width="140%" height="140%">
          <feGaussianBlur stdDeviation="2" />
        </filter>
      </defs>

      <rect width="1600" height="900" fill="url(#skyGrad)" />
      <circle cx="1340" cy="120" r="250" fill="url(#sunGlow)" />

      <!-- 云 -->
      <g class="cloud cloud-a" fill="#ffffff" opacity="0.72" filter="url(#softBlur)">
        <ellipse cx="220" cy="200" rx="150" ry="46" />
        <ellipse cx="300" cy="170" rx="92" ry="55" />
        <ellipse cx="130" cy="180" rx="80" ry="38" />
      </g>
      <g class="cloud cloud-b" fill="#ffffff" opacity="0.55" filter="url(#softBlur)">
        <ellipse cx="760" cy="140" rx="130" ry="36" />
        <ellipse cx="820" cy="118" rx="70" ry="40" />
        <ellipse cx="690" cy="128" rx="55" ry="28" />
      </g>
      <g class="cloud cloud-c" fill="#ffffff" opacity="0.48" filter="url(#softBlur)">
        <ellipse cx="1220" cy="500" rx="180" ry="42" />
        <ellipse cx="1320" cy="472" rx="92" ry="52" />
        <ellipse cx="1100" cy="484" rx="80" ry="30" />
      </g>
      <g class="cloud cloud-d" fill="#ffffff" opacity="0.38" filter="url(#softBlur)">
        <ellipse cx="520" cy="380" rx="100" ry="26" />
        <ellipse cx="580" cy="362" rx="55" ry="30" />
      </g>

      <!-- 远山 -->
      <path d="M0 620 C 140 548 260 588 360 612 C 470 640 540 580 660 600 C 780 620 840 570 960 596 C 1090 624 1180 572 1300 600 C 1420 628 1510 590 1600 606 L1600 900 L0 900 Z" fill="#d6f0c8" opacity="0.72" />
      <path d="M0 668 C 180 612 320 660 460 674 C 620 690 700 640 860 664 C 1010 686 1120 642 1280 668 C 1420 690 1510 660 1600 672 L1600 900 L0 900 Z" fill="url(#hillGrad)" opacity="0.85" />

      <!-- 画架 -->
      <g class="easel" stroke-linecap="round">
        <g stroke="#b6824f" stroke-width="10" opacity="0.9">
          <line x1="300" y1="468" x2="222" y2="792" />
          <line x1="322" y1="468" x2="410" y2="792" />
          <line x1="312" y1="478" x2="318" y2="806" />
          <line x1="246" y1="628" x2="384" y2="618" stroke-width="7" />
        </g>
        <g transform="rotate(3 320 420)">
          <rect x="230" y="318" width="184" height="214" rx="5" fill="#fffaf0" stroke="#a97945" stroke-width="7" />
          <rect x="246" y="336" width="152" height="178" rx="2" fill="#fffdfa" stroke="#e4cda9" stroke-width="2" />
          <g stroke="#c9b593" stroke-width="3" opacity="0.75">
            <line x1="262" y1="364" x2="380" y2="364" />
            <line x1="262" y1="386" x2="356" y2="386" />
            <line x1="262" y1="408" x2="374" y2="408" />
            <line x1="262" y1="430" x2="336" y2="430" />
            <line x1="262" y1="474" x2="300" y2="474" />
            <line x1="314" y1="474" x2="380" y2="474" />
          </g>
          <circle cx="392" cy="322" r="6" fill="#7fc9d9" />
        </g>
        <ellipse cx="320" cy="812" rx="118" ry="14" fill="#5a9e47" opacity="0.22" />
        <g transform="translate(448 690)">
          <ellipse cx="0" cy="46" rx="36" ry="8" fill="#5a9e47" opacity="0.22" />
          <path d="M-34 16 C-34 4 34 4 34 16 L28 40 C26 48 -26 48 -28 40 Z" fill="#f0cf9c" stroke="#b98a5a" stroke-width="3" />
          <circle cx="-14" cy="20" r="6" fill="#ff9fae" />
          <circle cx="2" cy="26" r="5" fill="#7fd7c9" />
          <circle cx="18" cy="18" r="6" fill="#ffd66e" />
          <path d="M-30 12 C-60 6 -64 -18 -48 -22" fill="none" stroke="#b98a5a" stroke-width="5" />
        </g>
      </g>

      <!-- 油纸伞 -->
      <g class="umbrella">
        <line x1="1258" y1="258" x2="1174" y2="838" stroke="#d8b783" stroke-width="11" stroke-linecap="round" />
        <path
          d="M1010 360 C1090 210 1310 168 1548 258 C1528 320 1498 382 1462 442 C1428 392 1392 400 1364 446 C1330 404 1296 412 1270 452 C1236 414 1200 422 1174 460 C1138 422 1100 432 1076 464 C1044 424 1022 392 1010 360Z"
          fill="url(#canopyGrad)"
          stroke="#7c9d92"
          stroke-width="4"
          opacity="0.93"
        />
        <g stroke="#a9bda8" stroke-width="2.5" opacity="0.75">
          <path d="M1258 242 L1076 464" />
          <path d="M1258 242 L1174 460" />
          <path d="M1258 242 L1270 452" />
          <path d="M1258 242 L1364 446" />
          <path d="M1258 242 L1462 442" />
          <path d="M1258 242 L1540 290" />
        </g>
        <path d="M1258 258 C1246 224 1218 210 1196 224" fill="none" stroke="#7c9d92" stroke-width="4" stroke-linecap="round" />
        <circle cx="1258" cy="250" r="8" fill="#f7e6b6" stroke="#7c9d92" stroke-width="3" />
      </g>

      <!-- 草地 -->
      <path d="M0 742 C 180 700 340 744 500 748 C 700 754 820 710 1000 730 C 1180 750 1320 706 1600 736 L1600 900 L0 900 Z" fill="url(#grassGrad)" />
      <g stroke="#4f9c34" stroke-width="3" stroke-linecap="round" opacity="0.6">
        <path v-for="(b, i) in blades" :key="i" :d="`M${b.x} 816 q ${b.lean} -${b.h} ${b.lean + 6} -${b.h + 16}`" />
      </g>
      <g fill="#fff4b8">
        <circle cx="180" cy="830" r="5" />
        <circle cx="188" cy="822" r="5" />
        <circle cx="174" cy="822" r="5" />
        <circle cx="181" cy="814" r="5" />
        <circle cx="180" cy="823" r="3" fill="#ffd45c" />
        <circle cx="1040" cy="846" r="5" />
        <circle cx="1048" cy="838" r="5" />
        <circle cx="1034" cy="838" r="5" />
        <circle cx="1041" cy="830" r="5" />
        <circle cx="1040" cy="839" r="3" fill="#ffd45c" />
        <circle cx="1390" cy="812" r="4.5" />
        <circle cx="1397" cy="805" r="4.5" />
        <circle cx="1384" cy="805" r="4.5" />
        <circle cx="1390" cy="798" r="4.5" />
        <circle cx="1390" cy="806" r="2.6" fill="#ffd45c" />
      </g>
      <g fill="#ffffff" opacity="0.85">
        <circle cx="760" cy="872" r="4" />
        <circle cx="766" cy="866" r="4" />
        <circle cx="756" cy="866" r="4" />
        <circle cx="761" cy="861" r="4" />
        <circle cx="760" cy="867" r="2.4" fill="#ffd45c" />
      </g>

      <!-- 飞散的花瓣 -->
      <g class="petals" fill="#fff7dd">
        <circle v-for="(p, i) in petals" :key="i" :cx="p.x" :cy="p.y" :r="p.r" :style="{ animationDelay: `${p.d}s` }" />
      </g>

      <rect width="1600" height="900" fill="url(#skyGrad)" opacity="0.02" />
    </svg>
    <div class="sky-wash"></div>
  </div>
</template>

<style scoped>
.sky-scene {
  position: fixed;
  inset: 0;
  z-index: -2;
  overflow: hidden;
  background: #a9e0f4;
}

.sky-svg {
  width: 100%;
  height: 100%;
  display: block;
}

.sky-wash {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at 68% 22%, rgba(255, 255, 255, 0.16), transparent 55%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.06), rgba(255, 255, 255, 0) 45%);
  pointer-events: none;
}

.cloud-a {
  animation: drift-a 46s ease-in-out infinite alternate;
}
.cloud-b {
  animation: drift-b 58s ease-in-out infinite alternate;
}
.cloud-c {
  animation: drift-a 64s ease-in-out infinite alternate-reverse;
}
.cloud-d {
  animation: drift-b 52s ease-in-out infinite alternate;
}

.petals circle {
  animation: float 9s ease-in-out infinite;
}

@keyframes drift-a {
  from {
    transform: translateX(-26px);
  }
  to {
    transform: translateX(38px);
  }
}
@keyframes drift-b {
  from {
    transform: translateX(18px);
  }
  to {
    transform: translateX(-34px);
  }
}
@keyframes float {
  0%,
  100% {
    transform: translateY(0);
    opacity: 0.35;
  }
  50% {
    transform: translateY(-22px);
    opacity: 0.85;
  }
}

@media (max-width: 760px) {
  .easel {
    opacity: 0.45;
  }
  .umbrella {
    opacity: 0.6;
  }
}

@media (prefers-reduced-motion: reduce) {
  .cloud,
  .petals circle {
    animation: none;
  }
}
</style>
