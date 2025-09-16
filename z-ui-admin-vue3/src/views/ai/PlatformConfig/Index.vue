<script name="UserIndex" setup lang="ts">
import { delTableListApi, getTableListApi, saveTableApi } from '@/api/ai/platformconfig'
import { TableData } from '@/api/ai/platformconfig/types'
import { ContentWrap } from '@/components/ContentWrap'
import { Dialog } from '@/components/Dialog'
import { Search } from '@/components/Search'
import { Table } from '@/components/Table'
import { CrudSchema, useCrudSchemas } from '@/hooks/web/useCrudSchemas'
import { useI18n } from '@/hooks/web/useI18n'
import { useTable } from '@/hooks/web/useTable'
import { ElButton } from 'element-plus'
import { reactive, ref, unref } from 'vue'
import AddOrUpdate from './components/AddOrUpdate.vue'
import Detail from './components/Detail.vue'

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
        label: '主键',
        search: {
          show: false
        },
        form: {
          show: false
        },
        detail: {
          show: false
        }
      },
      {
        field: 'created_by',
        label: '',
        search: {
          show: false
        },
        form: {
          show: false
        },
        detail: {
          show: false
        }
      },
      {
        field: 'created_date',
        label: '',
        search: {
          show: false
        },
        form: {
          show: false
        },
        detail: {
          show: false
        }
      },
      {
        field: 'last_modified_by',
        label: '',
        search: {
          show: false
        },
        form: {
          show: false
        },
        detail: {
          show: false
        }
      },
      {
        field: 'last_modified_date',
        label: '',
        search: {
          show: false
        },
        form: {
          show: false
        },
        detail: {
          show: false
        }
      },
      {
        field: 'api_key',
        label: 'API Key',
        search: {
          show: false
        },
        form: {
          show: false
        },
        detail: {
          show: false
        }
      },
      {
        field: 'app_id',
        label: '应用ID',
        search: {
          show: false
        },
        form: {
          show: false
        },
        detail: {
          show: false
        }
      },
      {
        field: 'base_url',
        label: '应用所属平台地址',
        search: {
          show: false
        },
        form: {
          show: false
        },
        detail: {
          show: false
        }
      },
      {
        field: 'bus_type',
        label: '业务类型',
        search: {
          show: false
        },
        form: {
          show: false
        },
        detail: {
          show: false
        }
      },
      {
        field: 'code',
        label: '应用凭据',
        search: {
          show: false
        },
        form: {
          show: false
        },
        detail: {
          show: false
        }
      },
      {
        field: 'name',
        label: '应用名称',
        search: {
          show: false
        },
        form: {
          show: false
        },
        detail: {
          show: false
        }
      },
      {
        field: 'picture',
        label: '应用图标',
        search: {
          show: false
        },
        form: {
          show: false
        },
        detail: {
          show: false
        }
      },
      {
        field: 'remark',
        label: '应用摘要',
        search: {
          show: false
        },
        form: {
          show: false
        },
        detail: {
          show: false
        }
      },
      {
        field: 'status',
        label: '是否启用',
        search: {
          show: false
        },
        form: {
          show: false
        },
        detail: {
          show: false
        }
      },
      {
        field: 'type',
        label: '平台类型',
        search: {
          show: false
        },
        form: {
          show: false
        },
        detail: {
          show: false
        }
      },

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
      <ElButton type="primary" @click="AddAction">{{ t('exampleDemo.add') }}</ElButton>
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
