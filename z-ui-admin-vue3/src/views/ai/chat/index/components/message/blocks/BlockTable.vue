<template>
  <div class="block-table">
    <div v-if="!parsed" class="block-loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载表格中...</span>
    </div>
    <el-table
      v-else
      :data="parsed.rows"
      border
      stripe
      style="width: 100%; margin: 12px 0"
      max-height="500"
    >
      <el-table-column
        v-for="col in parsed.headers"
        :key="col.key"
        :prop="col.key"
        :label="col.label"
        :min-width="col.width || 120"
        show-overflow-tooltip
      />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { Loading } from '@element-plus/icons-vue'

const props = defineProps<{
  content: string
  complete: boolean
}>()

interface TableColumn {
  key: string
  label: string
  width?: number
}

const parsed = computed(() => {
  if (!props.complete) return null
  try {
    const data = JSON.parse(props.content)
    const rawHeaders: string[] | TableColumn[] = data.headers || []
    const rows: Record<string, any>[] = data.rows || data.data || []

    const headers: TableColumn[] = rawHeaders.map((h) =>
      typeof h === 'string' ? { key: h, label: h } : h
    )

    return { headers, rows }
  } catch {
    return null
  }
})
</script>

<style scoped>
.block-table {
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
