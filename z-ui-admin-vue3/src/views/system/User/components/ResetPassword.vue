<script setup lang="ts">
import { Form } from '@/components/Form'
import { useForm } from '@/hooks/web/useForm'
import { PropType, reactive, watch } from 'vue'
import { TableData } from '@/api/table/types'
import { useValidator } from '@/hooks/web/useValidator'
import { FormSchema } from '@/types/form'
import { PasswordResetVO } from '@/api/system/user/types'
import { resetPasswordApi } from '@/api/system/user'
import component from 'virtual:svg-icons-register'
import { ElMessage } from 'element-plus'

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
  oldPassword: [required()],
  newPassword: [required()],
  confirmNewPassword: [required()]
})

const formSchemas = reactive<FormSchema[]>([
  {
    field: 'oldPassword',
    label: '原密码',
    component: 'Input',
    formItemProps: {
      required: true
    }
  },
  {
    field: 'newPassword',
    label: '新密码',
    component: 'Input',
    formItemProps: {
      required: true
    }
  },
  {
    field: 'confirmPassword',
    label: '确认新密码',
    component: 'Input',
    formItemProps: {
      required: true
    }
  }
])

const { register, methods, elFormRef } = useForm({
  schema: formSchemas
})

const save = async () => {
  const data = (await methods.getFormData()) as PasswordResetVO
  console.log(data)
  await resetPasswordApi(data)
    .catch(() => {})
    .finally(() => {
      ElMessage.success('修改成功')
    })
}

defineExpose({
  save: save
})
</script>

<template>
  <Form :rules="rules" @register="register" />
</template>
