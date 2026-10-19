<script setup lang="ts">
import { CheckCircle2, Info, X, AlertCircle } from 'lucide-vue-next'
import { useToast } from '../stores/toast'

const { state, dismiss } = useToast()
</script>

<template>
  <Teleport to="body">
    <div class="toast-stack" aria-live="polite" aria-atomic="false">
      <TransitionGroup name="toast">
        <div v-for="item in state.items" :key="item.id" class="toast" :class="item.tone">
          <CheckCircle2 v-if="item.tone === 'ok'" :size="17" />
          <AlertCircle v-else-if="item.tone === 'warn'" :size="17" />
          <Info v-else :size="17" />
          <span>{{ item.text }}</span>
          <button type="button" aria-label="关闭提示" @click="dismiss(item.id)">
            <X :size="14" />
          </button>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<style scoped>
.toast-stack {
  position: fixed;
  top: 84px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 120;
  display: grid;
  gap: 8px;
  width: min(420px, calc(100vw - 28px));
  pointer-events: none;
}

.toast {
  pointer-events: auto;
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 11px 12px 11px 15px;
  border-radius: 13px;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 16px 36px rgba(24, 82, 104, 0.18);
  font-size: 13px;
  color: #2b5665;
}

.toast.ok svg {
  color: #34b18b;
}
.toast.warn svg {
  color: #e29b43;
}
.toast.info svg {
  color: #4b9fce;
}

.toast span {
  flex: 1;
}

.toast button {
  border: 0;
  background: transparent;
  color: #92a9b1;
  cursor: pointer;
  display: grid;
  place-items: center;
  padding: 2px;
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.25s ease;
}
.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.97);
}
</style>
