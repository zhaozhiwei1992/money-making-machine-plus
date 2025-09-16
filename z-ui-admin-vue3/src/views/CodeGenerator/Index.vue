<script name="UserIndex" setup lang="ts">
import { generateCodeApi, getColListApi, getTableListApi } from '@/api/code-generator'
import { TableData } from '@/api/code-generator/types'
import { ContentWrap } from '@/components/ContentWrap'
import { getCurrentInstance, reactive } from 'vue'
import PreviewCode from './components/PreviewCode.vue'

const currentInstance: any = getCurrentInstance()
// vue3中不能直接使用
// const $refs: any = currentInstance.ctx.$refs

const tableConfig = {
  height: window.innerHeight - 273
}

// vue3中必须使用代理相应对象才会监听变化
let tableData = reactive<TableData[]>([])
const tableCurrentSelectRow = ref<TableData>(tableData[0])

let colData: any = reactive([])
const trueFalse = [
  { value: 'true', code: 'true', name: '是' },
  { value: 'false', code: 'false', name: '否' }
]

const columnTypeMapping = [
  { value: 'varchar', code: 'varchar', name: 'varchar类型' },
  { value: 'bigint', code: 'bigint', name: 'bigint类型' },
  { value: 'int', code: 'int', name: 'int类型' },
  { value: 'date', code: 'date', name: '日期类型' },
  { value: 'datetime', code: 'datetime', name: '日期时间类型' },
  { value: 'bit', code: 'bit', name: 'bit类型' }
]

const handleTableSelectionChange = async (row) => {
  // 明细表数据过滤
  tableCurrentSelectRow.value = row
  fetchColData(currentTableName())
}
const currentTableName = (): string => {
  // 当前选中行的表名, 用来前端展现filter中过滤数据
  if (tableCurrentSelectRow.value != null) {
    return tableCurrentSelectRow.value.tableName
  } else {
    return ''
  }
}
const tableAddRow = () => {
  tableData.push({ tableName: '', tableType: 'TABLE', remarks: 'x' })
  const newTableRow = tableData[tableData.length - 1]
  tableCurrentSelectRow.value = newTableRow
  currentInstance.ctx.$refs.table.clearSelection()
  currentInstance.ctx.$refs.table.toggleRowSelection(newTableRow)
}
const tableDelRow = () => {
  // 1. 获取列表选中行
  const selectedData = currentInstance.ctx.$refs.table.getSelectionRows()
  console.log(selectedData)
  // 2. 不存在id, 直接删除tableData中数据
  selectedData.forEach((selected) => {
    tableData = tableData.filter(function (item) {
      // 根据eleCode来删除数据
      return item.tableName != selected.tableName
    })
    const newTableRow = tableData[tableData.length - 1]
    tableCurrentSelectRow.value = newTableRow
    currentInstance.ctx.$refs.table.clearSelection()
    currentInstance.ctx.$refs.table.toggleRowSelection(newTableRow)

    // 列数据也去掉该选中表名数据
    colData = colData.filter((item) => item.tableName != selected.tableName)
  })
}
const colAddRow = () => {
  // 增加列信息, 表为当前选中表
  colData.push({ tableName: tableCurrentSelectRow.value.tableName })
  console.log(colData)
}

const colDelRow = () => {
  // 1. 获取列表选中行
  const selectedData = currentInstance.ctx.$refs.colTable.getSelectionRows()
  // 2. 不存在id, 直接删除colData中数据
  selectedData.forEach((selected) => {
    colData = colData.filter(function (item: any) {
      // 根据columnName来删除数据
      return item.columnName != selected.columnName
    })
  })
}

const showView = ref(false)

const generatorCode = () => {
  // 根据列表数据生成代码
  const data: any = {}
  data.tableData = [tableCurrentSelectRow.value]
  data.colData = colData
  generateCodeApi(data)
    .then((res) => {
      {
        let typeCount = {
          type: 'application/octet-stream'
        }

        let blob = new Blob([res.data], typeCount)

        if (res.data.type != typeCount.type) {
          // 说明是普通对象数据，后台转换失败

          const fileReader: any = new FileReader()
          fileReader.readAsText(blob, 'utf-8')

          fileReader.onload = function () {
            let msg = JSON.parse(fileReader.result).errors.message
            console.log(msg)
          }
        }
        let fileName = 'code.zip'
        let url = window.URL.createObjectURL(blob)
        let link = document.createElement('a')
        link.style.display = 'none'
        link.href = url
        link.setAttribute('download', fileName)
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
      }
    })
    .catch((err) => {
      console.log(err)
    })
}

