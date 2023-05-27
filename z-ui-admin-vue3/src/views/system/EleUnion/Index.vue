<template>
  <el-row :gutter="20">
    <el-col :span="6">
      <ElCard shadow="hover" class="mb-20px">
        <el-input placeholder="输入关键字进行过滤" v-model="filterText" />
      </ElCard>
      <ElCard shadow="hover" class="mb-20px">
        <el-tree
          class="filter-tree"
          :data="treeData"
          :props="defaultProps"
          default-expand-all
          :filter-node-method="filterNode"
          @node-click="handleNodeClick"
          ref="tree"
        />
      </ElCard>
    </el-col>
    <el-col :span="18">
      <ElCard shadow="hover" class="mb-20px">
        <el-row>
          <el-button type="primary" @click="addRow">增加行</el-button>
          <el-button type="primary" @click="deleteRow">删除行</el-button>
          <el-button type="primary" @click="saveData">保存</el-button>
        </el-row>
        <el-row>
          <div class="grid-content bg-purple">
            <el-table :data="tableData" style="width: 100%" ref="singleTable">
              <el-table-column type="selection" width="55" />
              <el-table-column prop="eleCatCode" label="分类编码" width="100">
                <template #default="scope">
                  <span v-if="treeSelected == true">{{ scope.row['eleCatCode'] }}</span>
                  <!-- 可编辑场景下特殊处理 -->
                  <el-input
                    size="small"
                    v-model="scope.row['eleCatCode']"
                    placeholder="请输入要素分类编码"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="eleCatName" label="分类名称" width="200">
                <template #default="scope">
                  <span v-if="treeSelected == true">{{ scope.row['eleCatName'] }}</span>
                  <!-- 可编辑场景下特殊处理 -->
                  <el-input
                    size="small"
                    v-model="scope.row['eleCatName']"
                    placeholder="请输入要素分类名称"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="eleCode" label="要素编码" width="100">
                <template #default="scope">
                  <!-- 可编辑场景下特殊处理 -->
                  <el-input
                    size="small"
                    v-model="scope.row['eleCode']"
                    placeholder="请输入要素编码"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="eleName" label="要素名称" width="200">
                <template #default="scope">
                  <!-- 可编辑场景下特殊处理 -->
                  <el-input
                    size="small"
                    v-model="scope.row['eleName']"
                    placeholder="请输入要素名称"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="parentId" label="父节点id" width="100">
                <template #default="scope">
                  <!-- 可编辑场景下特殊处理 -->
                  <el-input
                    size="small"
                    v-model="scope.row['parentId']"
                    placeholder="请输入父级id"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="levelNo" label="级次" width="100" />
              <el-table-column prop="isLeaf" label="是否末级" width="100">
                <template #default="scope">
                  <!-- 可编辑场景下特殊处理 -->
                  <el-select
                    size="small"
                    v-model="scope.row['isLeaf']"
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
              <el-table-column prop="isEnabled" label="是否启用" width="100">
                <template #default="scope">
                  <!-- 可编辑场景下特殊处理 -->
                  <el-select
                    size="small"
                    v-model="scope.row['isEnabled']"
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
            </el-table>
          </div>
        </el-row>
      </ElCard>
    </el-col>
  </el-row>
</template>

<script name="EleUnionIndex" setup lang="ts">
import {
  ElButton,
  ElTree,
  ElInput,
  ElTable,
  ElTableColumn,
  ElSelect,
  ElOption,
  ElRow,
  ElCol,
  ElCard,
  ElMessage
} from 'element-plus'
import { ref, reactive, onMounted, watch, getCurrentInstance } from 'vue'
import * as EleUnionApi from '@/api/system/ele-union'
import { EleUnionTreeVo, EleUnionVO } from '@/api/system/ele-union/types'

interface TreeNodeType {
  id: string
  label: string
}

const currentSelectTreeNode: TreeNodeType = reactive({
  id: '',
  label: ''
})

const currentInstance: any = getCurrentInstance()

const filterText = ref('')
const treeData: EleUnionTreeVo[] = reactive([])
const defaultProps = ref({
  children: 'children',
  label: 'label'
})
const tableData: EleUnionVO[] = reactive([])
// 默认不选中
const treeSelected = ref(false)
const trueFalse = ref([
  { value: true, code: true, name: '是' },
  { value: false, code: false, name: '否' }
])

const filterNode = (value, data) => {
  if (!value) return true
  return data.label.indexOf(value) !== -1
}

const handleNodeClick = (data) => {
  currentSelectTreeNode.id = data.id
  currentSelectTreeNode.label = data.label.split('-')[1]
  initTableData(data)
}
const deleteRow = () => {
  // 1. 获取列表选中行
  const selectedData = currentInstance.ctx.$refs.singleTable.selection
  // 2. 不存在id, 直接删除tableData中数据
  selectedData.forEach((selected) => {
    const filterData: EleUnionVO[] = tableData.filter(function (item) {
      // 根据eleCode来删除数据
      return item.eleCode != selected.eleCode
    })
    tableData.splice(0)
    tableData.push(...filterData)
  })
}
const addRow = () => {
  tableData.push({
    id: -1,
    eleCode: '',
    eleName: '',
    eleCatCode: currentSelectTreeNode.id,
    eleCatName: currentSelectTreeNode.label,
    parentId: '',
    levelNo: 0,
    isLeaf: false,
    isEnabled: false
  })
}

const saveData = () => {
  // 保存列表数据
  EleUnionApi.create(tableData)
    .then((res) => {
      console.log('保存成功', res)
      ElMessage.info('保存成功')
      initLeftTreeData()
    })
    .catch((err) => {
      console.log(err)
    })
}
const initTableData = (treeData) => {
  // 根据分类id查询基础要素信息
  EleUnionApi.getDetail(treeData.id).then((res) => {
    // 清空列表
    tableData.splice(0)
    tableData.push(...res.data)
  })
}

const initLeftTreeData = () => {
  EleUnionApi.getTree().then((res) => {
    // 清空原数据, 重新填充
    treeData.splice(0)
    treeData.push(...res.data)
  })
}

watch(filterText, (value) => {
  currentInstance.ctx.$refs.tree.filter(value)
})

/** 初始化 **/
onMounted(() => {
  initLeftTreeData()
})
</script>
