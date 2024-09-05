<script name="UserIndex" setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import {
  ElRow,
  ElCard,
  ElCol,
  ElTable,
  ElTableColumn,
  ElForm,
  ElFormItem,
  ElInput
} from 'element-plus'
import {
  getCacheContentInfo,
  getCacheInfo,
  getCacheKeysInfo
} from '@/api/framework/monitor/cacheinfo'
import { CacheContentVO, CacheKeyVO, CacheVO } from '@/api/framework/monitor/cacheinfo/types'
import { reactive, onMounted, ref } from 'vue'

const cacheInfos = reactive<CacheVO[]>([])

const cacheContentInfo = reactive<CacheContentVO>({
  content: '',
  key: '',
  name: ''
})

const currentCache = ref()

const cacheKeysInfo = reactive<CacheKeyVO[]>([])

const getCacheKeys = async (row: any, column: any, event: Event) => {
  currentCache.value = row.name
  cacheKeysInfo.length = 0
  const res = await getCacheKeysInfo(row.name)
  cacheKeysInfo.push(...res)
}

const getCacheContent = async (row: any, column: any, event: Event) => {
  const res = await getCacheContentInfo(currentCache.value, row.name)
  cacheContentInfo.content = res.content
  cacheContentInfo.key = res.key
  cacheContentInfo.name = res.name
}

onMounted(async () => {
  // 获取缓存信息列表
  const cacheInfo = await getCacheInfo()
  // reactive得用push才会生效,直接赋值界面不展现, 或者使用ref直接赋值
  cacheInfos.push(...cacheInfo)
})
</script>

<template>
  <ContentWrap>
    <ElRow>
      <ElCol :span="6" class="card-box">
        <ElCard>
          <template #header>
            <div class="card-header">
              <span>缓存列表</span>
            </div>
          </template>
          <ElTable :data="cacheInfos" style="width: 100%" @row-click="getCacheKeys">
            <ElTableColumn prop="name" label="缓存名称" width="180" />
            <ElTableColumn prop="remark" label="备注" width="180" />
          </ElTable>
        </ElCard>
      </ElCol>
      <ElCol :span="6" class="card-box">
        <ElCard>
          <template #header>
            <div class="card-header">
              <span>缓存键名</span>
            </div>
          </template>
          <ElTable :data="cacheKeysInfo" style="width: 100%" @row-click="getCacheContent">
            <ElTableColumn prop="name" label="键名名称" width="180" />
          </ElTable>
        </ElCard>
      </ElCol>
      <ElCol :span="12" class="card-box">
        <ElCard>
          <template #header>
            <div class="card-header">
              <span>缓存内容</span>
            </div>
          </template>
          <ElForm :model="cacheContentInfo" label-width="auto" style="max-width: 600px">
            <ElFormItem label="缓存名称">
              <ElInput v-model="cacheContentInfo.name" />
            </ElFormItem>
            <ElFormItem label="缓存键">
              <ElInput v-model="cacheContentInfo.key" />
            </ElFormItem>
            <ElFormItem label="缓存内容">
              <ElInput v-model="cacheContentInfo.content" type="textarea" />
            </ElFormItem>
          </ElForm>
        </ElCard>
      </ElCol>
    </ElRow>
  </ContentWrap>
</template>
