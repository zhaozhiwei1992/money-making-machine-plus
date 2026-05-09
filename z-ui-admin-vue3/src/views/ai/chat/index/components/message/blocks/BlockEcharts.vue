<template>
  <div class="block-echarts" ref="chartRef" :style="{ width: '100%', height: chartHeight }"></div>
</template>

<script setup lang="ts">
import * as echarts from '@/plugins/echarts'

const props = defineProps<{
  content: string
  complete: boolean
}>()

const chartRef = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

const chartHeight = computed(() => {
  try {
    const option = JSON.parse(props.content)
    return option.height || '400px'
  } catch {
    return '400px'
  }
})

const renderChart = () => {
  if (!chartRef.value || !props.complete) return

  try {
    const option = JSON.parse(props.content)
    if (!chartInstance) {
      chartInstance = echarts.init(chartRef.value)
    }
    chartInstance.setOption(option, true)
  } catch {
    // JSON 解析失败，静默处理
  }
}

// 监听 complete 和 content 变化
watch(
  () => [props.content, props.complete],
  () => {
    if (props.complete) {
      nextTick(renderChart)
    }
  },
  { immediate: true }
)

// 响应式调整
const resizeObserver = new ResizeObserver(() => {
  chartInstance?.resize()
})

onMounted(() => {
  if (chartRef.value) {
    resizeObserver.observe(chartRef.value)
  }
})

onBeforeUnmount(() => {
  resizeObserver.disconnect()
  chartInstance?.dispose()
  chartInstance = null
})
</script>

<style scoped>
.block-echarts {
  min-height: 200px;
  margin: 12px 0;
  border-radius: 8px;
  background: var(--el-bg-color);
}
</style>
