<script setup lang="ts">
import { ref, watch, computed, onMounted } from 'vue'
import { ElForm, ElFormItem, ElRadio, ElInputNumber, ElSelect, ElOption } from 'element-plus'
import { useEmitt } from '@/hooks/web/useEmitt'

// 定义响应式状态
const radioValue = ref(2)
const weekday = ref(2)
const cycle01 = ref(2)
const cycle02 = ref(3)
const average01 = ref(1)
const average02 = ref(2)
const checkboxList = ref([])
const weekList = ref([
  { key: 1, value: '星期日' },
  { key: 2, value: '星期一' },
  { key: 3, value: '星期二' },
  { key: 4, value: '星期三' },
  { key: 5, value: '星期四' },
  { key: 6, value: '星期五' },
  { key: 7, value: '星期六' }
])

// 定义 emit 以替代 this.$emit
const { emitter } = useEmitt()

// 定义计算属性
const cycleTotal = computed(() => `${cycle01.value}-${cycle02.value}`)
const averageTotal = computed(() => `${average02.value}#${average01.value}`)
const checkboxString = computed(() =>
  checkboxList.value.length ? checkboxList.value.join(',') : '*'
)

// 监听响应式状态变化
watch(radioValue, (newVal) => {
  if (newVal !== 2) {
    emitter.emit('update', { type: 'day', value: '?' })
  }
  radioChange(newVal)
})

// 单选按钮值变化时的方法
const radioChange = (value) => {
  switch (value) {
    case 1:
      emitter.emit('update', { type: 'week', value: '*' })
      break
    case 2:
      emitter.emit('update', { type: 'week', value: '?' })
      break
    case 3:
      emitter.emit('update', { type: 'week', value: cycleTotal.value })
      break
    case 4:
      emitter.emit('update', { type: 'week', value: averageTotal.value })
      break
    case 5:
      emitter.emit('update', { type: 'week', value: `${weekday.value}L` })
      break
    case 6:
      emitter.emit('update', { type: 'week', value: checkboxString.value })
      break
  }
}

// 监听周期变化
watch(cycle01, () => {
  if (radioValue.value === 3) {
    radioChange(radioValue.value)
  }
})

// 监听平均周期变化
watch(average02, () => {
  if (radioValue.value === 4) {
    radioChange(radioValue.value)
  }
})

// 监听复选框变化
watch(checkboxList, () => {
  if (radioValue.value === 6) {
    radioChange(radioValue.value)
  }
})

// 组件挂载时的逻辑
onMounted(() => {
  // 初始化操作，如果有的话
})
</script>

<template>
  <ElForm size="small">
    <ElFormItem>
      <ElRadio v-model="radioValue" :label="1"> 周，允许的通配符[, - * ? / L #] </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio v-model="radioValue" :label="2"> 不指定 </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio v-model="radioValue" :label="3">
        周期从星期
        <ElSelect clearable v-model="cycle01">
          <ElOption
            v-for="(item, index) of weekList"
            :key="index"
            :label="item.value"
            :value="item.key"
            :disabled="item.key === 1"
            item.value
            >{{ item.value }}</ElOption
          >
        </ElSelect>
        -
        <ElSelect clearable v-model="cycle02">
          <ElOption
            v-for="(item, index) of weekList"
            :key="index"
            :label="item.value"
            :value="item.key"
            :disabled="item.key < cycle01 && item.key !== 1"
            >{{ item.value }}</ElOption
          >
        </ElSelect>
      </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio v-model="radioValue" :label="4">
        第
        <ElInputNumber v-model="average01" :min="1" :max="4" /> 周的星期
        <ElSelect clearable v-model="average02">
          <ElOption
            v-for="(item, index) of weekList"
            :key="index"
            :label="item.value"
            :value="item.key"
            >{{ item.value }}</ElOption
          >
        </ElSelect>
      </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio v-model="radioValue" :label="5">
        本月最后一个星期
        <ElSelect clearable v-model="weekday">
          <ElOption
            v-for="(item, index) of weekList"
            :key="index"
            :label="item.value"
            :value="item.key"
            >{{ item.value }}</ElOption
          >
        </ElSelect>
      </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio v-model="radioValue" :label="6">
        指定
        <ElSelect
          clearable
          v-model="checkboxList"
          placeholder="可多选"
          multiple
          style="width: 100%"
        >
          <ElOption
            v-for="(item, index) of weekList"
            :key="index"
            :label="item.value"
            :value="String(item.key)"
          >
            {{ item.value }}</ElOption
          >
        </ElSelect>
      </ElRadio>
    </ElFormItem>
  </ElForm>
</template>
