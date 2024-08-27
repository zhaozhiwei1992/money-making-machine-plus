<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { getTableColListByMenuApi, getTableDataListApi } from '@/api/ui/table'
import { ref, unref, reactive, inject, onMounted } from 'vue'
import { EditTable } from '@/components/EditTable'
import { EditTableColumn } from '@/components/EditTable/src/types'

const props = defineProps({
  title: String,
  componentId: String
})

const editDataTable = ref()

// 列信息
const columns = reactive<EditTableColumn[]>([])

console.log('可编辑列表, 列信息', columns)

onMounted(async () => {
  // 父页面传入菜单id, 这里根据菜单id自己去后台获取编辑区信息
  const menuId: string | undefined = inject('menuId')
  console.log('父级传入menuid为x: ' + menuId)
  // 获取列表信息, 填充
  const res = getTableColListByMenuApi(menuId)
  res.forEach((element) => {
    const colItem: any = {
      ...element,
      field: element.code,
      label: element.name,
      type: element.type
    }
    columns.push(colItem)
  })
  console.log('可编辑列表, 列信息', columns)
})
</script>

<template>
  <ContentWrap :title="title">
    <EditTable :cols="columns" ref="editDataTable" />
  </ContentWrap>
</template>
