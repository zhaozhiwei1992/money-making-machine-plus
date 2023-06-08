<template>
  <ContentWrap>
    <div style="margin-bottom: 20px">
      <el-button size="default" type="primary" @click="addComponent()"> 新增组件 </el-button>
      <el-button size="default" type="primary" @click="save()"> 保存 </el-button>
      <el-button size="default" type="primary" @click="reset()"> 重置 </el-button>
    </div>
    <el-tabs
      v-model="editableTabsValue"
      type="card"
      class="demo-tabs"
      closable
      @tab-remove="removeComponent"
      @tab-change="selectComponent"
    >
      <el-tab-pane
        v-for="item in editableTabs"
        :key="item.key"
        :label="item.title"
        :name="item.name"
      >
        <template #label>
          <!-- 可编辑列表, 根据tab.component 显示不同列表, 支持增加不同的组件信息 -->
          <!-- 自定义个可编辑列表组件, 这里用 -->
        </template>
      </el-tab-pane>
    </el-tabs>
  </ContentWrap>
  <el-dialog v-model="dialogFormVisible" title="组件新增">
    <el-form :model="form">
      <el-form-item label="组件列表">
        <el-select v-model="form.component" placeholder="请选择一个组件">
          <el-option label="按钮区" value="toolbutton" />
          <el-option label="查询区" value="queryform" />
          <el-option label="页签区" value="tab" />
          <el-option label="列表区" value="table" />
          <el-option label="编辑区" value="editform" />
          <el-option label="左侧树" value="lefttree" />
        </el-select>
      </el-form-item>
      <!-- 组件名默认跟组件列表value一样 -->
      <el-form-item label="组件名称">
        <el-input v-model="form.title" autocomplete="off" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogClose">取消</el-button>
        <el-button type="primary" @click="dialogConfirm"> 确认 </el-button>
      </span>
    </template>
  </el-dialog>
</template>
<script setup lang="ts" name="GoView">
import { ContentWrap } from '@/components/ContentWrap'
import { ref, reactive } from 'vue'
import {
  ElTabs,
  ElTabPane,
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElSelect,
  ElOption,
  ElInput
} from 'element-plus'

const dialogFormVisible = ref(false)

const form = reactive({
  title: '',
  component: ''
})

const dialogConfirm = () => {
  // 根据选择组件返回数据填充
  editableTabs.value.push({
    title: form.title,
    name: form.component,
    key: form.component
  })
  // 选中的组件
  editableTabsValue.value = form.component
  dialogFormVisible.value = false
  // 根据选中组件显示不同的编辑区列表
}

const dialogClose = () => {
  // 清理form
  form.title = ''
  form.component = ''
  dialogFormVisible.value = false
}

interface ComponentType {
  title: string
  name: string
  key: string
}
const editableTabsValue = ref('2')
const editableTabs = ref<ComponentType[]>([
  // {
  //   title: '按钮区',
  //   name: 'toolbutton',
  //   key: 'toolbutton'
  // },
  // {
  //   title: 'Tab 2',
  //   name: '2',
  //   content: 'Tab 2 content'
  // }
])

// 新增组件
const addComponent = () => {
  // 点击按钮先选择组件
  dialogFormVisible.value = true
}

// 删除组件
const removeComponent = (targetName: string) => {
  const tabs = editableTabs.value
  let activeName = editableTabsValue.value
  if (activeName === targetName) {
    tabs.forEach((tab, index) => {
      if (tab.name === targetName) {
        const nextTab = tabs[index + 1] || tabs[index - 1]
        if (nextTab) {
          activeName = nextTab.name
        }
      }
    })
  }

  editableTabsValue.value = activeName
  editableTabs.value = tabs.filter((tab) => tab.name !== targetName)
}

const selectComponent = (tabPanelName) => {
  // 根据选中页签作为组件名称, 清理全局变量
  console.log('选中组件名称', tabPanelName)
}

const save = () => {
  // 保存组件配置
}

// 重置保存对象
const reset = () => {
  //
}
</script>
