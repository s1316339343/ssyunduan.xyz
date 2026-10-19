<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{ variant?: string; ratio?: string }>(), {
  variant: 'cloud',
  ratio: '16 / 10',
})

const uid = `art${Math.random().toString(36).slice(2, 9)}`

const themes: Record<string, [string, string, string]> = {
  pond: ['#062e2b', '#17705f', '#f3d89a'],
  code: ['#12233f', '#2b5f8f', '#8fe3ff'],
  hook: ['#e8a12e', '#ffd982', '#5a3a12'],
  web: ['#0d2947', '#1e6f96', '#6fe0ee'],
  deploy: ['#1e6f9e', '#79cfe4', '#ecfbff'],
  api: ['#2d6049', '#84c98e', '#f5efb0'],
  python: ['#173e66', '#e4b93c', '#ffefb3'],
  mountain: ['#8dcaE8', '#e9f7ef', '#3f7d93'],
  valley: ['#8ac9a1', '#2f7a68', '#e8f8cf'],
  town: ['#e9b97d', '#f7e0bd', '#9a5a40'],
  night: ['#07182e', '#14456b', '#ffd479'],
  sea: ['#2099ae', '#9be3d8', '#fff3c9'],
  forest: ['#2c6240', '#90c878', '#e8f5cf'],
  sunset: ['#ef7f68', '#6f477f', '#ffe3a3'],
  cafe: ['#e7cfa9', '#a9784f', '#fff8ea'],
  pen: ['#d84a46', '#f39a8f', '#fff1e4'],
  cloud: ['#7fcbe8', '#e6f8ff', '#ffffff'],
  letter: ['#f2e2c4', '#cba76f', '#fffaf0'],
  cat: ['#18272d', '#3f5c64', '#f2d17c'],
  flower: ['#ef9fb3', '#fbe0e4', '#fff8f5'],
  book: ['#b9783b', '#f0d19b', '#5f3a1c'],
}

const theme = computed(() => themes[props.variant] || themes.cloud)
const gradId = computed(() => `g-${uid}`)
const glowId = computed(() => `glow-${uid}`)
</script>

