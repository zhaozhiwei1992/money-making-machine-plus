<template>
  <div class="content-block-renderer">
    <template v-for="(block, index) in blocks" :key="index">
      <!-- markdown 块：复用现有 MarkdownView -->
      <MarkdownView v-if="block.type === 'markdown'" :content="block.content" />
      <!-- 特殊块：动态组件渲染 -->
      <component
        v-else
        :is="getBlockComponent(block.type)"
        :content="block.content"
        :complete="block.complete"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import MarkdownView from '@/components/MarkdownView/index.vue'
import { parseContent } from './blocks/parser'
import { getBlockComponent, getRegisteredTypes } from './blocks/registry'

const props = defineProps<{
  content: string
}>()

const blocks = computed(() => {
  return parseContent(props.content || '', getRegisteredTypes())
})
</script>
