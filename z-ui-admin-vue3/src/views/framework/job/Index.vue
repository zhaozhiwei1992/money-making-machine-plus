<script name="MenuIndex" setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Search } from '@/components/Search'
import { Dialog } from '@/components/Dialog'
import { useI18n } from '@/hooks/web/useI18n'
import { ElButton, ElMessage, ElTag } from 'element-plus'
import { Table } from '@/components/Table'
import {
  getTableListApi,
  saveTableApi,
  delTableListApi,
  startApi,
  stopApi,
  getJobSelect
} from '@/api/system/task'
import { useTable } from '@/hooks/web/useTable'
import { TableData } from '@/api/system/task/types'
import { ref, unref, reactive, h, onMounted } from 'vue'
import AddOrUpdate from './components/AddOrUpdate.vue'
import Detail from './components/Detail.vue'
import { CrudSchema, useCrudSchemas } from '@/hooks/web/useCrudSchemas'
import { ComponentOptions } from '@/types/components'

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

const jobOptions = ref<ComponentOptions[] | any>([])

onMounted(async () => {
  jobOptions.value = await getJobSelect()
})

const crudSchemas = reactive<CrudSchema[]>([
  {
    field: 'id',
    label: '编号',
    type: 'index',
    form: {
      show: false
    },
    detail: {
      show: false
    }
  },
  {
    field: 'name',
    label: '任务名称',
    search: {
      show: true
    }
  },
  {
    field: 'startClass',
    label: '任务入口',
    form: {
      component: 'Select',
      componentProps: {
        style: {
          width: '100%'
        },
        options: jobOptions
      }
    }
  },
  {
    field: 'cronExpression',
    label: '表达式'
  },
  {
    field: 'enable',
    label: '是否启用',
    formatter: (_: Recordable, __: TableColumn, cellValue: boolean) => {
      return h(
        ElTag,
        {
          type: 'success'
        },
        () => (cellValue === true ? '启用' : '停用')
      )
    },
    form: {
      component: 'Switch',
      componentProps: {
        style: {
          width: '100%'
        }
      }
    },
    detail: {
      span: 24
    }
  },
  {
    field: 'action',
    width: '260px',
    label: t('tableDemo.action'),
    form: {
      show: false
    },
    detail: {
      show: false
    }
  }
])

const { allSchemas } = useCrudSchemas(crudSchemas)

const dialogVisible = ref(false)

const dialogTitle = ref('')

const AddAction = () => {
  dialogTitle.value = t('exampleDemo.add')
  tableObject.currentRow = null
  dialogVisible.value = true
  actionType.value = ''
}

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

const start = async () => {
  const { getSelections } = methods
  const selections = await getSelections()
  const ids = selections.map((v) => v.id)
  const res = await startApi(ids)
  if ('success' === res) {
    // 保存成功
    ElMessage.info('已启用')
    getList()
  }
}

const stop = async () => {
  const { getSelections } = methods
  const selections = await getSelections()
  const ids = selections.map((v) => v.id)
  const res = await stopApi(ids)
  if ('success' === res) {
    // 保存成功
    ElMessage.info('已停用')
    getList()
  }
}

const actionType = ref('')

const action = (row: TableData, type: string) => {
  dialogTitle.value = t(type === 'edit' ? 'exampleDemo.edit' : 'exampleDemo.detail')
  actionType.value = type
  tableObject.currentRow = row
  dialogVisible.value = true
}

const writeRef = ref<ComponentRef<typeof AddOrUpdate>>()

const loading = ref(false)

const save = async () => {
  const write = unref(writeRef)
  await write?.elFormRef?.validate(async (isValid) => {
    if (isValid) {
      loading.value = true
      const data = (await write?.getFormData()) as TableData
      const res = await saveTableApi(data)
        .catch(() => {})
        .finally(() => {
          loading.value = false
        })
      if (res) {
        dialogVisible.value = false
        tableObject.currentPage = 1
        getList()
      }
    }
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
      <ElButton type="primary" @click="AddAction">{{ t('button.add') }}</ElButton>
      <ElButton :loading="delLoading" type="danger" @click="delData(null, true)">
        {{ t('button.del') }}
      </ElButton>
      <ElButton type="primary" @click="start()">{{ t('button.start') }}</ElButton>
      <ElButton type="primary" @click="stop()">{{ t('button.stop') }}</ElButton>
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
    >
      <template #action="{ row }">
        <ElButton type="primary" v-hasPermi="['example:dialog:edit']" @click="action(row, 'edit')">
          {{ t('exampleDemo.edit') }}
        </ElButton>
        <ElButton
          type="success"
          v-hasPermi="['example:dialog:view']"
          @click="action(row, 'detail')"
        >
          {{ t('exampleDemo.detail') }}
        </ElButton>
        <ElButton type="danger" v-hasPermi="['example:dialog:delete']" @click="delData(row, false)">
          {{ t('exampleDemo.del') }}
        </ElButton>
      </template>
    </Table>
  </ContentWrap>

  <Dialog v-model="dialogVisible" :title="dialogTitle">
    <AddOrUpdate
      v-if="actionType !== 'detail'"
      ref="writeRef"
      :form-schema="allSchemas.formSchema"
      :current-row="tableObject.currentRow"
    />

    <Detail
      v-if="actionType === 'detail'"
      :detail-schema="allSchemas.detailSchema"
      :current-row="tableObject.currentRow"
    />

    <template #footer>
      <ElButton v-if="actionType !== 'detail'" type="primary" :loading="loading" @click="save">
        {{ t('exampleDemo.save') }}
      </ElButton>
      <ElButton @click="dialogVisible = false">{{ t('dialogDemo.close') }}</ElButton>
    </template>
  </Dialog>
</template>
