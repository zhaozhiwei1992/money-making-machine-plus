<template>
  <ContentWrap>
    <IFrame :src="src" height="700" width="100%" />
  </ContentWrap>
</template>
<script setup lang="ts" name="JimuViewReport">
import { ref } from 'vue'
import { getAccessToken } from '@/utils/auth'
import { useRouter } from 'vue-router'

const BASE_URL = import.meta.env.VITE_SERVER_URL
// 查看页面, 报表菜单下点击对应的报表跳转使用
// 获取路由path中的报表id, 填充
const { currentRoute } = useRouter()
const fullPath = currentRoute.value.fullPath
const reportId = fullPath.substring(fullPath.lastIndexOf('/') + 1)
console.log('当前访问报表id: ', reportId)
const src = ref(BASE_URL + '/jmreport/view/' + reportId + '?token=' + getAccessToken())
</script>
