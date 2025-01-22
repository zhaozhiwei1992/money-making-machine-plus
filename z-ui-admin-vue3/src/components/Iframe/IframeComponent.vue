<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'

const props = defineProps({
  src: {
    type: String,
    required: true
  },
  fullscreen: {
    type: Boolean,
    default: false
  }
})

const iframeRef = ref<HTMLIFrameElement | null>(null)

const iframeStyle = computed(() => ({
  width: props.fullscreen ? '100vw' : '600px',
  height: props.fullscreen ? '100vh' : '400px',
  border: 'none'
}))

const handleIframeLoad = () => {
  console.log('Iframe loaded')
}

onMounted(() => {
  if (iframeRef.value) {
    iframeRef.value.addEventListener('load', handleIframeLoad)
  }
})
</script>

<template>
  <div class="iframe-container">
    <iframe ref="iframeRef" :src="src" :style="iframeStyle" @load="handleIframeLoad"></iframe>
  </div>
</template>

<style scoped>
.iframe-container {
  position: relative;
  overflow: hidden;
}
</style>
