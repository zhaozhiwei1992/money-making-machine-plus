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
            <EditTable :cols="columns" ref="editDataTable" />
          </div>
        </el-row>
      </ElCard>
    </el-col>
  </el-row>
</template>

<script name="EleUnionIndex" setup lang="ts">
import { ElButton, ElTree, ElInput, ElRow, ElCol, ElCard, ElMessage } from 'element-plus'
import { ref, reactive, onMounted, watch, getCurrentInstance } from 'vue'
import * as EleUnionApi from '@/api/system/ele-union'
import { EleUnionTreeVo, EleUnionVO } from '@/api/system/ele-union/types'
import { EditTable } from '@/components/EditTable'
import { EditTableColumn } from '@/components/EditTable/src/types'

interface TreeNodeType {
  id: string
  label: string
}

const currentSelectTreeNode: TreeNodeType = reactive({
  id: '',
  label: ''
})

const trueFalse = [
  { value: true, name: '是' },
  { value: false, name: '否' }
]

// 获取可编辑列表中数据
const columns: EditTableColumn[] = [
  {
    code: 'eleCatCode',
    name: '要素分类编码',
    type: 'Input',
    required: false,
    isEdit: true,
    source: '',
    width: 100,
    mapping: []
  },
  {
    code: 'eleCatName',
    name: '要素分类名称',
    type: 'Input',
    required: false,
    isEdit: true,
    source: '',
    width: 200,
    mapping: []
  },
  {
    code: 'eleCode',
    name: '要素编码',
    type: 'Input',
    required: false,
    isEdit: true,
    source: '',
    width: 100,
    mapping: []
  },
  {
    code: 'eleName',
    name: '要素名称',
    type: 'Input',
    required: false,
    isEdit: true,
    source: '',
    width: 200,
    mapping: []
  },
  {
    code: 'parentId',
    name: '父节点id',
    type: 'Input',
    required: false,
    isEdit: true,
    source: '',
    width: 100,
    mapping: []
  },
  {
    code: 'levelNo',
    name: '级次',
    type: 'Input',
    required: false,
    isEdit: true,
    source: '',
    width: 100,
    mapping: []
  },
  {
    code: 'isLeaf',
    name: '是否末级',
    type: 'Select',
    required: false,
    isEdit: true,
    source: '',
    width: 100,
    mapping: trueFalse
  },
  {
    code: 'isEnabled',
    name: '是否启用',
    type: 'Select',
    required: false,
    isEdit: true,
    source: '',
    width: 100,
    mapping: trueFalse
  }
]

const currentInstance: any = getCurrentInstance()

const filterText = ref('')
const treeData: EleUnionTreeVo[] = reactive([])
const defaultProps = ref({
  children: 'children',
  label: 'label'
})

// 引用到字组件
const editDataTable = ref()

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
  const selectedData = editDataTable.value.getSelection()
  // 2. 不存在id, 直接删除tableData中数据
  const editTableData = editDataTable.value.getList()
  selectedData.forEach((selected) => {
    const filterData: EleUnionVO[] = editTableData.filter(function (item) {
      // 根据eleCode来删除数据
      return item.eleCode != selected.eleCode
    })
    console.log('filterData', filterData)
    editDataTable.value.setData(filterData)
  })
}
const addRow = () => {
  editDataTable.value.addRow({
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
  const editTableData = editDataTable.value.getList()
  EleUnionApi.create(editTableData)
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
    editDataTable.value.setData(res)
  })
}

const initLeftTreeData = () => {
  EleUnionApi.getTree().then((res) => {
    // 清空原数据, 重新填充
    treeData.splice(0)
    treeData.push(...res)
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
