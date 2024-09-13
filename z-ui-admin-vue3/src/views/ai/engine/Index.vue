<script name="UserIndex" setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Search } from '@/components/Search'
import { Dialog } from '@/components/Dialog'
import { useI18n } from '@/hooks/web/useI18n'
import { ElButton, ElTag } from 'element-plus'
import { Table } from '@/components/Table'
import { getTableListApi, saveTableApi, delTableListApi } from '@/api/ai/engine'
import { useTable } from '@/hooks/web/useTable'
import { EngineVO } from '@/api/ai/engine/types'
import { ref, unref, reactive, h } from 'vue'
import AddOrUpdate from './components/AddOrUpdate.vue'
import Detail from './components/Detail.vue'
import { CrudSchema, useCrudSchemas } from '@/hooks/web/useCrudSchemas'
import { TableColumn } from '@/types/table'

const { register, tableObject, methods } = useTable<EngineVO>({
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
    label: '引擎名称',
    search: {
      show: true
    },
    form: {
      formItemProps: {
        required: true
      }
    }
  },
  {
    field: 'type',
    label: '引擎类型',
    search: {
      show: true
    },
    form: {
      formItemProps: {
        required: true
      }
    }
  },
  {
    field: 'apiKey',
    label: '认证密钥',
    search: {
      show: false
    }
  },
  {
    field: 'status',
    label: '状态',
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
    }
  },
  {
    field: 'createdDate',
    label: '创建日期',
    form: {
      show: false
    }
  }
])

const { allSchemas } = useCrudSchemas(crudSchemas)

const dialogVisible = ref(false)

const dialogTitle = ref('')

const AddAction = (row: EngineVO | null) => {
  dialogTitle.value = t('button.add')
  tableObject.currentRow = row
  dialogVisible.value = true
  actionType.value = ''
}

const delLoading = ref(false)

const delData = async (row: EngineVO | null, multiple: boolean) => {
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

const actionType = ref('')

const action = (row: EngineVO, type: string) => {
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
      const data = (await write?.getFormData()) as EngineVO
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
      <ElButton type="primary" @click="AddAction" v-hasPermi="['ai:engine:add']">{{
        t('button.add')
      }}</ElButton>
      <ElButton
        :loading="delLoading"
        type="danger"
        @click="delData(null, true)"
        v-hasPermi="['ai:engine:delete']"
      >
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
      row-key="id"
      border
    >
      <template #action="{ row }">
        <ElButton
          v-if="row.createdBy != 'system'"
          type="primary"
          v-hasPermi="['ai:engine:edit']"
          @click="action(row, 'edit')"
        >
          {{ t('exampleDemo.edit') }}
        </ElButton>
        <ElButton type="success" v-hasPermi="['ai:engine:add']" @click="AddAction(row)">
          {{ t('exampleDemo.add') }}
        </ElButton>
        <ElButton
          v-if="row.createdBy != 'system'"
          type="danger"
          v-hasPermi="['ai:engine:delete']"
          @click="delData(row, false)"
        >
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
      <ElButton
        v-if="actionType !== 'detail'"
        type="primary"
        :loading="loading"
        @click="save"
        v-hasPermi="['ai:engine:add']"
      >
        {{ t('exampleDemo.save') }}
      </ElButton>
      <ElButton @click="dialogVisible = false">{{ t('dialogDemo.close') }}</ElButton>
    </template>
  </Dialog>
</template>
