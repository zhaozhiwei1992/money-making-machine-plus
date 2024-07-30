<script name="UserIndex" setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Search } from '@/components/Search'
import { Dialog } from '@/components/Dialog'
import { useI18n } from '@/hooks/web/useI18n'
import { Table } from '@/components/Table'
import { getServerInfo } from '@/api/framework/monitor/serverinfo'
import { useTable } from '@/hooks/web/useTable'
import { TableData, ServerInfo } from '@/api/framework/monitor/serverinfo/types'
import { ref, unref, reactive, h, onMounted } from 'vue'
import { TableColumn } from '@/types/table'

let cpuInfos = reactive<TableData[]>([])
let memInfos = reactive<TableData[]>([])
let jvmInfos = reactive<TableData[]>([])
let sysInfos = reactive<TableData[]>([])
let diskInfos = reactive<TableData[]>([])

onMounted(async () => {
  // 进入页面获取服务器信息,展现
  const serverInfo = await getServerInfo()
  cpuInfos = serverInfo.data.cpu
  memInfos = serverInfo.data.memory
  diskInfos = serverInfo.data.disk
  sysInfos = serverInfo.data.sys
  jvmInfos = serverInfo.data.jvm
})
</script>

<template>
  <ContentWrap>
    <ElRow>
      <ElCol :span="12" class="card-box">
        <ElCard>
          <template #header>
            <div class="card-header">
              <span>CPU</span>
            </div>
          </template>
          <ElTable :data="cpuInfos" style="width: 100%">
            <ElTableColumn prop="type" label="属性" width="180" />
            <ElTableColumn prop="value" label="值" width="180" />
          </ElTable>
        </ElCard>
      </ElCol>
      <ElCol :span="12" class="card-box">
        <ElCard>
          <template #header>
            <div class="card-header">
              <span>内存</span>
            </div>
          </template>
          <ElTable :data="memInfos" style="width: 100%">
            <ElTableColumn prop="type" label="属性" width="180" />
            <ElTableColumn prop="value" label="值" width="180" />
          </ElTable>
        </ElCard>
      </ElCol>
      <ElCol :span="24" class="card-box">
        <ElCard>
          <template #header>
            <div class="card-header">
              <span>服务器信息</span>
            </div>
          </template>
          <ElTable :data="sysInfos" style="width: 100%">
            <ElTableColumn prop="type" label="属性" width="180" />
            <ElTableColumn prop="value" label="值" width="180" />
          </ElTable>
        </ElCard>
      </ElCol>
      <ElCol :span="24" class="card-box">
        <ElCard>
          <template #header>
            <div class="card-header">
              <span>JAVA虚拟机信息</span>
            </div>
          </template>
          <ElTable :data="jvmInfos" style="width: 100%">
            <ElTableColumn prop="type" label="属性" width="180" />
            <ElTableColumn prop="value" label="值" width="180" />
          </ElTable>
        </ElCard>
      </ElCol>
      <ElCol :span="24" class="card-box">
        <ElCard>
          <template #header>
            <div class="card-header">
              <span>磁盘状态信息</span>
            </div>
          </template>
          <ElTable :data="diskinfos" style="width: 100%">
            <ElTableColumn prop="type" label="属性" width="180" />
            <ElTableColumn prop="value" label="值" width="180" />
          </ElTable>
        </ElCard>
      </ElCol>
    </ElRow>
  </ContentWrap>
</template>
