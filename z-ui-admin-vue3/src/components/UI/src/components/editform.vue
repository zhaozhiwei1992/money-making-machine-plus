<script setup lang="ts">
import { Form, FormExpose } from '@/components/Form'
import { ContentWrap } from '@/components/ContentWrap'
import { reactive, ref, inject, onMounted } from 'vue'
// import { useValidator } from '@/hooks/web/useValidator'
import { getEditformListApi } from '@/api/ui/editform'
import { FormSchema } from '@/types/form'

const props = defineProps({
  title: String,
  name: String,
  comRef: ref<FormExpose>
})

const menuid: string | undefined = inject('menuid')

const schema = reactive<FormSchema[]>([])

// 初始化编辑区信息
onMounted(() => {
  // 这里通过异步接口后端获取返回
  // 获取页签信息, 填充
  console.log(menuid, '页签区菜单id')
  getEditformListApi(menuid).then((res) => {
    schema.push(...res.data)
  })
  // tabs.value.push(...tabsSchema)
})

// const formRef = ref<FormExpose>()
// const { required } = useValidator()
// const { t } = useI18n()
// const schema = reactive<FormSchema[]>([
//   {
//     field: 'field1',
//     label: t('formDemo.input'),
//     component: 'Input',
//     formItemProps: {
//       rules: [required()]
//     }
//   },
//   {
//     field: 'field2',
//     label: t('formDemo.select'),
//     component: 'Select',
//     componentProps: {
//       options: [
//         {
//           label: 'option1',
//           value: '1'
//         },
//         {
//           label: 'option2',
//           value: '2'
//         }
//       ]
//     }
//   },
//   {
//     field: 'field3',
//     label: t('formDemo.radio'),
//     component: 'Radio',
//     componentProps: {
//       options: [
//         {
//           label: 'option-1',
//           value: '1'
//         },
//         {
//           label: 'option-2',
//           value: '2'
//         }
//       ]
//     }
//   },
//   {
//     field: 'field4',
//     label: t('formDemo.checkbox'),
//     component: 'Checkbox',
//     value: [],
//     componentProps: {
//       options: [
//         {
//           label: 'option-1',
//           value: '1'
//         },
//         {
//           label: 'option-2',
//           value: '2'
//         },
//         {
//           label: 'option-3',
//           value: '3'
//         }
//       ]
//     }
//   },
//   {
//     field: 'field5',
//     component: 'DatePicker',
//     label: t('formDemo.datePicker'),
//     componentProps: {
//       type: 'date'
//     }
//   },
//   {
//     field: 'field6',
//     component: 'TimeSelect',
//     label: t('formDemo.timeSelect')
//   }
// ])

// const changeLabelWidth = (width: number | string) => {
//   unref(formRef)?.setProps({
//     labelWidth: width
//   })
// }

// const changeSize = (size: string) => {
//   unref(formRef)?.setProps({
//     size
//   })
// }

// const changeDisabled = (bool: boolean) => {
//   unref(formRef)?.setProps({
//     disabled: bool
//   })
// }

// const setValue = (reset: boolean) => {
//   const elFormRef = unref(formRef)?.getElFormRef()
//   if (reset) {
//     elFormRef?.resetFields()
//   } else {
//     unref(formRef)?.setValues({
//       field1: 'field1',
//       field2: '2',
//       field3: '2',
//       field4: ['1', '3'],
//       field5: '2022-01-27',
//       field6: '17:00'
//     })
//   }
// }
</script>

<template>
  <ContentWrap :title="props.title">
    <Form :schema="schema" ref="comRef" />
  </ContentWrap>
</template>
