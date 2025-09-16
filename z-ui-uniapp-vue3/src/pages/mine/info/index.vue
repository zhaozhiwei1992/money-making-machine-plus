<template>
  <view class="container">
    <uni-list>
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'person-filled'}" title="昵称" :rightText="username" />
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'person-filled'}" title="账号" :rightText="userlogin" />
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'phone-filled'}" title="手机号码" :rightText="phonenumber" />
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'email-filled'}" title="邮箱" :rightText="email" />
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'auth-filled'}" title="岗位" :rightText="positionIdListStr" />
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'staff-filled'}" title="角色" :rightText="roleIdListStr" />
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'calendar-filled'}" title="创建日期" :rightText="createdDate" />
    </uni-list>
  </view>
</template>

<script setup lang="ts">
import {getUserDetLoginApi} from '@/api/system/user';
import { onMounted, reactive, ref } from 'vue';

// rightText对reactive支持有问题,使用ref搞可以凑合用先
const userlogin = ref("x2x");
const username = ref("");
const phonenumber = ref("");
const email = ref("");
const positionIdListStr =  ref("");
const roleIdListStr = ref("");
const createdDate = ref("");
const name = ref("");

onMounted(async () => {
  name.value = uni.getStorageSync("username");
  const user = await getUserDetLoginApi(name.value);
  username.value = user.name;
  userlogin.value = user.login;
  phonenumber.value = user.phonenumber;
  email.value = user.email;
  roleIdListStr.value = user.roleIdListStr;
  positionIdListStr.value = user.positionIdListStr;
  createdDate.value = user.createdDate;
  console.log(user);
})
</script>


<style lang="scss">
  page {
    background-color: #ffffff;
  }
</style>
