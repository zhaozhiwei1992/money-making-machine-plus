<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { useI18n } from '@/hooks/web/useI18n'
import { Table, TableExpose } from '@/components/Table'
import { getTableListApi } from '@/api/table'
import { TableData } from '@/api/table/types'
import { ref, unref, h, reactive } from 'vue'
import { ElTag, ElButton } from 'element-plus'
import { useTable } from '@/hooks/web/useTable'
import { Pagination, TableColumn, TableSlotDefault } from '@/types/table'
import { useEmitt } from '@/hooks/web/useEmitt'

// 对外暴露一些方法, 通过事件 开始
// 重新加载数据
useEmitt({
  name: 'tableLoadData',
  callback: (tableObj: any) => {
    console.log(tableObj, '数据查询对象')
    // 填充数据查询参数
    // tableObject.params.push()
    // todo 多个列表如何区分??
    getList()
  }
})

// 获取选中数据
useEmitt({
  name: 'getSelectedData',
  callback: (tableObj: any) => {
    console.log(tableObj, '数据查询对象')
    // 获取指定列表选中数据
  }
})
// 对外暴露一些方法, 通过事件 结束

const { t } = useI18n()

const columns = reactive<TableColumn[]>([
  {
    field: 'index',
    label: t('tableDemo.index'),
    type: 'index'
  },
  {
    field: 'content',
    label: t('tableDemo.header'),
    children: [
      {
        field: 'title',
        label: t('tableDemo.title')
      },
      {
        field: 'author',
        label: t('tableDemo.author')
      },
      {
        field: 'display_time',
        label: t('tableDemo.displayTime')
      },
      {
        field: 'importance',
        label: t('tableDemo.importance'),
        formatter: (_: Recordable, __: TableColumn, cellValue: number) => {
          return h(
            ElTag,
            {
              type: cellValue === 1 ? 'success' : cellValue === 2 ? 'warning' : 'danger'
            },
            () =>
              cellValue === 1
                ? t('tableDemo.important')
                : cellValue === 2
                ? t('tableDemo.good')
                : t('tableDemo.commonly')
          )
        }
      },
      {
        field: 'pageviews',
        label: t('tableDemo.pageviews')
      }
    ]
  },
  {
    field: 'action',
    label: t('tableDemo.action')
  }
])

const { emitter } = useEmitt()

// const getTableList = (params: any): Promise<IResponse> => {
//   // 通过事件由业务实现返回数据, 怎么拿到值呢?
//   // 1. 通过store中转, 2. 直接通过事件把数据发回来, 然后监听填充, 3. 动态传入列表取数url(感觉靠谱点)
//   emitter.emit('getTableListApi', params)
//   // return request.get({ url: '/example/list', params })
// }

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

const { getList } = methods

getList()

const tableRef = ref<TableExpose>()

const actionFn = (data: TableSlotDefault) => {
  console.log(data)
}

const paginationObj = ref<Pagination>()
// 显示分页, 条数根据数据控制
paginationObj.value = {
  total: 0
}

// 显示多选
unref(tableRef)?.setProps({
  selection: true
})

// 选中数据
// const index = ref(1)

const title = ref('列表1')
</script>

<template>
  <ContentWrap :title="title">
    <Table
      ref="tableRef"
      v-model:pageSize="tableObject.pageSize"
      v-model:currentPage="tableObject.currentPage"
      :data="tableObject.tableList"
      :loading="tableObject.loading"
      :pagination="paginationObj"
      @register="register"
    >
      <template #action="data">
        <ElButton type="primary" @click="actionFn(data as TableSlotDefault)">
          {{ t('tableDemo.action') }}
        </ElButton>
      </template>

      <template #expand="data">
        <div class="ml-30px">
          <div>{{ t('tableDemo.title') }}：{{ data.row.title }}</div>
          <div>{{ t('tableDemo.author') }}：{{ data.row.author }}</div>
          <div>{{ t('tableDemo.displayTime') }}：{{ data.row.display_time }}</div>
        </div>
      </template>
    </Table>
  </ContentWrap>
</template>
