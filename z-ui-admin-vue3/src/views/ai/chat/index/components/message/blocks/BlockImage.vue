<template>
  <div class="block-image">
    <div v-if="!parsed" class="block-loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载图片中...</span>
    </div>
    <img
      v-else
      :src="parsed.url"
      :alt="parsed.alt || ''"
      :style="{
        maxWidth: parsed.width ? `${parsed.width}px` : '100%',
        borderRadius: '8px'
      }"
      loading="lazy"
    />
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
      url: data.url || data.data?.url || '',
      alt: data.alt || data.data?.alt || '',
      width: data.width || data.data?.width,
      height: data.height || data.data?.height
    }
  } catch {
    return null
  }
})
</script>

<style scoped>
.block-image {
  margin: 12px 0;
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
