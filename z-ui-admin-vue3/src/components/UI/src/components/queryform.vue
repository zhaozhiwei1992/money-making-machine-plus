<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Search } from '@/components/Search'
import { reactive, ref, unref, inject, onMounted } from 'vue'
// import { useValidator } from '@/hooks/web/useValidator'
import { FormSchema } from '@/types/form'
import { useEmitt } from '@/hooks/web/useEmitt'
import { getQueryformListApi } from '@/api/ui/queryform'

const { emitter } = useEmitt()

const props = defineProps({
  title: String,
  componentId: String,
  comRef: ref<any>
})

// const { required } = useValidator()

const menuid: string | undefined = inject('menuid')

const schema = reactive<FormSchema[]>([])

// 初始化编辑区信息
onMounted(() => {
  // 这里通过异步接口后端获取返回
  // 获取页签信息, 填充
  console.log(menuid, '页签区菜单id')
  getQueryformListApi(menuid).then((res) => {
    schema.push(...res.data)
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
  console.log(data, 'searchParam')
  // 需要将数据发到业务页面, 业务再把数据发给列表, 填充查询对象
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
