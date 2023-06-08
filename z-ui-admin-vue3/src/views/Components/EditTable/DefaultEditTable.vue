<!-- 测试可编辑列表 -->
<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { EditTable } from '@/components/EditTable'
import { EditTableColumn } from '@/components/EditTable/src/types'
import { ref } from 'vue'
import { ElButton } from 'element-plus'

// 获取可编辑列表中数据
const columns: EditTableColumn[] = [
  {
    code: 'name',
    name: '姓名',
    type: 'Input',
    required: false,
    isEdit: true,
    source: '',
    width: 200,
    mapping: []
  },
  {
    code: 'login',
    name: '登录名',
    type: 'Input',
    required: false,
    isEdit: true,
    source: '',
    width: 200,
    mapping: []
  }
]

// 引用到字组件
const editDataTable = ref()

const addRow = () => {
  editDataTable.value.addRow({})
  // editDataTable.value.tableData.push({})
}

const getList = () => {
  const data = editDataTable.value.getList()
  console.log('列表数据', data)
}

const title = '演示demo-可编辑列表'
</script>

<template>
  <ContentWrap>
    <ElButton @click="addRow()">增加行</ElButton>
    <ElButton @click="getList()">获取数据</ElButton>
  </ContentWrap>
  <ContentWrap :title="title">
    <EditTable :cols="columns" ref="editDataTable" />
  </ContentWrap>
</template>
