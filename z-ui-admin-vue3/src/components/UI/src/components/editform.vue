<script setup lang="ts">
import { Form, FormExpose } from '@/components/Form'
import { useForm } from '@/hooks/web/useForm'
import { ContentWrap } from '@/components/ContentWrap'
import { reactive, ref, unref, inject, onMounted } from 'vue'
// import { useValidator } from '@/hooks/web/useValidator'
import { getEditformListByMenuApi } from '@/api/ui/editform'
import { FormSchema } from '@/types/form'
import { useEmitt } from '@/hooks/web/useEmitt'
import { getDictOptions } from '@/utils/dict'

// 对外提供一些方法 开始
const { emitter } = useEmitt()

// 编辑区赋值
emitter.on('editform.setValue', (dataObj: any) => {
  // 设置编辑区值
  setValueForForm('editform', dataObj)
})

// 这个register感觉是用来绑定form的
const { register, methods } = useForm({})

const { getFormData } = methods

// 对外提供方法  结束

const props = defineProps({
  title: String,
  componentId: String
})

const schema = reactive<FormSchema[]>([])

// 初始化编辑区信息
onMounted(async () => {
  // 这里通过异步接口后端获取返回
  // 获取页签信息, 填充
  const menuId: string | undefined = inject('menuId')
  const res = await getEditformListByMenuApi(menuId)
  // 重新构建数据
  res.forEach((element) => {
    const formItem: FormSchema = {
      field: element.code,
      label: element.name,
      component: element.type,
      value: [], // checkbox需要
      formItemProps: {
        rules: []
      },
      componentProps: {}
    }
    // 如果是必填
    if (element.requirement) {
      formItem.formItemProps?.rules?.push(true)
    }
    // 填充翻译值集
    if (element.type == 'Select' || element.type === 'Radio' || element.type === 'Checkbox') {
      // 获取值集填充
      console.log('数据源id', element.source)
      formItem.componentProps.options = getDictOptions(element.source)
    } else if (element.type === 'DatePicker') {
      // formItem.componentProps.type = element.format
    }
    schema.push(formItem)
  })
})

const comRef = ref<FormExpose>()

const formRefs = reactive({})
formRefs[props.componentId] = ref(null)

// 设置编辑区值
// 使用 假设你有一个数据对象 newData 和一个表单名称 "editform1"
// setValueForForm('editform1', newData);
const setValueForForm = (formName, data) => {
  const formRef = formRefs[formName]?.value
  if (formRef) {
    const elFormRef = formRef.getElFormRef()
    elFormRef?.resetFields()
    formRef.setValues(data)
  }
}

defineExpose({
  getFormData,
  setValueForForm
})
</script>

<template>
  <ContentWrap :title="props.title">
    <Form :schema="schema" ref="formRefs[props.componentId]" @register="register" />
  </ContentWrap>
</template>
