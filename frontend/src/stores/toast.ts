import { reactive } from 'vue'

export interface Toast {
  id: number
  text: string
  tone: 'ok' | 'warn' | 'info'
}

const state = reactive<{ items: Toast[] }>({ items: [] })
let seq = 0

export function notify(text: string, tone: Toast['tone'] = 'ok', duration = 3200) {
  const id = ++seq
  state.items.push({ id, text, tone })
  window.setTimeout(() => dismiss(id), duration)
}

export function dismiss(id: number) {
  const index = state.items.findIndex((item) => item.id === id)
  if (index >= 0) state.items.splice(index, 1)
}

export function useToast() {
  return { state, notify, dismiss }
}
