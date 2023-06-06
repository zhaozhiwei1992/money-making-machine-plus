<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { useI18n } from '@/hooks/web/useI18n'
import { Table, TableExpose } from '@/components/Table'
import { getTableConfigListApi } from '@/api/ui/table'
import { TableData } from '@/api/table/types'
import { ref, unref, h, reactive, inject, onMounted } from 'vue'
import { ElTag, ElButton } from 'element-plus'
import { useTable } from '@/hooks/web/useTable'
import { Pagination, TableColumn, TableSlotDefault } from '@/types/table'
import { useEmitt } from '@/hooks/web/useEmitt'

const props = defineProps({
  title: String,
  componentId: String,
  comRef: ref<any>
})

// 对外暴露一些方法, 通过事件 开始
// 重新加载数据, 查询区点击按钮,或者变更页签等触发
useEmitt({
  name: 'tableLoadData',
  callback: (queryObj: any) => {
    console.log(queryObj, '数据查询对象')
    // 填充数据查询参数 queryObj
    // tableObject.params.push()
    // todo 多个列表如何区分??
    // getList()
    console.log(queryObj.componentId, '触发来源组件')
    setSearchParams(queryObj.data)
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

const tableDataRes = ref<Promise<IResponse<any>>>()
useEmitt({
  name: 'getTableDataListEnd',
  callback: (res: any) => {
    tableDataRes.value = res
  }
})

// 对外暴露一些方法, 通过事件 结束

const menuid: string | undefined = inject('menuid')

const { t } = useI18n()

const columns = reactive<TableColumn[]>([])
// const columns = reactive<TableColumn[]>([
//   {
//     field: 'index',
//     label: t('tableDemo.index'),
//     type: 'index'
//   },
//   {
//     field: 'content',
//     label: t('tableDemo.header'),
//     children: [
//       {
//         field: 'title',
//         label: t('tableDemo.title')
//       },
//       {
//         field: 'author',
//         label: t('tableDemo.author')
//       },
//       {
//         field: 'display_time',
//         label: t('tableDemo.displayTime')
//       },
//       {
//         field: 'importance',
//         label: t('tableDemo.importance'),
//         formatter: (_: Recordable, __: TableColumn, cellValue: number) => {
//           return h(
//             ElTag,
//             {
//               type: cellValue === 1 ? 'success' : cellValue === 2 ? 'warning' : 'danger'
//             },
//             () =>
//               cellValue === 1
//                 ? t('tableDemo.important')
//                 : cellValue === 2
//                 ? t('tableDemo.good')
//                 : t('tableDemo.commonly')
//           )
//         }
//       },
//       {
//         field: 'pageviews',
//         label: t('tableDemo.pageviews')
//       }
//     ]
//   },
//   {
//     field: 'action',
//     label: t('tableDemo.action')
//   }
// ])

onMounted(() => {
  // 父页面传入菜单id, 这里根据菜单id自己去后台获取编辑区信息
  console.log('父级传入menuid为: ' + menuid)
  // 获取按钮信息, 填充
  getTableConfigListApi(menuid).then((res) => {
    columns.push(...res.data)
  })
  // 模拟测试
  // buttons.value.push(...buttonsSchema)
})

const { emitter } = useEmitt()

const getTableListApi = (params: any): Promise<IResponse> => {
  // 通过事件由业务实现返回数据, 怎么拿到值呢?
  // 1. 通过store中转, 2. 直接通过事件把数据发回来, 然后监听填充(事件不能返回)
  emitter.emit('getTableDataList', params)
  // 这里有没有可能出现数据还没查到情况
  console.log(tableDataRes.value, '返回列表数据')
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

const { getList, setSearchParams } = methods

getList()

const tableRef = ref<TableExpose>()

const actionFn = (data: TableSlotDefault) => {
  console.log(data)
}

const paginationObj = ref<Pagination>()
// 显示分页, 条数根据数据控制
paginationObj.value = {
  total: tableObject.total
}

// 显示多选
unref(tableRef)?.setProps({
  selection: true
})

// 选中数据
// const index = ref(1)
</script>

<template>
  <ContentWrap :title="props.title">
    <Table
      ref="comRef"
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
