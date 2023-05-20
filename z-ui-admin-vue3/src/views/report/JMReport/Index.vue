<template>
  <ContentWrap>
    <IFrame :src="src" height="700" width="100%" />
  </ContentWrap>
</template>
<script setup lang="ts" name="JimuListReport">
import { ref } from 'vue'
import { getAccessToken } from '@/utils/auth'

const BASE_URL = import.meta.env.VITE_SERVER_URL
const src = ref(BASE_URL + '/jmreport/list?token=' + getAccessToken())
</script>
