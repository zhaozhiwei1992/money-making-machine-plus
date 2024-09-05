<script setup lang="ts">
import { useForm } from '@/hooks/web/useForm'
import { PropType, reactive, watch } from 'vue'
import { TableData } from '@/api/table/types'
import { useValidator } from '@/hooks/web/useValidator'
import { FormSchema } from '@/types/form'
import {
  ElRow,
  ElCard,
  ElCol,
  ElTable,
  ElTableColumn,
  ElForm,
  ElFormItem,
  ElInput
} from 'element-plus'

const { required } = useValidator()

const props = defineProps({
  currentRow: {
    type: Object as PropType<Nullable<TableData>>,
    default: () => null
  },
  formSchema: {
    type: Array as PropType<FormSchema[]>,
    default: () => []
  }
})

const rules = reactive({
  title: [required()],
  author: [required()],
  importance: [required()],
  pageviews: [required()],
  display_time: [required()],
  content: [required()]
})

const { register, methods, elFormRef } = useForm({
  schema: props.formSchema
})

watch(
  () => props.currentRow,
  (currentRow) => {
    if (!currentRow) return
    const { setValues } = methods
    setValues(currentRow)
  },
  {
    deep: true,
    immediate: true
  }
)

defineExpose({
  elFormRef,
  getFormData: methods.getFormData
})
</script>

<template>
  <el-form :model="formObj" label-width="80px">
    <el-form-item label="任务名称">
      <el-input v-model="formObj.name" autocomplete="off"></el-input>
    </el-form-item>
    <el-row>
      <el-col :span="18">
        <el-form-item label="表达式">
          <el-input v-model="formObj.cronExpression" autocomplete="off"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <!-- 这里紧跟着 横着来两按钮, 一个是表达式配置, 一个是预览 -->
        <el-button @click="genCronExpression">生成表达式</el-button>
      </el-col>
    </el-row>
    <el-row>
      <el-form-item label="任务入口">
        <!-- 列出代码列表, 选择填入即可 -->
        <el-input v-model="formObj.startClass" autocomplete="off"></el-input>
      </el-form-item>
    </el-row>
    <el-switch
      style="display: block"
      v-model="formObj.enable"
      active-color="#13ce66"
      inactive-color="#ff4949"
      active-text="启用"
      inactive-text="停用"
    >
    </el-switch>
  </el-form>
</template>
