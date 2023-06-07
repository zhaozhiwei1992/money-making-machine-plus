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
// 编辑区赋值
useEmitt({
  name: 'editform.setValue',
  callback: (dataObj: any) => {
    // 设置编辑区值
    setValue(dataObj)
  }
})

const { emitter } = useEmitt()

// 这个register感觉是用来绑定form的
const { register, methods } = useForm({})

useEmitt({
  name: 'editform.getValue',
  callback: (editObj: any) => {
    // console.log('编辑区对象 ', editObj)
    // 获取编辑区值
    const { getFormData } = methods
    getFormData().then((data) => {
      // console.log('编辑区数据', data)
      emitter.emit('editform.getValueEnd', data)
    })
  }
})

// 对外提供方法  结束

const props = defineProps({
  title: String,
  name: String,
  comRef: ref<FormExpose>
})

const schema = reactive<FormSchema[]>([])

// 初始化编辑区信息
onMounted(() => {
  // 这里通过异步接口后端获取返回
  // 获取页签信息, 填充
  const menuId: string | undefined = inject('menuId')
  getEditformListByMenuApi(menuId).then((res) => {
    // 重新构建数据
    res.data.forEach((element) => {
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
      if (element.required === 'true') {
        formItem.formItemProps.rules.push(required())
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
})

const comRef = ref<FormExpose>()
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

// 设置编辑区值
/**
 * data: {
 *    field1: 'field1',
      field2: '2',
      field3: '2',
      field4: ['1', '3'],
      field5: '2022-01-27',
      field6: '17:00'
 * }
 */
const setValue = (data) => {
  const elFormRef = unref(comRef)?.getElFormRef()
  elFormRef?.resetFields()
  unref(comRef)?.setValues(data)
}
</script>

<template>
  <ContentWrap :title="props.title">
    <Form :schema="schema" ref="comRef" @register="register" />
  </ContentWrap>
</template>
