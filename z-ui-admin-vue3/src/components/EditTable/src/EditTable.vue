<script setup lang="ts">
import { Table, TableExpose } from '@/components/Table'
import { TableData } from '@/api/table/types'
import { ref, h, onMounted, reactive, unref, onBeforeMount } from 'vue'
import { ElButton, ElInput } from 'element-plus'
import { TableColumn } from '@/types/table'
import { EditTableColumn } from './types'
import { ContentWrap } from '@/components/ContentWrap'
import { useTable } from '@/hooks/web/useTable'

const props = defineProps({
  cols: Array<EditTableColumn>
})

const tableRef = ref<TableExpose>()

// 重新构建列信息, 根据类型转换为可编辑
onBeforeMount(() => {
  props.cols?.forEach((element) => {
    const colObj = reactive<TableColumn>(element)
    if (element.type === 'Input') {
      colObj.formatter = (_: Recordable, __: TableColumn, cellValue: number) => {
        return h(ElInput, {
          value: cellValue
        })
      }
      columns.push(element)
    }
  })
})
// const columns: TableColumn[] = []
const columns = reactive<TableColumn[]>([
  {
    field: 'index',
    label: '序号',
    type: 'index'
  },
  {
    field: 'name',
    label: '姓名',
    formatter: (_: Recordable, __: TableColumn, cellValue: number) => {
      return h(ElInput, {
        value: cellValue
      })
    }
  }
])

// 增加空行
const addRow = () => {
  tableObject.tableList.push({
    name: 'xx',
    login: 'xx'
  })
}

const tableDataRes = ref<Promise<IResponse<any>>>()

const getTableListApi = (params: any): Promise<IResponse> => {
  console.log(params)
  return tableDataRes.value
}

const { register, tableObject, methods } = useTable<TableData>({
  getListApi: getTableListApi,
  response: {
    list: 'list',
    total: 'total'
  },
  props: {
    columns
  }
})
tableObject.loading = false

const { getList } = methods

// getList()
</script>

<template>
  <!-- 需要封装, 自带  增加, 删除行按钮 -->
  <!-- <Table
        :columns="columns"
        :data="tableDataList"
        :loading="loading"
        :defaultSort="{ prop: 'display_time', order: 'descending' }"
      /> -->
  <ContentWrap>
    <ElButton type="primary" @click="addRow()"> 增加行 </ElButton>
  </ContentWrap>
  <ContentWrap>
    <Table
      ref="tableRef"
      v-model:pageSize="tableObject.pageSize"
      v-model:currentPage="tableObject.currentPage"
      :data="tableObject.tableList"
      :loading="tableObject.loading"
      @register="register"
    />
  </ContentWrap>
</template>
