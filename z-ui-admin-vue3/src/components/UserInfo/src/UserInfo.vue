<script setup lang="ts">
import { loginOutApi } from '@/api/login'
import { Dialog } from '@/components/Dialog'
import { useCache } from '@/hooks/web/useCache'
import { useDesign } from '@/hooks/web/useDesign'
import { useI18n } from '@/hooks/web/useI18n'
import { resetRouter } from '@/router'
import { useAppStore } from '@/store/modules/app'
import { useTagsViewStore } from '@/store/modules/tagsView'
import ModPersonalInfoVue from '@/views/system/User/components/ModPersonalInfo.vue'
import ResetPassword from '@/views/system/User/components/ResetPassword.vue'
import { ElButton, ElDropdown, ElDropdownItem, ElDropdownMenu } from 'element-plus'
import { onMounted, ref, unref } from 'vue'
import { useRouter } from 'vue-router'

const tagsViewStore = useTagsViewStore()

const { getPrefixCls } = useDesign()

const prefixCls = getPrefixCls('user-info')

const { t } = useI18n()

const { wsCache } = useCache()

const appStore = useAppStore()
const userName = wsCache.get(appStore.getUserInfo).username

const { replace } = useRouter()

const loginOut = async () => {
  const res = await loginOutApi().catch(() => {})
  if (res) {
    wsCache.clear()
    tagsViewStore.delAllViews()
    resetRouter() // 重置静态路由表
    replace('/login')
  }
  // ElMessageBox.confirm(t('common.loginOutMessage'), t('common.reminder'), {
  //   confirmButtonText: t('common.ok'),
  //   cancelButtonText: t('common.cancel'),
  //   type: 'warning'
  // })
  //   .then(async () => {
  //     const res = await loginOutApi().catch(() => {})
  //     if (res) {
  //       wsCache.clear()
  //       tagsViewStore.delAllViews()
  //       resetRouter() // 重置静态路由表
  //       replace('/login')
  //     }
  //   })
  //   .catch(() => {})
}

const writeRef = ref<ComponentRef<typeof ResetPassword>>()

const dialogVisible = ref(false)
const dialogTitle = ref('重置密码')

// 重置密码
const toResetPassword = () => {
  // 跳转密码重置页面
  dialogVisible.value = true
}

const save = () => {
  const write = unref(writeRef)
  write?.save()
  dialogVisible.value = false
}

const personalDialogVisible = ref(false)
const personalDialogTitle = ref('个人信息修改')
const toModPersonalInfo = () => {
  // 跳转个人中心页面
  personalDialogVisible.value = true
}

const personalWriteRef = ref<ComponentRef<typeof ModPersonalInfoVue>>()
const savePersonal = () => {
  const write = unref(personalWriteRef)
  write?.save()
  personalDialogVisible.value = false
}

// 头像
const avatarImageUrl = ref('@/assets/imgs/avatar.jpg')

onMounted(() => {
  // captchaImageUrl.value = 'data:image/png;base64,' + response.data
  avatarImageUrl.value = 'data:image/png;base64,' + wsCache.get(appStore.getUserInfo).avatar
})
</script>

<template>
  <ElDropdown :class="prefixCls" trigger="click">
    <div class="flex items-center">
      <img :src="avatarImageUrl" alt="" class="w-[calc(var(--logo-height)-25px)] rounded-[50%]" />
      <span class="<lg:hidden text-14px pl-[5px] text-[var(--top-header-text-color)]">
        {{ userName }}
      </span>
    </div>
    <template #dropdown>
      <ElDropdownMenu>
        <ElDropdownItem>
          <div @click="toModPersonalInfo">{{ t('common.modPersonalInfo') }}</div>
        </ElDropdownItem>
        <ElDropdownItem>
          <div @click="toResetPassword">{{ t('common.resetPassword') }}</div>
        </ElDropdownItem>
        <ElDropdownItem divided>
          <div @click="loginOut">{{ t('common.loginOut') }}</div>
        </ElDropdownItem>
      </ElDropdownMenu>
    </template>
  </ElDropdown>

  <Dialog v-model="personalDialogVisible" :title="personalDialogTitle">
    <ModPersonalInfoVue ref="personalWriteRef" />

    <template #footer>
      <ElButton type="primary" @click="savePersonal">
        {{ t('exampleDemo.save') }}
      </ElButton>
      <ElButton @click="dialogPersonalVisible = false">{{ t('dialogDemo.close') }}</ElButton>
    </template>
  </Dialog>

  <Dialog v-model="dialogVisible" :title="dialogTitle">
    <ResetPassword ref="writeRef" />

    <template #footer>
      <ElButton type="primary" @click="save">
        {{ t('exampleDemo.save') }}
      </ElButton>
      <ElButton @click="dialogVisible = false">{{ t('dialogDemo.close') }}</ElButton>
    </template>
  </Dialog>
</template>
