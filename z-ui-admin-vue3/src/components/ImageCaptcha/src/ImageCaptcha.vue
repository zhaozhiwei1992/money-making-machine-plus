<script name="ImageCaptcha" setup lang="ts">
import { ref, unref, watch } from 'vue'
import { ElInput } from 'element-plus'
import { propTypes } from '@/utils/propTypes'
import { useConfigGlobal } from '@/hooks/web/useConfigGlobal'
import { useDesign } from '@/hooks/web/useDesign'

const { getPrefixCls } = useDesign()

const prefixCls = getPrefixCls('image-captcha')

const props = defineProps({
  modelValue: propTypes.string.def('')
})

watch(
  () => props.modelValue,
  (val: string) => {
    if (val === unref(valueRef)) return
    valueRef.value = val
  }
)

const { configGlobal } = useConfigGlobal()

const emit = defineEmits(['update:modelValue'])

// 设置input的type属性
const textType = 'text'

// 输入框的值
const valueRef = ref(props.modelValue)

// 监听
watch(
  () => valueRef.value,
  (val: string) => {
    emit('update:modelValue', val)
  }
)

const SERVER_URL = import.meta.env.VITE_SERVER_URL

// 图形验证码地址
const codeUrl = SERVER_URL + '/captcha/numCode'

// 获取图形验证码, 二进制
const genCode = () => {
  return 'xx'
}
</script>

<template>
  <div :class="[prefixCls, `${prefixCls}--${configGlobal?.size}`]">
    <ElInput v-bind="$attrs" v-model="valueRef" :type="textType" :class="`${prefixCls}__input`" />
    <!-- 图形验证码图片 -->
    <div :class="`${prefixCls}__image`">
      <img :src="codeUrl" @click="genCode" />
    </div>
  </div>
</template>

<style lang="less" scoped>
@prefix-cls: ~'@{namespace}-image-captcha';

.@{prefix-cls} {
  :deep(.@{elNamespace}-input__clear) {
    margin-left: 5px;
  }

  &__input {
    width: 60% !important;
  }

  &__image {
    width: 34%;
    float: right;
  }
}
</style>
