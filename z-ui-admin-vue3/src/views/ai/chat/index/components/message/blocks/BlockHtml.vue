<template>
  <div class="block-html">
    <div v-if="!parsed" class="block-loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载内容中...</span>
    </div>
    <iframe
      v-else
      :srcdoc="parsed.content"
      sandbox="allow-scripts"
      class="html-iframe"
    ></iframe>
  </div>
</template>

<script setup lang="ts">
import { Loading } from '@element-plus/icons-vue'

const props = defineProps<{
  content: string
  complete: boolean
}>()

const parsed = computed(() => {
  if (!props.complete) return null
  try {
    const data = JSON.parse(props.content)
    return {
      content: data.content || data.data?.content || props.content
    }
  } catch {
    // 如果不是 JSON，直接作为 HTML 内容
    return { content: props.content }
  }
})
</script>

<style scoped>
.block-html {
  margin: 12px 0;
}
.html-iframe {
  width: 100%;
  min-height: 300px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  background: var(--el-bg-color);
}
.block-loading {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 20px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}
</style>
