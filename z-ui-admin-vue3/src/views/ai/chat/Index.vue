<script setup lang="ts">
import { propTypes } from '@/utils/propTypes'
import { ChatRound } from '@element-plus/icons-vue'
import { ref } from 'vue'

defineProps({
  color: propTypes.string.def('')
})

const showAI = () => {
  show.value = !show.value
}

const show = ref(false)
</script>

<template>
  <div @click="showAI()">
    <el-icon><ChatRound /></el-icon>
  </div>
  <!-- 默认显示到右下角 -->
  <el-dialog v-model="show" title="AI助手" width="20%" draggable class="right-bottom-dialog">
    <h1>hello </h1>
  </el-dialog>
</template>

<style scoped>
.right-bottom-dialog {
  position: fixed;
  bottom: 20px;
  right: 20px;
}
</style>
