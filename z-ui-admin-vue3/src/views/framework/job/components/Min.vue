<script setup lang="ts">
import { ref, watch, computed, defineProps, defineEmits, inject } from 'vue'
import {
  ElRadio,
  ElInputNumber,
  ElSelect,
  ElOption,
  ElCheckboxGroup,
  ElCheckbox
} from 'element-plus'
import { useEmitt } from '@/hooks/web/useEmitt'

// 定义 emit 钩子
const checkNum = inject('checkNumber') as Function
const { emitter } = useEmitt()

// 定义组件的 props
const props = defineProps({
  check: Number,
  cron: Object // 假设 cron 是一个对象，具体结构根据实际情况定义
})

// 初始化响应式状态
const radioValue = ref(1)
const cycle01 = ref(1)
const cycle02 = ref(2)
const average01 = ref(0)
const average02 = ref(1)
const checkboxList = ref([])

// 计算属性
const cycleTotal = computed(
  () => `${checkNum(cycle01.value, 0, 58)}-${checkNum(cycle02.value, cycle01.value + 1, 59)}`
)
const averageTotal = computed(
  () =>
    `${checkNum(average01.value, 0, 58)}/${checkNum(average02.value, 1, 59 - average01.value || 0)}`
)
const checkboxString = computed(() => checkboxList.value.join('') || '*')

// 监听响应式状态变化
watch(radioValue, (newVal) => {
  radioChange(newVal)
})

// 定义方法
const radioChange = (value) => {
  switch (value) {
    case 1:
      emitter.emit('update', { type: 'min', value: '*' })
      break
    case 2:
      emitter.emit('update', { type: 'min', value: cycleTotal.value })
      break
    case 3:
      emitter.emit('update', { type: 'min', value: averageTotal.value })
      break
    case 4:
      emitter.emit('update', { type: 'min', value: checkboxString.value })
      break
  }
}

// 监听周期变化
watch(cycle01, () => {
  if (radioValue.value === 2) {
    radioChange(radioValue.value)
  }
})

// 平均周期变化
watch(average02, () => {
  if (radioValue.value === 3) {
    radioChange(radioValue.value)
  }
})

// 复选框变化
watch(checkboxList, () => {
  if (radioValue.value === 4) {
    checkboxChange()
  }
})

// 定义周期变化时的方法
const cycleChange = () => {
  if (radioValue.value === 2) {
    emitter.emit('update', { type: 'min', value: cycleTotal.value })
  }
}

// 定义平均周期变化时的方法
const averageChange = () => {
  if (radioValue.value === 3) {
    emitter.emit('update', { type: 'min', value: averageTotal.value })
  }
}

// 定义复选框变化时的方法
const checkboxChange = () => {
  if (radioValue.value === 4) {
    emitter.emit('update', { type: 'min', value: checkboxString.value })
  }
}
</script>

<template>
  <ElForm size="small">
    <ElFormItem>
      <ElRadio v-model="radioValue" :label="1"> 分钟，允许的通配符[, - * /] </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio v-model="radioValue" :label="2">
        周期从
        <ElInputNumber v-model="cycle01" :min="0" :max="58" /> -
        <ElInputNumber v-model="cycle02" :min="cycle01 ? cycle01 + 1 : 1" :max="59" /> 分钟
      </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio v-model="radioValue" :label="3">
        从
        <ElInputNumber v-model="average01" :min="0" :max="58" /> 分钟开始，每
        <ElInputNumber v-model="average02" :min="1" :max="59 - average01 || 0" /> 分钟执行一次
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
