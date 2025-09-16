<template>
  <ContentWrap>
    <IFrame :src="src" width="100%" />
  </ContentWrap>
</template>
<script setup lang="ts" name="SwaggerIndex">
import { getAccessToken } from '@/utils/auth'
import { ref } from 'vue'

const BASE_URL = import.meta.env.VITE_SERVER_URL
const src = ref(BASE_URL + '/doc.html?token=' + getAccessToken())
</script>
<style scoped lang="scss">
iframe {
  border: none;
  height: calc(100vh - 80px - 40px);
}
</style>
