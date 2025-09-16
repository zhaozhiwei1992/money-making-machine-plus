<script setup lang="ts">
import IframeComponent from '@/components/Iframe/IframeComponent.vue'
import { propTypes } from '@/utils/propTypes'
import { ChatRound } from '@element-plus/icons-vue'
import { ref } from 'vue'

defineProps({
  color: propTypes.string.def('')
})

const handleChange = () => {
  // 弹出机器人
  console.log('handleChange 弹出机器人')
  show.value = !show.value
}

const show = ref(false)

const iframeSrc = ref('http://localhost/chatbot/KvV4PIKmpaqPPEV7')
const isFullscreen = ref(false)
</script>

<template>
  <div @click="handleChange()">
    <el-icon><ChatRound /></el-icon>
  </div>
  <el-dialog v-model="show" title="AI助手" width="650">
    <IframeComponent :src="iframeSrc" :fullscreen="isFullscreen" />
  </el-dialog>
</template>
