<script name="UserIndex" setup lang="ts">
import { getServerInfo } from '@/api/framework/monitor/serverinfo'
import { SysFiles, TableData } from '@/api/framework/monitor/serverinfo/types'
import { ContentWrap } from '@/components/ContentWrap'
import { onMounted, reactive } from 'vue'

const cpuInfos = reactive<TableData[]>([])
const memInfos = reactive<TableData[]>([])
const jvmInfos = reactive<TableData[]>([])
const sysInfos = reactive<TableData[]>([])
const diskInfos = reactive<SysFiles[]>([])

onMounted(async () => {
  // 进入页面获取服务器信息,展现
  const serverInfo = await getServerInfo()
  // reactive得用push才会生效,直接赋值界面不展现, 或者使用ref直接赋值
  cpuInfos.push(...serverInfo.cpu)
  memInfos.push(...serverInfo.mem)
  diskInfos.push(...serverInfo.sysFiles)
  sysInfos.push(...serverInfo.sys)
  jvmInfos.push(...serverInfo.jvm)
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
      <ElCol :span="12" class="card-box">
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
      <ElCol :span="12" class="card-box">
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
          <ElTable :data="diskInfos" style="width: 100%">
            <ElTableColumn prop="typeName" label="磁盘" width="180" />
            <ElTableColumn prop="dirName" label="挂载点" width="180" />
            <ElTableColumn prop="sysTypeName" label="文件系统" width="180" />
            <ElTableColumn prop="total" label="总空间" width="180" />
            <ElTableColumn prop="used" label="已使用" width="180" />
            <ElTableColumn prop="free" label="剩余" width="180" />
            <ElTableColumn prop="usage" label="使用率" width="180" />
          </ElTable>
        </ElCard>
      </ElCol>
    </ElRow>
  </ContentWrap>
</template>
