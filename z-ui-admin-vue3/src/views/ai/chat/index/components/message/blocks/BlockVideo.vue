<template>
  <div class="block-video">
    <div v-if="!parsed" class="block-loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载视频中...</span>
    </div>
    <video
      v-else
      controls
      :style="{ maxWidth: '100%', borderRadius: '8px' }"
      :poster="parsed.poster"
    >
      <source :src="parsed.url" type="video/mp4" />
    </video>
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
      poster: data.poster || data.data?.poster || '',
      time: data.time || data.data?.times || 0
    }
  } catch {
    return null
  }
})
</script>

<style scoped>
.block-video {
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
