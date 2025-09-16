<script setup lang="ts">
import { getJobSelect, saveTableApi } from '@/api/system/task'
import { TableData } from '@/api/system/task/types'
import { useValidator } from '@/hooks/web/useValidator'
import { ComponentOptions, PropType, reactive, watch } from 'vue'

const { required } = useValidator()

const props = defineProps({
  currentRow: {
    type: Object as PropType<Nullable<TableData>>,
    default: () => null
  },
  isOpen: {
    type: Boolean as PropType<boolean>,
    required: true
  }
})

const rules = reactive({
  name: [required()]
})

watch(
  () => props.currentRow,
  (currentRow) => {
    if (!currentRow) return
    // TODO 设置表单数据
  },
  {
    deep: true,
    immediate: true
  }
)

const formRef = ref()

defineExpose({
  formRef
})

const dialogTitle = computed(() => {
  return '编辑'
})

const emit = defineEmits(['update:isOpen', 'success'])

const localIsOpen = computed({
  get() {
    return props.isOpen // 读取父组件的 props
  },
  set(newValue) {
    emit('update:isOpen', newValue) // 通知父组件更新
  }
})

const formData = ref<TableData>({
  id: '',
  name: '',
  cronExpression: '',
  startClass: '',
  enable: false
})

const loading = ref(false)
const save = async () => {
  await formRef.value.validate(async (isValid) => {
    if (isValid) {
      loading.value = true
      const res = await saveTableApi(formData.value)
        .catch(() => {})
        .finally(() => {
          loading.value = false
        })
      if (res) {
        localIsOpen.value = false
        emit('success')
      }
    }
  })
}

const jobOptions = ref<ComponentOptions[] | any>([])

onMounted(async () => {
  jobOptions.value = await getJobSelect()
})
</script>

<template>
  <Dialog :title="dialogTitle" v-model="localIsOpen">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
      <el-form-item label="任务名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入任务名称" />
      </el-form-item>
      <el-form-item label="CRON 表达式" prop="cron_expression">
        <crontab v-model="formData.cronExpression" />
      </el-form-item>
      <el-form-item label="定时任务入口" prop="start_class" key="start_class">
        <el-select v-model="formData.startClass" placeholder="Select" style="width: 240px">
          <el-option
            v-for="item in jobOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="是否启用" prop="enable">
        <el-switch v-model="formData.enable" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" @click="save" :loading="loading">确 定</el-button>
      <el-button @click="localIsOpen = false">取 消</el-button>
    </template>
  </Dialog>
</template>
