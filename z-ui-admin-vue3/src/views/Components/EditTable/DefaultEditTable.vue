<!-- 测试可编辑列表 -->
<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { EditTable } from '@/components/EditTable'
import { TableColumn } from '@/types/table'

// 获取可编辑列表中数据

const columns: TableColumn[] = [
  {
    field: 'index',
    label: '序号',
    type: 'index'
  },
  {
    field: 'name',
    label: '姓名',
    type: 'Input'
  },
  {
    field: 'login',
    label: '登录名',
    type: 'Input'
  }
]

const title = '演示demo-可编辑列表'
</script>

<template>
  <ContentWrap :title="title">
    <EditTable :columns="columns" />
  </ContentWrap>
</template>
