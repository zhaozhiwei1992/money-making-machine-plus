<script name="UserIndex" setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Search } from '@/components/Search'
import { Dialog } from '@/components/Dialog'
import { useI18n } from '@/hooks/web/useI18n'
import { ElButton, ElTag } from 'element-plus'
import { Table } from '@/components/Table'
import { getTableListApi, saveTableApi, delTableListApi } from '@/api/system/user'
import { getRoleSelect } from '@/api/system/role'
import { getDeptSelect } from '@/api/system/dept'
import { getPositionSelect } from '@/api/system/position'
import { useTable } from '@/hooks/web/useTable'
import { TableData } from '@/api/table/types'
import { ref, unref, reactive, h, onMounted } from 'vue'
import AddOrUpdate from './components/AddOrUpdate.vue'
import Detail from './components/Detail.vue'
import { CrudSchema, useCrudSchemas } from '@/hooks/web/useCrudSchemas'
import { TableColumn } from '@/types/table'
import { ComponentOptions } from '@/types/components'
import { UserVO } from '@/api/system/user/types'

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

const roleOptions = ref<ComponentOptions[] | any>([])
const deptOptions = ref<ComponentOptions[] | any>([])
const positionOptions = ref<ComponentOptions[] | any>([])

async function fetchOptions() {
  try {
    // 并行发起所有请求
    const [roles, departments, positions] = await Promise.all([
      getRoleSelect(),
      getDeptSelect(),
      getPositionSelect()
    ])

    deptOptions.value = departments
    positionOptions.value = positions
    roleOptions.value = roles
  } catch (error) {
    console.error('Failed to fetch options:', error)
  }
}

onMounted(async () => {
  fetchOptions()
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
    label: '中文名',
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
    field: 'login',
    label: '登录名',
    form: {
      formItemProps: {
        required: true
      }
    }
  },
  {
    field: 'phoneNumber',
    label: '手机号',
    form: {
      formItemProps: {
        required: true
      }
    }
  },
  {
    field: 'email',
    label: '邮箱'
  },
  {
    field: 'departmentIdListStr',
    label: '部门',
    form: {
      component: 'Cascader',
      componentProps: {
        style: {
          width: '100%'
        },
        options: deptOptions
      }
    }
  },
  {
    field: 'positionIdListStr',
    label: '岗位',
    form: {
      component: 'Select',
      componentProps: {
        style: {
          width: '100%'
        },
        options: positionOptions
      }
    }
  },
  {
    field: 'roleIdListStr',
    label: '角色',
    form: {
      component: 'Select',
      componentProps: {
        style: {
          width: '100%'
        },
        options: roleOptions
      }
    }
  },
  {
    field: 'activated',
    label: '激活',
    formatter: (_: Recordable, __: TableColumn, cellValue: boolean) => {
      return h(
        ElTag,
        {
          type: 'success'
        },
        () => (cellValue === true ? '激活' : '未激活')
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
    field: 'createdBy',
    label: '创建人',
    form: {
      show: false
    }
  },
  {
    field: 'createdDate',
    label: '创建日期',
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

console.log(allSchemas)

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
      const data = (await write?.getFormData()) as UserVO
      // 数据特殊处理
      data.departmentIdListStr = data.departmentIdListStr.join(',')
      console.log(data)
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
      <ElButton type="primary" v-hasPermi="['system:user:add']" @click="AddAction">{{
        t('exampleDemo.add')
      }}</ElButton>
      <ElButton
        :loading="delLoading"
        v-hasPermi="['system:user:delete']"
        type="danger"
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
    >
      <template #action="{ row }">
        <ElButton type="success" v-hasPermi="['system:user:detail']" @click="action(row, 'detail')">
          {{ t('exampleDemo.detail') }}
        </ElButton>
        <ElButton
          v-if="row.createdBy != 'system'"
          type="danger"
          v-hasPermi="['system:user:delete']"
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
        v-hasPermi="['system:user:add']"
        :loading="loading"
        @click="save"
      >
        {{ t('exampleDemo.save') }}
      </ElButton>
      <ElButton @click="dialogVisible = false">{{ t('dialogDemo.close') }}</ElButton>
    </template>
  </Dialog>
</template>
