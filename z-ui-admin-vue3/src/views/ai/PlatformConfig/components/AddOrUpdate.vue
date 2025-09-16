<script setup lang="ts">
import { TableData } from '@/api/ai/platformconfig/types'
import { Form } from '@/components/Form'
import { useForm } from '@/hooks/web/useForm'
import { useValidator } from '@/hooks/web/useValidator'
import { FormSchema } from '@/types/form'
import { PropType, reactive, watch } from 'vue'

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
    id: [required()],
    created_by: [required()],
    created_date: [required()],
    last_modified_by: [required()],
    last_modified_date: [required()],
    api_key: [required()],
    app_id: [required()],
    base_url: [required()],
    bus_type: [required()],
    code: [required()],
    name: [required()],
    picture: [required()],
    remark: [required()],
    status: [required()],
    type: [required()],
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
  <Form :rules="rules" @register="register" />
</template>
