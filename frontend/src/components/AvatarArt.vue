<script setup lang="ts">
withDefaults(defineProps<{ size?: number; ring?: boolean; sleepy?: boolean }>(), {
  size: 96,
  ring: true,
  sleepy: false,
})
</script>

<template>
  <span class="avatar-art" :class="{ ring }" :style="{ width: `${size}px`, height: `${size}px` }">
    <svg viewBox="0 0 200 200" role="img" aria-label="云端的卡通头像">
      <defs>
        <linearGradient id="avatarBg" x1="20" y1="10" x2="180" y2="190">
          <stop offset="0" stop-color="#d9f5ff" />
          <stop offset="0.55" stop-color="#a8e2f4" />
          <stop offset="1" stop-color="#7fc9e8" />
        </linearGradient>
        <linearGradient id="hoodie" x1="50" y1="140" x2="150" y2="200">
          <stop offset="0" stop-color="#7fb6d6" />
          <stop offset="1" stop-color="#4c86ad" />
        </linearGradient>
        <linearGradient id="hair" x1="55" y1="50" x2="145" y2="150">
          <stop offset="0" stop-color="#606878" />
          <stop offset="0.55" stop-color="#3d4553" />
          <stop offset="1" stop-color="#272d39" />
        </linearGradient>
      </defs>

      <clipPath id="avatarClip">
        <circle cx="100" cy="100" r="99" />
      </clipPath>

      <g clip-path="url(#avatarClip)">
        <rect width="200" height="200" fill="url(#avatarBg)" />
        <circle cx="158" cy="42" r="46" fill="#ffffff" opacity="0.18" />
        <ellipse cx="38" cy="62" rx="34" ry="12" fill="#ffffff" opacity="0.38" />
        <ellipse cx="52" cy="56" rx="18" ry="12" fill="#ffffff" opacity="0.32" />
        <ellipse cx="146" cy="166" rx="46" ry="16" fill="#ffffff" opacity="0.12" />

        <path d="M44 200 C46 152 74 136 100 136 C126 136 154 152 156 200 Z" fill="url(#hoodie)" />
        <path d="M79 140 L100 158 L121 140" fill="none" stroke="#eaf7ff" stroke-width="5" stroke-linecap="round" opacity="0.7" />
        <path d="M92 156 L100 170 L108 156" fill="#f4fbff" opacity="0.85" />

        <ellipse cx="100" cy="112" rx="45" ry="42" fill="#ffe3cf" />
        <ellipse cx="57" cy="111" rx="7" ry="10" fill="#ffd9c2" />
        <ellipse cx="143" cy="111" rx="7" ry="10" fill="#ffd9c2" />

        <path
          d="M54 108 C50 72 69 49 100 49 C131 49 150 72 146 108 C142 95 136 86 129 80 C123 92 116 98 108 101 C108 88 103 79 95 73 C92 89 82 99 68 104 C64 106 60 108 54 108Z"
          fill="url(#hair)"
        />
        <path d="M55 104 C48 104 47 118 55 121" fill="#c4bebb" />
        <path d="M145 104 C152 104 153 118 145 121" fill="#c4bebb" />

        <g v-if="!sleepy">
          <path d="M71 107 C76 103 82 103 87 107" fill="none" stroke="#3d3a44" stroke-width="2.2" stroke-linecap="round" opacity="0.75" />
          <path d="M113 107 C118 103 124 103 129 107" fill="none" stroke="#3d3a44" stroke-width="2.2" stroke-linecap="round" opacity="0.75" />
          <ellipse cx="78" cy="117" rx="4.6" ry="5.8" fill="#3b3036" />
          <ellipse cx="122" cy="117" rx="4.6" ry="5.8" fill="#3b3036" />
          <circle cx="79.5" cy="115" r="1.6" fill="#ffffff" />
          <circle cx="123.5" cy="115" r="1.6" fill="#ffffff" />
          <path d="M72 118 C74 113 79 111 83 114" fill="none" stroke="#2c242b" stroke-width="1.4" stroke-linecap="round" />
          <path d="M116 114 C120 111 125 113 127 118" fill="none" stroke="#2c242b" stroke-width="1.4" stroke-linecap="round" />
        </g>
        <g v-else>
          <path d="M73 117 C79 113 85 113 91 117" fill="none" stroke="#4a3b3b" stroke-width="2.8" stroke-linecap="round" />
          <path d="M109 117 C115 113 121 113 127 117" fill="none" stroke="#4a3b3b" stroke-width="2.8" stroke-linecap="round" />
        </g>

        <ellipse cx="68" cy="127" rx="8" ry="4.5" fill="#ffb2ae" opacity="0.6" />
        <ellipse cx="132" cy="127" rx="8" ry="4.5" fill="#ffb2ae" opacity="0.6" />
        <path d="M97 123 C99 127 101 127 103 123" fill="none" stroke="#d98d86" stroke-width="1.8" stroke-linecap="round" opacity="0.7" />
        <path d="M94 131 C98 136 103 136 106 131" fill="none" stroke="#d98d86" stroke-width="2.4" stroke-linecap="round" />

        <path d="M127 58 C132 46 144 44 149 50 C156 43 169 46 169 58 C169 68 153 74 146 66 C140 74 126 69 127 58Z" fill="#eaf9ff" opacity="0.9" />
        <path d="M137 60 C139 57 142 57 144 60" stroke="#7cb7cb" stroke-width="1.6" fill="none" stroke-linecap="round" />
        <path d="M151 60 C153 57 156 57 158 60" stroke="#7cb7cb" stroke-width="1.6" fill="none" stroke-linecap="round" />

        <path d="M94 49 C91 39 101 35 106 42 C110 34 119 38 115 47" fill="none" stroke="#3d4452" stroke-width="4" stroke-linecap="round" />
        <circle cx="143" cy="93" r="5" fill="#8fd6b6" />
        <path d="M143 88 C147 84 151 87 150 91 C148 95 144 95 143 91Z" fill="#5fb98f" />
        <path d="M132 62 l1.4 4.2 4.2 1.4 -4.2 1.4 -1.4 4.2 -1.4 -4.2 -4.2 -1.4 4.2 -1.4Z" fill="#ffffff" opacity="0.7" />

        <g fill="#ffffff" opacity="0.45">
          <circle cx="32" cy="150" r="3" />
          <circle cx="172" cy="126" r="2.4" />
          <circle cx="42" cy="94" r="2" />
        </g>
      </g>
      <circle cx="100" cy="100" r="98" fill="none" stroke="#ffffff" stroke-width="2" opacity="0.7" />
    </svg>
  </span>
</template>

<style scoped>
.avatar-art {
  display: inline-flex;
  border-radius: 50%;
  flex: none;
  background: #bfeaf8;
  overflow: hidden;
}

.avatar-art.ring {
  box-shadow:
    0 0 0 4px rgba(255, 255, 255, 0.9),
    0 10px 28px rgba(30, 102, 130, 0.22);
}

.avatar-art :deep(svg) {
  width: 100%;
  height: 100%;
  display: block;
}
</style>
