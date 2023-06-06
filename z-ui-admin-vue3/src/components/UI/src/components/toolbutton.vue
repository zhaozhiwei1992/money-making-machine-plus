<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ElButton } from 'element-plus'
import { ref, onMounted, inject } from 'vue'
import { useEmitt } from '@/hooks/web/useEmitt'
import { getToolButtonListApi } from '@/api/ui/toolbutton'

const { emitter } = useEmitt()

// 设置props
const props = defineProps({
  title: String,
  componentId: String,
  comRef: ref<any>
})

interface ButtonType {
  id: number
  name: string
  action: string
  type: any
  size: any
}

const buttons = ref<ButtonType[]>([])

// 模拟后端返回的tab信息
// const buttonsSchema = reactive<ButtonType[]>([
//   {
//     id: 1,
//     name: '新增',
//     action: 'add',
//     type: 'primary',
//     size: 'default'
//   },
//   {
//     id: 2,
//     name: '删除',
//     action: 'del',
//     type: 'danger',
//     size: 'default'
//   }
// ])

// 初始化按钮
onMounted(() => {
  // 父页面传入菜单id, 这里根据菜单id自己去后台获取编辑区信息
  const menuId: string | undefined = inject('menuId')
  console.log('父级传入menuid为: ' + menuId)
  // 获取按钮信息, 填充
  getToolButtonListApi(menuId).then((res) => {
    buttons.value.push(...res.data)
  })
  // 模拟测试
  // buttons.value.push(...buttonsSchema)
})

//动态绑定操作按钮的点击事件, 父页面实现方法
const handleClick = (item) => {
  // 子组件里用$emit向父组件触发一个事件，父组件监听这个事件就行了。
  emitter.emit('buttonClick', { componentId: props.componentId, item: item })
}
</script>
<template>
  <ContentWrap>
    <el-button
      ref="toolbutton"
      :type="item.type"
      plain
      v-for="(item, i) in buttons"
      :key="i"
      @click="handleClick(item)"
      :size="item.size"
      class="right-btn"
      >{{ item.name }}</el-button
    >
  </ContentWrap>
</template>
