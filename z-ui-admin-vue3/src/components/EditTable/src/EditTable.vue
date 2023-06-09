<script setup lang="ts">
import { ref, unref } from 'vue'
import {
  ElButton,
  ElInput,
  ElTable,
  ElTableColumn,
  ElDatePicker,
  ElTreeSelect,
  ElSelect,
  ElOption
} from 'element-plus'
import { EditTableColumn } from './types'
import { useEmitt } from '@/hooks/web/useEmitt'
import { getTableDataListApi } from '@/api/edittable'

// 传入必要属性
const props = defineProps({
  cols: Array<EditTableColumn>
})

const { emitter } = useEmitt()

// 对外暴露一些方法, 通过事件 开始
useEmitt({
  name: 'edittable.tableLoadData',
  callback: (queryObj: any) => {
    console.log(queryObj, '数据查询对象')
    console.log(queryObj.componentId, '触发来源组件')
    // 根据传入参数获取数据
    getTableDataListApi({ page: 1, size: 10, url: queryObj.url }).then((res) => {
      tableData.value.push(...res.data)
    })
  }
})

// 获取选中数据
useEmitt({
  name: 'edittable.getSelectedData',
  callback: (tableObj: any) => {
    console.log(tableObj, '数据查询对象')
    // 获取指定列表选中数据
  }
})

useEmitt({
  name: 'edittable.getDataList',
  callback: (res: any) => {
    console.log(res)
    const data = getList()
    emitter.emit('editDataTable.getDataListEnd', data)
  }
})

useEmitt({
  name: 'edittable.addRow',
  callback: (data: any) => {
    addRow(data)
  }
})

// 对外暴露一些方法, 通过事件 结束

const tableData = ref<any[]>([])

// 增加行
const addRow = (dataObj) => {
  tableData.value.push(dataObj)
}

// 返回列表数据
const getList = () => {
  return unref(tableData)
}

// 设置列表数据, 覆盖
const setData = (data) => {
  // 先清空
  tableData.value.splice(0)
  tableData.value.push(...data)
}

const elTableRef = ref()

// 获取选中数据
const getSelection = () => {
  return elTableRef.value.getSelectionRows()
}

// 如果不导出, 父组件是调用不到子组件方法的
defineExpose({
  addRow,
  getList,
  setData,
  getSelection,
  tableData
})
</script>

<template>
  <el-table :data="tableData" style="width: 100%" ref="elTableRef">
    <!-- 额外添加的编号项（可删除） -->
    <el-table-column type="selection" width="55" />
    <el-table-column type="index" :label="'编号'" :width="55" />
    <!-- 自定义表项 -->
    <el-table-column
      v-for="(col, index) in cols"
      :key="index"
      :prop="col.code"
      :label="col.name"
      :width="col.width"
    >
      <!-- 调整可编辑列表开始 -->
      <!-- 设计思路, 先之支持下拉, 输入, 树形, 日期 -->
      <template #default="scope">
        <!-- 非编辑直接显示 -->
        <span v-if="col.isEdit == false">{{ scope.row[col.code] }}</span>
        <!-- 可编辑场景下特殊处理 -->
        <el-input
          v-else-if="col.isEdit == true && col.type === 'Input'"
          size="small"
          v-model="scope.row[col.code]"
          placeholder="请输入内容"
        />
        <el-select
          v-else-if="col.type === 'Select'"
          size="small"
          v-model="scope.row[col.code]"
          placeholder="请选择内容"
          value=""
        >
          <el-option
            v-for="option in col.mapping"
            :value="option.value"
            :key="option.value"
            :label="option.name"
          />
        </el-select>
        <el-date-picker
          v-else-if="col.type === 'Date'"
          v-model="scope.row[col.code]"
          :name="col.code"
          type="date"
          format="yyyy-MM-dd"
          value-format="yyyy-MM-dd"
          placeholder="请选择日期"
        />
        <ElTreeSelect
          v-else-if="col.type === 'Tree'"
          v-model="scope.row[col.code]"
          :options="col.mapping"
          :show-all-levels="false"
          :props="{ expandTrigger: 'hover' }"
          clearable
        />
        <el-button
          v-else-if="col.type === 'Btn'"
          @click="onSelected(scope.$index, scope.row)"
          type="primary"
          size="small"
        >
          {{ col.name }}
        </el-button>
      </template>
      <!-- 调整可编辑列表结束, 如果没有此类需求, 删掉开头结尾内容即可 -->
    </el-table-column>
  </el-table>
</template>
