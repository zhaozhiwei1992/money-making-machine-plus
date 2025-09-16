<template>
  <ContentWrap>
    <IFrame :src="src" height="700" width="100%" />
  </ContentWrap>
</template>
<script setup lang="ts" name="GoView">
import { ContentWrap } from '@/components/ContentWrap'
import { getAccessToken } from '@/utils/auth'

const tokenId = getAccessToken()
console.log(tokenId)

const src = 'http://127.0.0.1:3000'
</script>
