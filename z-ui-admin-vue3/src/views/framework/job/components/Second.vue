<script setup lang="ts">
import { ref, watch, computed, onMounted, defineEmits, inject } from 'vue'
import { ElForm, ElFormItem, ElRadio, ElSelect, ElOption, ElInputNumber } from 'element-plus'
import { useEmitt } from '@/hooks/web/useEmitt'

const checkNum = inject('checkNumber') as Function

// 定义响应式状态
const radioValue: any = ref(1)
const cycle01 = ref(1)
const cycle02 = ref(2)
const average01 = ref(0)
const average02 = ref(1)
const checkboxList = ref([])

const { emitter } = useEmitt()

// 监听 radioValue 变化
watch(radioValue, (newVal) => {
  radioChange(newVal)
})

// 定义其它 watch 监听器
// ...

// 单选按钮值变化时的方法
const radioChange = (value) => {
  switch (value) {
    // 其他 case 逻辑保持不变
    case 1:
      emitter.emit('update', { type: 'second', value: '*' })
      break
    case 2:
      emitter.emit('update', { type: 'second', value: cycleTotal })
      break
    case 3:
      emitter.emit('update', { type: 'second', value: averageTotal })
      break
    case 4:
      emitter.emit('update', { type: 'second', value: checkboxString })
      break
  }
}

// 监听周期变化
watch(radioValue, () => {
  if (radioValue.value === 2) {
    radioChange(radioValue.value)
  }
})

// 监听平均周期变化
watch(average02, () => {
  if (radioValue.value === 3) {
    radioChange(radioValue.value)
  }
})

// 监听复选框变化
watch(checkboxList, () => {
  if (radioValue.value === 4) {
    radioChange(radioValue.value)
  }
})

// 使用 computed 属性替代 computed 属性
const cycleTotal = computed(() => {
  const a = checkNum(cycle01.value, 0, 58)
  const b = checkNum(cycle02.value, cycle01.value ? cycle01.value + 1 : 1, 59)
  return a + '-' + b
})
const averageTotal = computed(() => {
  const a = checkNum(average01, 0, 58)
  const b = checkNum(average02, 1, 59 - average01.value || 0)
  return a + '/' + b
})
const checkboxString = computed(() => checkboxList.value.join('') || '*')

// 组件挂载时的逻辑
onMounted(() => {
  // 初始化操作，如果有的话
})

// 使用 props 替代 props 定义
const props = defineProps({
  check: Number,
  radioParent: Number
})

// 使用 watch 监听 radioParent 的变化
watch(
  () => props.radioParent,
  (newVal) => {
    radioValue.value = newVal
  }
)
</script>

<template>
  <ElForm size="small">
    <ElFormItem>
      <ElRadio v-model="radioValue" :label="1"> 秒，允许的通配符[, - * /] </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio v-model="radioValue" :label="2">
        周期从
        <ElInputNumber v-model="cycle01" :min="0" :max="58" /> -
        <ElInputNumber v-model="cycle02" :min="cycle01 ? cycle01 + 1 : 1" :max="59" /> 秒
      </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio v-model="radioValue" :label="3">
        从
        <ElInputNumber v-model="average01" :min="0" :max="58" /> 秒开始，每
        <ElInputNumber v-model="average02" :min="1" :max="59 - average01 || 0" /> 秒执行一次
      </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio v-model="radioValue" :label="4">
        指定
        <ElSelect
          clearable
          v-model="checkboxList"
          placeholder="可多选"
          multiple
          style="width: 100%"
        >
          <ElOption v-for="item in 60" :key="item" :value="item - 1">{{ item - 1 }}</ElOption>
        </ElSelect>
      </ElRadio>
    </ElFormItem>
  </ElForm>
</template>
