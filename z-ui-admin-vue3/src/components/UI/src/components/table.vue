<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { useI18n } from '@/hooks/web/useI18n'
import { Table, TableExpose } from '@/components/Table'
import { getTableColListByMenuApi, getTableDataListApi } from '@/api/ui/table'
import { TableData } from '@/api/table/types'
import { ref, unref, reactive, inject, onMounted } from 'vue'
import { ElButton } from 'element-plus'
import { useTable } from '@/hooks/web/useTable'
import { Pagination, TableColumn, TableResponse, TableSlotDefault } from '@/types/table'
import { useEmitt } from '@/hooks/web/useEmitt'
import { TableVO } from '@/api/ui/table/types'

const props = defineProps({
  title: String,
  componentId: String
})

const { emitter } = useEmitt()

// 对外暴露一些方法, 通过事件 开始
// 重新加载数据, 查询区点击按钮,或者变更页签等触发
emitter.on('tableLoadData', ({ queryObj, componentId }) => {
  console.log(queryObj, '数据查询对象')
  // 填充数据查询参数 queryObj
  // tableObject.params.push()
  // todo 多个列表如何区分??
  // getList()
  console.log(queryObj.componentId, '触发来源组件')
  setSearchParams(queryObj.data)

  console.log(componentId, '触发来源组件')
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

const { t } = useI18n()

const columns = reactive<TableColumn[]>([])

onMounted(async () => {
  // 父页面传入菜单id, 这里根据菜单id自己去后台获取编辑区信息
  const menuId: string | undefined = inject('menuId')
  console.log('父级传入menuid为: ' + menuId)
  // 获取按钮信息, 填充
  const res = await getTableColListByMenuApi(menuId)
  res.forEach((element) => {
    const colItem: any = {
      field: element.code,
      label: element.name,
      type: element.type
    }
    columns.push(colItem)
  })
  // 模拟测试
  // buttons.value.push(...buttonsSchema)
})

const getTableList = async (): Promise<TableResponse<T>> => {
  // 调用事件, 需要业务去实现获取数据方法
  emitter.emit('tableLoadData', {
    componentId: props.componentId
  })
  emitter.on('getTableDataListEnd', (data) => {
    return data
  })
}

const { register, tableObject, methods } = useTable<TableVO>({
  getListApi: getTableList,
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

const tableRefs = reactive({})
tableRefs[props.componentId] = ref(null)

// 选中数据
// const index = ref(1)
</script>

<template>
  <ContentWrap :title="props.title">
    <Table
      ref="tableRefs[props.componentId]"
      v-model:pageSize="tableObject.pageSize"
      v-model:currentPage="tableObject.currentPage"
      :data="tableObject.tableList"
      :loading="tableObject.loading"
      :pagination="paginationObj"
      @register="register"
    >
      <template #action="data">
        <ElButton type="primary" @click="actionFn(data)">
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
