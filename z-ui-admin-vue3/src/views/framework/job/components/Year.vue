<script setup lang="ts">
import { ref, watch, computed, onMounted, inject } from 'vue'
import { ElForm, ElFormItem, ElRadio, ElInputNumber, ElSelect, ElOption } from 'element-plus'
import { useEmitt } from '@/hooks/web/useEmitt'

// 定义响应式属性
const fullYear = ref(0)
const radioValue = ref(1)
const cycle01 = ref(0)
const cycle02 = ref(0)
const average01 = ref(0)
const average02 = ref(1)
const checkboxList = ref([])
const checkNum = inject('checkNumber') as Function

// 定义计算属性
const cycleTotal = computed(() => {
  // 根据 cycle01 和 cycle02 的值计算周期
  const a = checkNum(cycle01.value, fullYear.value, 2098)
  const b = checkNum(cycle02.value, cycle01.value ? cycle01.value + 1 : fullYear.value + 1, 2099)
  return a + '-' + b
})

const averageTotal = computed(() => {
  // 根据 average01 和 average02 的值计算平均周期
  const a = checkNum(average01.value, fullYear, 2098)
  const b = checkNum(average02.value, 1, 2099 - average01.value || fullYear)
  return a + '/' + b
})

const checkboxString = computed(() => {
  // 根据 checkboxList 计算字符串
  let str = checkboxList.value.join()
  return str
})

const { emitter } = useEmitt()

// 定义方法
const radioChange = () => {
  // 根据 radioValue 的变化更新外部属性
  switch (radioValue.value) {
    case 1:
      emitter.emit('update', { type: 'year', value: '' })
      break
    case 2:
      emitter.emit('update', { type: 'year', value: '*' })
      break
    case 3:
      emitter.emit('update', { type: 'year', value: cycleTotal })
      break
    case 4:
      emitter.emit('update', { type: 'year', value: averageTotal })
      break
    case 5:
      emitter.emit('update', { type: 'year', value: checkboxString })
      break
  }
}

const cycleChange = () => {
  // 周期值变化时的逻辑
  if (radioValue.value === 3) {
    emitter.emit('update', { type: 'year', value: cycleTotal })
  }
}

const averageChange = () => {
  // 平均值变化时的逻辑
  if (radioValue.value === 4) {
    emitter.emit('update', { type: 'year', value: averageTotal })
  }
}

const checkboxChange = () => {
  // 复选框变化时的逻辑
  if (radioValue.value == 5) {
    emitter.emit('update', { type: 'year', value: checkboxString })
  }
}

// 使用 watch 来监听响应式属性的变化
watch(radioValue, radioChange)
watch(cycleTotal, cycleChange)
watch(averageTotal, averageChange)
watch(checkboxString, checkboxChange)

// 组件挂载时的逻辑
onMounted(() => {
  fullYear.value = Number(new Date().getFullYear())
  cycle01.value = fullYear.value
  average01.value = fullYear.value
})
</script>
<template>
  <ElForm size="small">
    <ElFormItem>
      <ElRadio :label="1" v-model="radioValue"> 不填，允许的通配符[, - * /] </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio :label="2" v-model="radioValue"> 每年 </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio :label="3" v-model="radioValue">
        周期从
        <ElInputNumber v-model="cycle01" :min="fullYear" :max="2098" /> -
        <ElInputNumber v-model="cycle02" :min="cycle01 ? cycle01 + 1 : fullYear + 1" :max="2099" />
      </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio :label="4" v-model="radioValue">
        从
        <ElInputNumber v-model="average01" :min="fullYear" :max="2098" /> 年开始，每
        <ElInputNumber v-model="average02" :min="1" :max="2099 - average01 || fullYear" />
        年执行一次
      </ElRadio>
    </ElFormItem>

    <ElFormItem>
      <ElRadio :label="5" v-model="radioValue">
        指定
        <ElSelect clearable v-model="checkboxList" placeholder="可多选" multiple>
          <ElOption
            v-for="item in 9"
            :key="item"
            :value="item - 1 + fullYear"
            :label="item - 1 + fullYear"
          />
        </ElSelect>
      </ElRadio>
    </ElFormItem>
  </ElForm>
</template>
