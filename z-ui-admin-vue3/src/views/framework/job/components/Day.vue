<script setup lang="ts">
import { ref, watch, computed, onMounted, inject } from 'vue'
import {
  ElForm,
  ElFormItem,
  ElRadio,
  ElInputNumber,
  ElSelect,
  ElOption,
  ElCheckbox,
  ElCheckboxGroup
} from 'element-plus'
import { useEmitt } from '@/hooks/web/useEmitt'

// 定义组件属性
const props = defineProps({
  check: Number,
  cron: Object // 假设 cron 是一个对象
})

// 定义 emit 钩子
const checkNum = inject('checkNumber') as Function
const { emitter } = useEmitt()

// 初始化响应式状态
const radioValue = ref(1)
const workday = ref(1)
const cycle01 = ref(1)
const cycle02 = ref(2)
const average01 = ref(1)
const average02 = ref(1)
const checkboxList = ref([])
const weekList = ref([
  // 填充 weekList 数据
])

// 监听 radioValue 变化
watch(radioValue, (newVal) => {
  if (newVal !== 2 && props?.cron?.week !== '?') {
    emitter.emit('update', { type: 'week', value: '?' })
  }
  radioChange(newVal)
})

// 单选按钮值变化时的方法
const radioChange = (value) => {
  switch (value) {
    case 1:
      emitter.emit('update', { type: 'day', value: '*' })
      break
    // 其他 case 逻辑保持不变
    case 7:
      emitter.emit('update', { type: 'day', value: checkboxString.value })
      break
  }
}

// 监听周期变化
watch(cycle01, () => {
  if (radioValue.value === 3) {
    radioChange(radioValue.value)
  }
})

// 平均周期变化
watch(average02, () => {
  if (radioValue.value === 4) {
    radioChange(radioValue.value)
  }
})

// 工作日变化
watch(workday, () => {
  if (radioValue.value === 5) {
    workdayChange()
  }
})

// 复选框变化
watch(checkboxList, () => {
  if (radioValue.value === 7) {
    checkboxChange()
  }
})

// 周期两个值变化时的方法
const cycleChange = computed(() => `${cycle01.value}-${cycle02.value}`)

// 平均两个值变化时的方法
const averageChange = computed(() => `${average01.value}/${average02.value}`)

// 工作日检查
const workdayCheck = computed(() => checkNum(workday.value, 1, 31))

// 复选框字符串
const checkboxString = computed(() => checkboxList.value.join(''))

// 组件挂载时的逻辑
onMounted(() => {
  // 可能需要初始化数据或执行其他逻辑
})

// 计算属性
const cycleTotal = computed(
  () =>
    `${checkNum(cycle01.value, 1, 30)}-${checkNum(
      cycle02.value,
      cycle01.value ? cycle01.value + 1 : 2,
      31
    )}`
)
const averageTotal = computed(
  () =>
    `${checkNum(average01.value, 1, 30)}/${checkNum(average02.value, 1, 30 - average01.value || 0)}`
)

// 工作日变化时的方法
const workdayChange = () => {
  if (radioValue.value === 5) {
    emitter.emit('update', { type: 'day', value: `${workdayCheck.value}W` })
  }
}

// 复选框变化时的方法
const checkboxChange = () => {
  if (radioValue.value === 7) {
    emitter.emit('update', { type: 'day', value: checkboxString.value || '*' })
  }
}
</script>

<template>
  <ElForm size="small">
    <ElFormItem>
      <ElRadio v-model="radioValue" :label="1"> 日，允许的通配符[, - * ? / L W] </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio v-model="radioValue" :label="2"> 不指定 </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio v-model="radioValue" :label="3">
        周期从
        <ElInputNumber v-model="cycle01" :min="1" :max="30" /> -
        <ElInputNumber v-model="cycle02" :min="cycle01 ? cycle01 + 1 : 2" :max="31" /> 日
      </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio v-model="radioValue" :label="4">
        从
        <ElInputNumber v-model="average01" :min="1" :max="30" /> 号开始，每
        <ElInputNumber v-model="average02" :min="1" :max="31 - average01 || 1" /> 日执行一次
      </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio v-model="radioValue" :label="5">
        每月
        <ElInputNumber v-model="workday" :min="1" :max="31" /> 号最近的那个工作日
      </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio v-model="radioValue" :label="6"> 本月最后一天 </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio v-model="radioValue" :label="7">
        指定
        <ElSelect
          clearable
          v-model="checkboxList"
          placeholder="可多选"
          multiple
          style="width: 100%"
        >
          <ElOption v-for="item in 31" :key="item" :value="item">{{ item }}</ElOption>
        </ElSelect>
      </ElRadio>
    </ElFormItem>
  </ElForm>
</template>
