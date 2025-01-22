<script setup lang="ts">
import { propTypes } from '@/utils/propTypes'
import { ChatRound } from '@element-plus/icons-vue'
import { ElIcon, ElDialog } from 'element-plus'
import { ref } from 'vue'
import IframeComponent from '@/components/Iframe/IframeComponent.vue'

defineProps({
  color: propTypes.string.def('')
})

const handleChange = () => {
  // 弹出机器人
  console.log('handleChange 弹出机器人')
  show.value = !show.value
}

const show = ref(false)

const iframeSrc = ref('http://localhost/chatbot/e06rvPIo3wSZLQ4A')
const isFullscreen = ref(false)
</script>

<template>
  <div @click="handleChange()">
    <el-icon><ChatRound /></el-icon>
  </div>
  <el-dialog v-model="show" title="财政部规范专家" width="650">
    <IframeComponent :src="iframeSrc" :fullscreen="isFullscreen" />
  </el-dialog>
</template>
