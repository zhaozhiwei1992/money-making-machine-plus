<script name="ImageCaptcha" setup lang="ts">
import { getImgCodeApi } from '@/api/login'
import { useConfigGlobal } from '@/hooks/web/useConfigGlobal'
import { useDesign } from '@/hooks/web/useDesign'
import { propTypes } from '@/utils/propTypes'
import { ElInput } from 'element-plus'
import { onMounted, ref, unref, watch } from 'vue'

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

// 图形验证码地址, 异步加载时需要将onMounted打开
const SERVER_URL = import.meta.env.VITE_SERVER_URL
// let captchaImageUrl = SERVER_URL + '/captcha/numCode'
// 使用异步方式加载验证码, vue3中为了触发渲染, 都得加ref
const captchaImageUrl = ref(SERVER_URL + '/captcha/numCode')

// 获取图形验证码, 二进制
const genCode = async () => {
  const res = await getImgCodeApi()
  // console.log('验证码', response.data)
  // 将获取到的验证码图片数据转换成Base64字符串
  // const base64 = btoa(
  //   new Uint8Array(response.data).reduce((data, byte) => data + String.fromCharCode(byte), '')
  // )
  // 在前端显示验证码图片
  // const blob = new Blob([response.data], { type: 'image/jpeg' })
  // captchaImageUrl = URL.createObjectURL(blob)
  // base64方式, 后端返回base64格式图片字符串
  captchaImageUrl.value = 'data:image/png;base64,' + res
}

// 通过axios异步加载时候使用
onMounted(async () => {
  await genCode()
})
</script>

<template>
  <div :class="[prefixCls, `${prefixCls}--${configGlobal?.size}`]">
    <ElInput v-bind="$attrs" v-model="valueRef" :type="textType" :class="`${prefixCls}__input`" />
    <!-- 图形验证码图片 -->
    <div :class="`${prefixCls}__image`">
      <img :src="captchaImageUrl" @click="genCode" />
      <!-- 下述方式, 每次刷新页面就不显示, 随便把button标签放开又可以显示, 刷新又没了 -->
      <!-- <img :src="captchaImageUrl" @click="genCode" /> -->
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