<template>
  <div class="art-cover" :style="{ aspectRatio: ratio }">
    <svg viewBox="0 0 640 400" preserveAspectRatio="xMidYMid slice" role="img" :aria-label="`${variant} 主题插画`">
      <defs>
        <linearGradient :id="gradId" x1="0" y1="0" x2="1" y2="1">
          <stop offset="0" :stop-color="theme[0]" />
          <stop offset="1" :stop-color="theme[1]" />
        </linearGradient>
        <radialGradient :id="glowId" cx="50%" cy="50%" r="50%">
          <stop offset="0" :stop-color="theme[2]" stop-opacity="0.5" />
          <stop offset="1" :stop-color="theme[2]" stop-opacity="0" />
        </radialGradient>
        <pattern :id="`dots-${uid}`" width="14" height="14" patternUnits="userSpaceOnUse">
          <circle cx="2" cy="2" r="1" fill="#ffffff" opacity="0.16" />
        </pattern>
      </defs>

      <rect width="640" height="400" :fill="`url(#${gradId})`" />
      <circle cx="510" cy="60" r="190" :fill="`url(#${glowId})`" />
      <rect width="640" height="400" :fill="`url(#dots-${uid})`" />

      <!-- 池塘与猫 -->
      <g v-if="variant === 'pond'">
        <path d="M0 250 C120 220 180 268 280 250 C400 228 450 300 560 268 C600 256 620 260 640 268 L640 400 L0 400Z" fill="#0b4a40" opacity="0.8" />
        <path d="M0 300 C130 266 220 316 340 300 C470 282 540 338 640 310 L640 400 L0 400Z" fill="#0a3a34" opacity="0.85" />
        <ellipse cx="320" cy="330" rx="150" ry="20" fill="#d9fff2" opacity="0.12" />
        <g transform="translate(320 240)">
          <path d="M-40 80 C-52 42 -42 6 -16 -12 C-6 -20 6 -20 16 -12 C42 6 52 42 40 80Z" fill="#041b18" />
          <path d="M-26 -16 L-34 -54 L-4 -30Z" fill="#041b18" />
          <path d="M26 -16 L34 -54 L4 -30Z" fill="#041b18" />
          <ellipse cx="0" cy="-14" rx="34" ry="28" fill="#041b18" />
          <circle cx="-11" cy="-16" r="4" fill="#f2d17c" />
          <circle cx="11" cy="-16" r="4" fill="#f2d17c" />
          <path d="M-4 0 L4 0 L0 4Z" fill="#d9a9a2" />
          <path d="M38 60 C76 70 84 34 64 22" stroke="#041b18" stroke-width="10" fill="none" stroke-linecap="round" />
        </g>
        <g stroke="#bffce9" stroke-width="1" opacity="0.3">
          <line x1="90" y1="80" x2="80" y2="130" />
          <line x1="180" y1="40" x2="172" y2="96" />
          <line x1="480" y1="70" x2="470" y2="126" />
          <line x1="570" y1="120" x2="562" y2="170" />
        </g>
      </g>

      <!-- 代码 -->
      <g v-else-if="variant === 'code'">
        <text x="320" y="252" text-anchor="middle" font-family="Consolas, monospace" font-size="150" font-weight="700" :fill="theme[2]" opacity="0.9">&lt;/&gt;</text>
        <g stroke="#9fdcff" stroke-width="7" stroke-linecap="round" opacity="0.42">
          <line x1="86" y1="120" x2="176" y2="120" />
          <line x1="86" y1="146" x2="150" y2="146" />
          <line x1="464" y1="286" x2="554" y2="286" />
          <line x1="490" y1="312" x2="554" y2="312" />
        </g>
        <circle cx="100" cy="300" r="26" fill="none" stroke="#8fe3ff" stroke-width="5" opacity="0.5" />
        <circle cx="540" cy="104" r="16" fill="#8fe3ff" opacity="0.55" />
      </g>

      <!-- Hook -->
      <g v-else-if="variant === 'hook'">
        <path d="M250 90 C250 80 262 74 272 80 L300 98 C340 122 350 168 326 208 C304 244 262 256 224 240" fill="none" :stroke="theme[2]" stroke-width="18" stroke-linecap="round" opacity="0.88" />
        <path d="M224 240 C206 232 200 212 212 198" fill="none" :stroke="theme[2]" stroke-width="18" stroke-linecap="round" opacity="0.88" />
        <rect x="388" y="130" width="160" height="110" rx="10" fill="#fff8e7" opacity="0.9" />
        <rect x="404" y="146" width="128" height="78" rx="5" fill="#5a3a12" opacity="0.75" />
        <path d="M370 262 L560 262" stroke="#5a3a12" stroke-width="10" stroke-linecap="round" opacity="0.55" />
        <circle cx="120" cy="310" r="38" fill="#5a3a12" opacity="0.18" />
        <text x="120" y="324" text-anchor="middle" font-family="Consolas, monospace" font-size="46" font-weight="700" :fill="theme[2]" opacity="0.85">{}</text>
      </g>

      <!-- Web 立方体 -->
      <g v-else-if="variant === 'web'">
        <g stroke="#8ee7f3" stroke-width="3" fill="none" opacity="0.75">
          <path d="M320 86 L420 140 L420 250 L320 304 L220 250 L220 140Z" />
          <path d="M220 140 L320 194 L420 140 M320 194 L320 304" />
          <path d="M270 112 L370 166 L370 276 L270 330" opacity="0.4" />
          <path d="M370 112 L470 166 L470 276 L370 330" opacity="0.4" />
        </g>
        <g fill="#8ee7f3" opacity="0.88">
          <circle cx="320" cy="86" r="7" />
          <circle cx="420" cy="140" r="7" />
          <circle cx="220" cy="140" r="7" />
          <circle cx="320" cy="304" r="7" />
        </g>
        <circle cx="498" cy="84" r="26" fill="none" stroke="#8ee7f3" stroke-width="4" opacity="0.5" />
      </g>

      <!-- 部署 -->
      <g v-else-if="variant === 'deploy'">
        <g opacity="0.92">
          <path d="M120 292 L240 226 L360 292 L240 358Z" fill="#124b6b" />
          <path d="M120 292 L240 226 L240 286 L120 352Z" fill="#2b7fa4" />
          <path d="M240 226 L360 292 L360 352 L240 286Z" fill="#1b6288" />
          <path d="M210 214 L310 160 L410 214 L310 268Z" fill="#176485" />
          <path d="M210 214 L310 160 L310 206 L210 260Z" fill="#4aa6c4" />
          <path d="M310 160 L410 214 L410 260 L310 206Z" fill="#2b86a6" />
          <path d="M300 130 L380 88 L460 130 L380 172Z" fill="#1f728f" />
          <path d="M300 130 L380 88 L380 122 L300 164Z" fill="#6ac7de" />
          <path d="M380 88 L460 130 L460 164 L380 122Z" fill="#3da2bf" />
        </g>
        <circle cx="120" cy="110" r="42" fill="#e8fbff" opacity="0.16" />
      </g>

      <!-- API -->
      <g v-else-if="variant === 'api'">
        <g transform="translate(230 200)">
          <circle r="82" fill="#1d4736" opacity="0.55" />
          <g v-for="i in 10" :key="i" :transform="`rotate(${i * 36})`">
            <rect x="-12" y="-110" width="24" height="34" rx="7" :fill="theme[2]" opacity="0.7" />
          </g>
          <circle r="66" fill="#2f6b52" />
          <circle r="52" fill="#173b2c" />
        </g>
        <text x="230" y="222" text-anchor="middle" font-family="Consolas, monospace" font-size="52" font-weight="700" fill="#f5efb0">API</text>
        <g fill="none" stroke="#f5efb0" stroke-width="4" opacity="0.65">
          <path d="M388 136 L368 136 L368 264 L388 264" />
          <path d="M456 136 L476 136 L476 264 L456 264" />
        </g>
        <circle cx="430" cy="200" r="8" fill="#f5efb0" />
      </g>

      <!-- Python -->
      <g v-else-if="variant === 'python'">
        <path d="M270 88 C200 88 196 132 196 166 L196 196 L276 196 L276 214 L172 214 C130 214 108 242 108 286" fill="none" stroke="#ffd94e" stroke-width="26" stroke-linecap="round" opacity="0.9" />
        <path d="M370 312 C440 312 444 268 444 234 L444 204 L364 204 L364 186 L468 186 C510 186 532 158 532 114" fill="none" stroke="#ffd94e" stroke-width="26" stroke-linecap="round" opacity="0.9" />
        <circle cx="278" cy="128" r="7" fill="#173e66" />
        <circle cx="362" cy="272" r="7" fill="#173e66" />
        <g stroke="#fff2b8" stroke-width="3" opacity="0.45">
          <line x1="90" y1="340" x2="550" y2="340" />
          <line x1="90" y1="360" x2="430" y2="360" />
        </g>
      </g>

      <!-- 山 -->
      <g v-else-if="variant === 'mountain'">
        <circle cx="500" cy="96" r="58" fill="#fff8d8" opacity="0.9" />
        <path d="M0 300 L150 168 L270 282 L380 200 L520 320 L640 230 L640 400 L0 400Z" fill="#8ec9c0" opacity="0.75" />
        <path d="M0 336 L120 246 L230 330 L370 246 L520 350 L640 290 L640 400 L0 400Z" fill="#4d9899" />
        <path d="M120 246 L156 218 L188 250 L160 240Z" fill="#effbf5" />
        <path d="M370 246 L410 214 L446 252 L414 240Z" fill="#effbf5" />
        <path d="M0 372 L160 330 L320 378 L480 338 L640 372 L640 400 L0 400Z" fill="#2f6b73" opacity="0.8" />
        <path d="M120 100 C132 88 146 90 152 104 C140 102 130 104 120 100Z" fill="#3f7d93" opacity="0.5" />
      </g>

      <!-- 山谷与河流 -->
      <g v-else-if="variant === 'valley'">
        <path d="M0 400 L0 250 C90 190 170 230 250 196 C340 158 420 198 520 170 C570 156 610 166 640 154 L640 400Z" fill="#245f52" opacity="0.45" />
        <path d="M0 400 L0 300 C100 250 190 292 290 266 C390 240 470 278 570 252 C600 244 625 248 640 242 L640 400Z" fill="#4d9672" opacity="0.82" />
        <path d="M0 400 L0 350 C120 318 230 352 340 332 C450 312 550 344 640 320 L640 400Z" fill="#2f7a5f" />
        <path d="M505 -20 C455 70 520 120 470 190 C430 246 480 300 442 360 C424 390 432 400 430 420" fill="none" stroke="#c6ead6" stroke-width="34" stroke-linecap="round" opacity="0.75" />
        <path d="M505 -20 C455 70 520 120 470 190 C430 246 480 300 442 360" fill="none" stroke="#e9fbef" stroke-width="10" stroke-linecap="round" opacity="0.6" />
        <g fill="#1e5747" opacity="0.55">
          <circle cx="90" cy="320" r="12" />
          <circle cx="118" cy="334" r="9" />
          <circle cx="150" cy="316" r="14" />
          <circle cx="190" cy="340" r="8" />
          <circle cx="240" cy="326" r="11" />
          <circle cx="300" cy="348" r="9" />
          <circle cx="350" cy="330" r="13" />
          <circle cx="410" cy="352" r="8" />
          <circle cx="580" cy="306" r="11" />
          <circle cx="608" cy="320" r="8" />
        </g>
        <g fill="#f7ffe9" opacity="0.6">
          <circle cx="204" cy="90" r="3" />
          <circle cx="238" cy="128" r="2.2" />
          <circle cx="176" cy="148" r="2.6" />
          <circle cx="540" cy="86" r="3" />
        </g>
        <circle cx="105" cy="92" r="50" fill="#fff8d8" opacity="0.62" />
      </g>

      <!-- 老城 -->
      <g v-else-if="variant === 'town'">
        <circle cx="470" cy="100" r="70" fill="#fff1c9" opacity="0.75" />
        <path d="M0 300 L640 300 L640 400 L0 400Z" fill="#c17d55" opacity="0.55" />
        <g fill="#9a5a40">
          <rect x="56" y="178" width="120" height="150" />
          <rect x="196" y="140" width="150" height="188" />
          <rect x="366" y="196" width="120" height="132" />
          <rect x="506" y="160" width="96" height="168" />
        </g>
        <g fill="#ffe9b8" opacity="0.8">
          <rect x="76" y="200" width="22" height="28" />
          <rect x="112" y="200" width="22" height="28" />
          <rect x="76" y="246" width="22" height="28" />
          <rect x="112" y="246" width="22" height="28" />
          <rect x="222" y="166" width="26" height="30" />
          <rect x="268" y="166" width="26" height="30" />
          <rect x="222" y="218" width="26" height="30" />
          <rect x="268" y="218" width="26" height="30" />
          <rect x="388" y="218" width="24" height="28" />
          <rect x="432" y="218" width="24" height="28" />
          <rect x="528" y="182" width="22" height="26" />
          <rect x="560" y="182" width="22" height="26" />
          <rect x="528" y="226" width="22" height="26" />
        </g>
        <g stroke="#6f3f2c" stroke-width="4" opacity="0.5">
          <path d="M56 178 L176 178 L116 150Z" fill="#7a4630" />
          <path d="M196 140 L346 140 L271 104Z" fill="#7a4630" />
          <path d="M506 160 L602 160 L554 132Z" fill="#7a4630" />
        </g>
      </g>

      <!-- 江边夜色 -->
      <g v-else-if="variant === 'night'">
        <circle cx="510" cy="86" r="44" fill="#ffe6b0" opacity="0.85" />
        <g fill="#0b2947">
          <rect x="60" y="216" width="42" height="110" />
          <rect x="114" y="176" width="58" height="150" />
          <rect x="184" y="238" width="36" height="88" />
          <rect x="232" y="152" width="54" height="174" />
          <rect x="298" y="208" width="44" height="118" />
          <rect x="352" y="184" width="72" height="142" />
          <rect x="438" y="230" width="40" height="96" />
          <rect x="490" y="160" width="62" height="166" />
          <rect x="564" y="214" width="46" height="112" />
        </g>
        <g fill="#ffd479" opacity="0.85">
          <rect x="124" y="190" width="8" height="10" />
          <rect x="142" y="190" width="8" height="10" />
          <rect x="124" y="212" width="8" height="10" />
          <rect x="142" y="232" width="8" height="10" />
          <rect x="244" y="168" width="8" height="10" />
          <rect x="262" y="188" width="8" height="10" />
          <rect x="244" y="216" width="8" height="10" />
          <rect x="366" y="200" width="8" height="10" />
          <rect x="386" y="224" width="8" height="10" />
          <rect x="502" y="176" width="8" height="10" />
          <rect x="522" y="198" width="8" height="10" />
          <rect x="502" y="224" width="8" height="10" />
        </g>
        <path d="M0 326 L640 326 L640 400 L0 400Z" fill="#071a30" opacity="0.9" />
        <g stroke="#ffcf76" stroke-width="3" opacity="0.35">
          <line x1="80" y1="338" x2="150" y2="338" />
          <line x1="240" y1="350" x2="330" y2="350" />
          <line x1="410" y1="340" x2="500" y2="340" />
          <line x1="530" y1="362" x2="610" y2="362" />
        </g>
      </g>

      <!-- 海 -->
      <g v-else-if="variant === 'sea'">
        <circle cx="470" cy="110" r="60" fill="#fff4c7" opacity="0.9" />
        <path d="M0 260 C90 230 150 280 240 258 C330 236 390 284 480 262 C560 242 600 268 640 258 L640 400 L0 400Z" fill="#55bdb7" opacity="0.8" />
        <path d="M0 306 C110 274 170 322 280 302 C380 284 450 330 540 308 C590 296 620 306 640 302 L640 400 L0 400Z" fill="#2a8e9c" />
        <path d="M0 348 C120 322 200 362 320 346 C440 330 520 370 640 344 L640 400 L0 400Z" fill="#16647a" />
        <g stroke="#fff8da" stroke-width="3" opacity="0.6">
          <path d="M360 300 C380 292 400 292 420 300" fill="none" />
          <path d="M120 330 C140 322 160 322 180 330" fill="none" />
          <path d="M470 350 C490 342 510 342 530 350" fill="none" />
        </g>
        <g stroke="#fff8da" stroke-width="2" opacity="0.5">
          <path d="M488 84 l10 -18 l6 18" fill="none" />
          <path d="M518 78 l8 -14 l5 14" fill="none" />
        </g>
      </g>

      <!-- 森林 -->
      <g v-else-if="variant === 'forest'">
        <path d="M0 400 L0 280 C120 250 220 300 320 284 C440 264 540 300 640 276 L640 400Z" fill="#1e4c33" opacity="0.6" />
        <g fill="#2e6b45">
          <path d="M100 330 L150 180 L200 330Z" />
          <path d="M190 350 L250 170 L310 350Z" />
          <path d="M420 340 L480 160 L540 340Z" />
          <path d="M520 360 L570 220 L620 360Z" />
        </g>
        <g fill="#8fcb7c" opacity="0.8">
          <path d="M130 330 L150 238 L170 330Z" />
          <path d="M222 350 L250 224 L278 350Z" />
          <path d="M452 340 L480 214 L508 340Z" />
        </g>
        <rect x="146" y="330" width="8" height="34" fill="#5b3f25" />
        <rect x="246" y="350" width="8" height="30" fill="#5b3f25" />
        <rect x="476" y="340" width="8" height="34" fill="#5b3f25" />
        <g stroke="#e8f5cf" stroke-width="2" opacity="0.28">
          <line x1="40" y1="120" x2="180" y2="120" />
          <line x1="80" y1="144" x2="250" y2="144" />
          <line x1="380" y1="96" x2="560" y2="96" />
        </g>
      </g>

      <!-- 日落 -->
      <g v-else-if="variant === 'sunset'">
        <circle cx="320" cy="238" r="118" fill="#ffe3a3" opacity="0.9" />
        <path d="M0 300 C100 270 180 312 280 298 C390 282 470 322 560 302 C600 294 620 298 640 302 L640 400 L0 400Z" fill="#4d2f52" opacity="0.62" />
        <path d="M0 344 C140 312 260 358 400 340 C500 326 580 350 640 342 L640 400 L0 400Z" fill="#35203b" opacity="0.7" />
        <g stroke="#4b2c3f" stroke-width="4" fill="none" opacity="0.65">
          <path d="M110 110 q12 -12 24 0 q12 -12 24 0" />
          <path d="M470 76 q10 -10 20 0 q10 -10 20 0" />
          <path d="M520 140 q8 -8 16 0 q8 -8 16 0" />
        </g>
        <g stroke="#ffe3a3" stroke-width="2" opacity="0.5">
          <line x1="264" y1="238" x2="376" y2="238" />
          <line x1="288" y1="262" x2="352" y2="262" />
        </g>
      </g>

      <!-- 咖啡 -->
      <g v-else-if="variant === 'cafe'">
        <rect x="0" y="296" width="640" height="104" fill="#a9784f" opacity="0.42" />
        <ellipse cx="300" cy="292" rx="98" ry="18" fill="#7d5434" opacity="0.2" />
        <path d="M220 196 L380 196 L362 292 C358 308 242 308 238 292Z" fill="#fffaf0" />
        <path d="M380 214 C432 206 440 262 386 264" fill="none" stroke="#fffaf0" stroke-width="16" />
        <ellipse cx="300" cy="196" rx="80" ry="16" fill="#7d5434" />
        <g stroke="#c7a37c" stroke-width="6" fill="none" stroke-linecap="round" opacity="0.65">
          <path d="M276 160 C262 140 284 130 272 108" />
          <path d="M306 158 C294 136 316 126 306 102" />
          <path d="M334 160 C324 142 342 132 334 114" />
        </g>
        <g fill="#8a5c38" opacity="0.55">
          <ellipse cx="118" cy="262" rx="24" ry="10" transform="rotate(-18 118 262)" />
          <ellipse cx="520" cy="240" rx="20" ry="8" transform="rotate(22 520 240)" />
        </g>
        <rect x="486" y="148" width="86" height="112" rx="8" fill="#fffaf0" opacity="0.8" />
        <g stroke="#c7a37c" stroke-width="4" opacity="0.7">
          <line x1="502" y1="176" x2="556" y2="176" />
          <line x1="502" y1="196" x2="546" y2="196" />
          <line x1="502" y1="216" x2="556" y2="216" />
        </g>
      </g>

      <!-- 笔 -->
      <g v-else-if="variant === 'pen'">
        <path d="M256 80 L424 248 L360 312 L192 144Z" fill="#fff1e4" opacity="0.92" />
        <path d="M424 248 L462 326 L384 288Z" fill="#7c2f2c" />
        <path d="M256 80 L300 124 L236 188 L192 144Z" fill="#f6b6ac" opacity="0.8" />
        <g stroke="#d84a46" stroke-width="3" opacity="0.4">
          <line x1="96" y1="250" x2="250" y2="250" />
          <line x1="96" y1="276" x2="210" y2="276" />
          <line x1="418" y1="100" x2="556" y2="100" />
          <line x1="448" y1="126" x2="556" y2="126" />
        </g>
        <circle cx="520" cy="300" r="36" fill="#fff1e4" opacity="0.16" />
      </g>

      <!-- 云与纸飞机 -->
      <g v-else-if="variant === 'cloud'">
        <g fill="#ffffff" opacity="0.7">
          <ellipse cx="150" cy="130" rx="90" ry="26" />
          <ellipse cx="190" cy="112" rx="48" ry="30" />
          <ellipse cx="480" cy="260" rx="100" ry="28" />
          <ellipse cx="520" cy="242" rx="52" ry="32" />
        </g>
        <path d="M300 220 L500 150 L420 250 L392 214Z" fill="#ffffff" opacity="0.92" />
        <path d="M392 214 L500 150 L430 268Z" fill="#dff6ff" opacity="0.9" />
        <path d="M300 220 L392 214 L430 268" fill="none" stroke="#ffffff" stroke-width="3" opacity="0.7" />
        <path d="M300 220 C260 236 236 262 208 300" fill="none" stroke="#ffffff" stroke-width="3" stroke-dasharray="7 9" opacity="0.6" />
      </g>

      <!-- 信 -->
      <g v-else-if="variant === 'letter'">
        <rect x="132" y="106" width="376" height="220" rx="10" fill="#fffaf0" />
        <path d="M132 126 L320 250 L508 126" fill="none" stroke="#cba76f" stroke-width="5" />
        <path d="M132 306 L272 208 M508 306 L368 208" fill="none" stroke="#e0c69b" stroke-width="4" />
        <rect x="448" y="128" width="44" height="52" rx="3" fill="#ef9f93" />
        <g stroke="#8c6f4a" stroke-width="3" opacity="0.4">
          <line x1="170" y1="150" x2="300" y2="150" />
          <line x1="170" y1="172" x2="270" y2="172" />
        </g>
        <path d="M100 330 C120 310 144 318 152 340" fill="none" stroke="#cba76f" stroke-width="3" opacity="0.5" />
      </g>

      <!-- 猫 -->
      <g v-else-if="variant === 'cat'">
        <g transform="translate(320 196)">
          <path d="M-100 100 C-116 40 -86 -30 -34 -66 C-12 -82 12 -82 34 -66 C86 -30 116 40 100 100Z" fill="#0b171b" />
          <path d="M-66 -58 L-86 -126 L-18 -86Z" fill="#0b171b" />
          <path d="M66 -58 L86 -126 L18 -86Z" fill="#0b171b" />
          <circle cx="-34" cy="-30" r="10" fill="#f2d17c" />
          <circle cx="34" cy="-30" r="10" fill="#f2d17c" />
          <path d="M-10 4 L10 4 L0 14Z" fill="#d9a9a2" />
          <path d="M0 14 C-6 24 -16 22 -20 16 M0 14 C6 24 16 22 20 16" stroke="#8f7f74" stroke-width="3" fill="none" stroke-linecap="round" />
          <g stroke="#c9fff0" stroke-width="2" opacity="0.4">
            <path d="M-44 10 L-96 2" />
            <path d="M-44 22 L-94 30" />
            <path d="M44 10 L96 2" />
            <path d="M44 22 L94 30" />
          </g>
        </g>
      </g>

      <!-- 花 -->
      <g v-else-if="variant === 'flower'">
        <g v-for="(item, i) in [{x:180,y:150,s:1},{x:360,y:250,s:1.35},{x:500,y:120,s:0.85},{x:120,y:310,s:0.7}]" :key="i" :transform="`translate(${item.x} ${item.y}) scale(${item.s})`">
          <g fill="#fff8f5" opacity="0.92">
            <circle cx="0" cy="-30" r="22" />
            <circle cx="28" cy="-9" r="22" />
            <circle cx="17" cy="24" r="22" />
            <circle cx="-17" cy="24" r="22" />
            <circle cx="-28" cy="-9" r="22" />
          </g>
          <circle r="13" fill="#ffd166" />
        </g>
        <g stroke="#5f8b62" stroke-width="4" opacity="0.6" fill="none">
          <path d="M180 180 C170 240 200 290 190 340" />
          <path d="M360 280 C360 320 340 350 350 380" />
          <path d="M500 150 C490 200 520 240 510 280" />
        </g>
      </g>

      <!-- 书 -->
      <g v-else-if="variant === 'book'">
        <path d="M120 150 C180 128 250 136 316 170 L316 318 C250 286 180 280 120 300Z" fill="#fff8e8" />
        <path d="M512 150 C452 128 382 136 316 170 L316 318 C382 286 452 280 512 300Z" fill="#fdf1d6" />
        <path d="M316 170 L316 318" stroke="#b9783b" stroke-width="5" />
        <g stroke="#c9a26b" stroke-width="3" opacity="0.7">
          <line x1="154" y1="178" x2="286" y2="204" />
          <line x1="154" y1="204" x2="286" y2="230" />
          <line x1="154" y1="230" x2="250" y2="250" />
          <line x1="346" y1="204" x2="478" y2="178" />
          <line x1="346" y1="230" x2="478" y2="204" />
          <line x1="346" y1="256" x2="440" y2="232" />
        </g>
        <path d="M86 322 L316 300 L546 322 L316 348Z" fill="#5f3a1c" opacity="0.45" />
      </g>

      <!-- 兜底云 -->
      <g v-else fill="#ffffff" opacity="0.7">
        <ellipse cx="240" cy="200" rx="120" ry="40" />
        <ellipse cx="300" cy="176" rx="64" ry="42" />
        <ellipse cx="420" cy="250" rx="90" ry="30" />
      </g>

      <rect x="0.5" y="0.5" width="639" height="399" fill="none" stroke="#ffffff" stroke-width="1" opacity="0.12" />
    </svg>
  </div>
</template>

<style scoped>
.art-cover {
  width: 100%;
  overflow: hidden;
  background: #cfeaf3;
}

.art-cover svg {
  width: 100%;
  height: 100%;
  display: block;
}
</style>
