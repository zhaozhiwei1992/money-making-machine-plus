<script setup lang="ts">
import { personalInfoModApi } from '@/api/system/user'
import { uploadAvatarApi } from '@/api/system/upload'
import { ElMessage, ElInput, ElForm, ElFormItem, ElUpload, ElIcon } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { ref } from 'vue'

const save = async () => {
  personalInfoModApi(formData.value).then(() => {
    ElMessage.success('修改成功')
  })
}

const formData = ref({
  name: '',
  avatar: 0,
  email: '',
  phoneNumber: ''
})
const form = ref(null)

const handleUpload = async (request) => {
  const form = new FormData()
  form.append('f', request.file)
  const res = await uploadAvatarApi(form)
  console.log('头像上传', res)
  formData.value.avatar = res.id
  ElMessage.success('头像上传成功！')
}

const beforeUpload = (file) => {
  const isJPG = file.type === 'image/jpeg'
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

defineExpose({
  save: save
})
</script>

<template>
  <ElForm ref="form" :model="formData" :rules="rules" label-width="100px">
    <ElFormItem label="头像" prop="f">
      <ElUpload
        class="avatar-uploader"
        action="#"
        :show-file-list="false"
        :http-request="handleUpload"
        :before-upload="beforeUpload"
      >
        <img v-if="formData.avatar" :src="formData.avatar" class="avatar" />
        <ElIcon v-else class="avatar-uploader-icon">
          <Plus />
        </ElIcon>
      </ElUpload>
    </ElFormItem>
    <ElFormItem label="中文名" prop="name">
      <ElInput v-model="formData.name" />
    </ElFormItem>
    <ElFormItem label="手机号" prop="phoneNumber">
      <ElInput v-model="formData.phoneNumber" />
    </ElFormItem>
    <ElFormItem label="邮箱" prop="email">
      <ElInput v-model="formData.email" />
    </ElFormItem>
  </ElForm>
</template>