// 响应式数据
const loading = ref(false)
// 获取数据方法
const fetchTableData = async () => {
  try {
    loading.value = true
    const params = {
      tableName: searchKey.value
    }
    const res = await getTableListApi(params)
    tableData.length = 0
    tableData.push(...res)
  } catch (error) {
    console.error('获取表数据失败:', error)
    ElMessage.error('数据加载失败')
  } finally {
    loading.value = false
  }
}

const fetchColData = async (tableName: string) => {
  try {
    loading.value = true
    const params = {
      tableName: tableName
    }
    const res = await getColListApi(params)
    colData.length = 0
    colData.push(...res)
    // 界面新增列处理
    // colData.filter((v) => v.tableName == currentTableName())
  } catch (error) {
    console.error('获取表列数据失败:', error)
    ElMessage.error('数据加载失败')
  } finally {
    loading.value = false
  }
}

const searchKey = ref('')

const handleSearch = () => {
  fetchTableData()
}

const openView = () => {
  showView.value = true
}

onMounted(async () => {
  // 初始化数据
  fetchTableData()
})
</script>

<template>
  <ContentWrap>
    <el-row>
      <el-col :span="8">
        <div class="mb-10px">
          <ElButton type="primary" size="small" @click="tableAddRow">增加行</ElButton>
          <ElButton type="danger" size="small" @click="tableDelRow">删除行</ElButton>
          <el-input
            v-model="searchKey"
            placeholder="输入表名搜索"
            style="width: 300px; padding: 0px 10px"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button size="small" :icon="Search" @click="handleSearch">搜</el-button>
            </template>
          </el-input>
        </div>

        <el-table
          :data="tableData"
          style="width: 100%"
          ref="table"
          :height="tableConfig.height"
          highlight-current-row
          @current-change="handleTableSelectionChange"
        >
          <!-- <el-table-column type="selection" width="50" /> -->
          <el-table-column prop="tableComments" label="表中文名" width="150">
            <template #default="scope">
              <el-input
                size="small"
                v-model="scope.row['tableComments']"
                placeholder="请输出表中文名"
              />
            </template>
          </el-table-column>
          <el-table-column prop="tableName" label="表英文名" width="150">
            <template #default="scope">
              <el-input
                size="small"
                v-model="scope.row['tableName']"
                placeholder="请输出表中文名"
              />
            </template>
          </el-table-column>
        </el-table>
      </el-col>
      <el-col :span="16">
        <div class="mb-10px">
          <ElButton type="primary" size="small" @click="colAddRow">增加行</ElButton>
          <ElButton type="danger" size="small" @click="colDelRow">删除行</ElButton>
          <ElButton type="primary" size="small" @click="generatorCode">生成代码</ElButton>
          <ElButton type="primary" size="small" @click="openView()">预览代码</ElButton>
        </div>

        <el-table
          :data="colData"
          style="width: 100%"
          ref="colTable"
          :height="tableConfig.height"
          highlight-current-row
        >
          <el-table-column type="selection" width="50" />
          <el-table-column prop="colComments" label="中文名" width="140">
            <template #default="scope">
              <el-input
                size="small"
                v-model="scope.row['colComments']"
                placeholder="请输出列中文名"
              />
            </template>
          </el-table-column>
          <el-table-column prop="columnName" label="英文名" width="120">
            <template #default="scope">
              <el-input
                size="small"
                v-model="scope.row['columnName']"
                placeholder="请输出列英文名"
              />
            </template>
          </el-table-column>
          <el-table-column prop="columnType" label="字段类型">
            <template #default="scope">
              <el-select
                size="small"
                v-model="scope.row['columnType']"
                placeholder="请选择列类型"
                value=""
              >
                <el-option
                  v-for="option in columnTypeMapping"
                  :value="option.value"
                  :key="option.code"
                  :label="option.name"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="requirement" label="是否必填">
            <template #default="scope">
              <el-select
                size="small"
                v-model="scope.row['requirement']"
                placeholder="请选择内容"
                value=""
              >
                <el-option
                  v-for="option in trueFalse"
                  :value="option.value"
                  :key="option.code"
                  :label="option.name"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="pKey" label="是否主键">
            <template #default="scope">
              <el-select size="small" v-model="scope.row['pKey']" placeholder="请选择内容" value="">
                <el-option
                  v-for="option in trueFalse"
                  :value="option.value"
                  :key="option.code"
                  :label="option.name"
                />
              </el-select>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <el-row>
      <span style="color: red">注: 每次只能生成一个表的代码，以界面显示字段为准</span>
    </el-row>
  </ContentWrap>

  <!-- 代码预览 -->
  <PreviewCode
    ref="previewRef"
    :is-open="showView"
    @update:is-open="showView = $event"
    v-if="showView"
    :table-name="tableCurrentSelectRow.tableName"
  />
</template>
