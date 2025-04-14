<script name="MenuIndex" setup lang="ts">
// import { delTableListApi, getTableListApi } from '@/api/system/login-log'
import { delTableListApi, getTableListApi } from '@/api/system/request-log'
import { TableData } from '@/api/table/types'
import { ContentWrap } from '@/components/ContentWrap'
import { Search } from '@/components/Search'
import { Table } from '@/components/Table'
import { CrudSchema, useCrudSchemas } from '@/hooks/web/useCrudSchemas'
import { useI18n } from '@/hooks/web/useI18n'
import { useTable } from '@/hooks/web/useTable'
import { ElButton } from 'element-plus'
import { reactive, ref } from 'vue'

const { register, tableObject, methods } = useTable<TableData>({
  getListApi: getTableListApi,
  delListApi: delTableListApi,
  response: {
    list: 'list',
    total: 'total'
  },
  defaultParams: {
    title: 's'
  }
})

const { getList, setSearchParams } = methods

getList()

const { t } = useI18n()

const crudSchemas = reactive<CrudSchema[]>([
  {
    field: 'id',
    label: '编号',
    type: 'index'
  },
  {
    field: 'loginName',
    label: '用户名',
    search: {
      show: true
    }
  },
  {
    field: 'requestName',
    label: '请求描述',
    search: {
      show: true
    }
  },
  {
    field: 'createdDate',
    label: '请求时间',
    search: {
      show: true
    }
  },
  {
    field: 'requestURI',
    label: '请求地址'
  },
  {
    field: 'requestMethod',
    label: '请求方式'
  },
  {
    field: 'clientIp',
    label: '客户端ip'
  },
  {
    field: 'params',
    label: '请求参数信息'
  },
  {
    field: 'success',
    label: '请求成功'
  },
  {
    field: 'traceId',
    label: '请求id'
  }
])

const { allSchemas } = useCrudSchemas(crudSchemas)

const delLoading = ref(false)

const delData = async (row: TableData | null, multiple: boolean) => {
  tableObject.currentRow = row
  const { delList, getSelections } = methods
  const selections = await getSelections()
  delLoading.value = true
  await delList(
    multiple ? selections.map((v) => v.id) : [tableObject.currentRow?.id as string],
    multiple
  ).finally(() => {
    delLoading.value = false
  })
}
</script>

<template>
  <ContentWrap>
    <Search
      :model="{ title: 's' }"
      :schema="allSchemas.searchSchema"
      @search="setSearchParams"
      @reset="setSearchParams"
    />

    <div class="mb-10px">
      <ElButton :loading="delLoading" type="danger" @click="delData(null, true)">
        {{ t('exampleDemo.del') }}
      </ElButton>
    </div>

    <Table
      v-model:pageSize="tableObject.pageSize"
      v-model:currentPage="tableObject.currentPage"
      :columns="allSchemas.tableColumns"
      :data="tableObject.tableList"
      :loading="tableObject.loading"
      :pagination="{
        total: tableObject.total
      }"
      @register="register"
    />
  </ContentWrap>
</template>
