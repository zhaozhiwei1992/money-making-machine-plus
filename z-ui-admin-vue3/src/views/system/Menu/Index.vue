<script name="MenuIndex" setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Search } from '@/components/Search'
import { Dialog } from '@/components/Dialog'
import { useI18n } from '@/hooks/web/useI18n'
import { ElButton, ElTag } from 'element-plus'
import { Table } from '@/components/Table'
import { getTableListApi, saveTableApi, delTableListApi } from '@/api/system/menu'
import { useTable } from '@/hooks/web/useTable'
import { MenuVO as TableData } from '@/api/system/menu/types'
import { ref, unref, reactive, h } from 'vue'
import AddOrUpdate from './components/AddOrUpdate.vue'
import Detail from './components/Detail.vue'
import { CrudSchema, useCrudSchemas } from '@/hooks/web/useCrudSchemas'
import { TableColumn } from '@/types/table'

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
    label: '菜单名称',
    search: {
      show: true
    },
    form: {
      colProps: {
        span: 24
      },
      formItemProps: {
        required: true
      }
    }
  },
  {
    field: 'orderNum',
    label: '排序',
    form: {
      show: false
    }
  },
  {
    field: 'url',
    label: '请求地址',
    form: {
      colProps: {
        span: 24
      },
      formItemProps: {
        required: true
      }
    },
    detail: {
      span: 24
    }
  },
  {
    field: 'component',
    label: '组件路径',
    form: {
      colProps: {
        span: 24
      },
      formItemProps: {
        required: true
      }
    },
    detail: {
      span: 24
    }
  },
  {
    field: 'menuType',
    label: '类型',
    form: {
      show: false
    },
    formatter: (_: Recordable, __: TableColumn, cellValue: number) => {
      let type
      switch (cellValue) {
        case 1:
          type = 'success'
          break
        case 2:
          type = 'danger'
          break
        default:
          type = 'primary'
      }
      return h(
        ElTag,
        {
          type: type
        },
        () => (cellValue === 1 ? '菜单' : cellValue === 2 ? '按钮' : '目录')
      )
    }
  },
  {
    field: 'permissionCode',
    label: '权限标识',
    form: {
      colProps: {
        span: 24
      },
      formItemProps: {
        required: true
      }
    },
    detail: {
      span: 24
    }
  },
  {
    field: 'config',
    label: '扩展配置',
    form: {
      colProps: {
        span: 24
      }
    },
    detail: {
      span: 24
    }
  },
  {
    field: 'createdBy',
    label: '创建人',
    form: {
      show: false
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

const AddAction = (row: TableData | null) => {
  dialogTitle.value = t('exampleDemo.add')
  // 外部新增按钮只能增加1级菜单 0: 目录, 1: 菜单, 2: 按钮
  if (row) {
    row.menuType = 0
    row.parentId = 0
  }
  tableObject.currentRow = row
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

const actionType = ref('')

const action = (row: TableData, type: string) => {
  dialogTitle.value = t(type === 'edit' ? 'exampleDemo.edit' : 'exampleDemo.detail')
  actionType.value = type
  // 选择数据新增按钮根据选择确定菜单类型， 0: 目录, 1: 菜单, 2: 按钮
  if (row) {
    if (row.menuType === 0) {
      row.parentId = row.id
      row.menuType = 1
    } else if (row.menuType === 1) {
      row.parentId = row.id
      row.menuType = 2
    } else if (row.menuType === 2) {
      row.parentId = row.parentId
      row.menuType = 2
    } else {
      row.menuType = 0
      row.parentId = 0
    }
  }
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
      <ElButton type="primary" @click="AddAction" v-hasPermi="['system:menu:add']">{{
        t('exampleDemo.add')
      }}</ElButton>
      <ElButton
        :loading="delLoading"
        type="danger"
        v-hasPermi="['system:menu:delete']"
        @click="delData(null, true)"
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
        <ElButton type="primary" v-hasPermi="['system:menu:edit']" @click="action(row, 'edit')">
          {{ t('exampleDemo.edit') }}
        </ElButton>
        <ElButton type="success" v-hasPermi="['system:menu:add']" @click="AddAction(row)">
          {{ t('exampleDemo.add') }}
        </ElButton>
        <ElButton
          v-if="row.createdBy != 'system'"
          type="danger"
          v-hasPermi="['system:menu:delete']"
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
        v-hasPermi="['system:menu:add']"
      >
        {{ t('exampleDemo.save') }}
      </ElButton>
      <ElButton @click="dialogVisible = false">{{ t('dialogDemo.close') }}</ElButton>
    </template>
  </Dialog>
</template>
