<script name="UserIndex" setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import {
  ElButton,
  ElTable,
  ElTableColumn,
  ElRow,
  ElCol,
  ElInput,
  ElSelect,
  ElOption
} from 'element-plus'
import { generateCodeApi } from '@/api/code-generator'
import { ref, reactive } from 'vue'

const tableNameFilter = ref('')

const tableRef = ref()
const colRef = ref()

const tableConfig = {
  height: window.innerHeight - 273
}

// vue3中必须使用代理相应对象才会监听变化
let tableData: any = reactive([])

let colData: any = reactive([])

const handleTableSelectionChange = (row) => {
  console.log('table.select', row)
  // 重新加载列信息, 根据当前选中tableName
}

const genDoc = () => {
  // 弹出选择生成类型, 支持html, markdown, word
  const data: any = {}
  data.tableData = tableData
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
</script>

<template>
  <ContentWrap>
    <el-row>
      <el-col :span="8">
        <div class="mb-10px">
          <el-input placeholder="输入表名进行过滤" v-model="tableNameFilter" />
          <ElButton type="primary" size="small" @click="genDoc">生成文档</ElButton>
        </div>

        <el-table
          :data="tableData"
          style="width: 100%"
          ref="tableRef"
          :height="tableConfig.height"
          highlight-current-row
          @current-change="handleTableSelectionChange"
        >
          <el-table-column type="selection" width="50" />
          <el-table-column prop="tableComments" label="表中文名" width="150" />
          <el-table-column prop="tableName" label="表英文名" width="150" />
        </el-table>
      </el-col>
      <el-col :span="16">
        <el-table
          :data="colData"
          style="width: 100%"
          ref="colRef"
          :height="tableConfig.height"
          highlight-current-row
        >
          <el-table-column type="selection" width="50" />
          <el-table-column prop="colComments" label="中文名" width="140" />
          <el-table-column prop="columnName" label="英文名" width="120" />
          <el-table-column prop="columnType" label="字段类型" />
          <el-table-column prop="requirement" label="是否必填" />
          <el-table-column prop="pKey" label="是否主键" />
        </el-table>
      </el-col>
    </el-row>
  </ContentWrap>
</template>
