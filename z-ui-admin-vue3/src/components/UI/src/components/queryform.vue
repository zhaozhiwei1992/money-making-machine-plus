<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Search } from '@/components/Search'
import { reactive, ref, unref, inject, onMounted } from 'vue'
import { useValidator } from '@/hooks/web/useValidator'
import { FormSchema } from '@/types/form'
import { useEmitt } from '@/hooks/web/useEmitt'
import { getQueryformListByMenuApi } from '@/api/ui/queryform'
import { getDictOptions } from '@/utils/dict'

const { emitter } = useEmitt()

const props = defineProps({
  title: String,
  componentId: String
})

const { required } = useValidator()

const schema = reactive<FormSchema[]>([])

// 初始化编辑区信息
onMounted(() => {
  // 这里通过异步接口后端获取返回
  // 获取页签信息, 填充
  const menuId: string | undefined = inject('menuId')
  console.log(menuId, '查询区菜单id')
  getQueryformListByMenuApi(menuId).then((res) => {
    // 重新构建数据
    res.data.forEach((element) => {
      const formItem: FormSchema = {
        field: element.code,
        label: element.name,
        component: element.type,
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
      if (element.type == 'Select' || element.type === 'Radio') {
        // 获取值集填充
        console.log(element.source, '数据源id')
        formItem.componentProps.options = getDictOptions(element.source)
      } else if (element.type === 'DatePicker') {
        // formItem.componentProps.type = element.format
      }
      schema.push(formItem)
    })
  })
  // tabs.value.push(...tabsSchema)
})

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
//       ],
//       onChange: (value: string) => {
//         console.log(value)
//       }
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

const isGrid = ref(false)

const layout = ref('inline')
layout.value = unref(layout) === 'inline' ? 'bottom' : 'inline'

const buttomPosition = ref('right')

const search = (data) => {
  // console.log(data, 'searchParam')
  // 出发列表查询事件
  emitter.emit('tableLoadData', { componentId: props.componentId, data: data })
}
</script>

<template>
  <ContentWrap :title="props.title">
    <Search
      :schema="schema"
      :is-col="isGrid"
      :layout="layout"
      :buttom-position="buttomPosition"
      @search="search"
      expand
      expand-field="field3"
      ref="comRef"
    />
  </ContentWrap>
</template>
