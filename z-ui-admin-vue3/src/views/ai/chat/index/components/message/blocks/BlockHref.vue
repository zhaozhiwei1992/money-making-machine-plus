<template>
  <div class="block-href">
    <div v-if="!parsed" class="block-loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载链接中...</span>
    </div>
    <div v-else class="href-card" @click="openLink">
      <div class="href-title">
        <el-icon><Link /></el-icon>
        <span>{{ parsed.title || parsed.url }}</span>
      </div>
      <div v-if="parsed.description" class="href-desc">{{ parsed.description }}</div>
      <div class="href-url">{{ parsed.url }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Link, Loading } from '@element-plus/icons-vue'

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
      title: data.title || data.alt || data.data?.title || data.data?.alt || '',
      description: data.description || data.data?.description || ''
    }
  } catch {
    return null
  }
})

const openLink = () => {
  if (parsed.value?.url) {
    window.open(parsed.value.url, '_blank')
  }
}
</script>

<style scoped>
.block-href {
  margin: 12px 0;
}
.href-card {
  padding: 12px 16px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}
.href-card:hover {
  background: var(--el-fill-color-light);
}
.href-title {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--el-color-primary);
  font-weight: 500;
  font-size: 14px;
}
.href-desc {
  margin-top: 6px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
  line-height: 1.5;
}
.href-url {
  margin-top: 6px;
  font-size: 12px;
  color: var(--el-text-color-placeholder);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
