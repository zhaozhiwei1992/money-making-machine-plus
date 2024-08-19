<template>
  <view class="container">
    <view class="example">
      <uni-forms ref="form" :model="user" labelWidth="80px">
        <uni-forms-item label="用户昵称" name="username">
          <uni-easyinput v-model="user.name" placeholder="请输入昵称" />
        </uni-forms-item>
        <uni-forms-item label="手机号码" name="phonenumber">
          <uni-easyinput v-model="user.phonenumber" placeholder="请输入手机号码" />
        </uni-forms-item>
        <uni-forms-item label="邮箱" name="email">
          <uni-easyinput v-model="user.email" placeholder="请输入邮箱" />
        </uni-forms-item>
        <uni-forms-item label="性别" name="sex" required>
          <uni-data-checkbox v-model="user.sex" :localdata="sexs" />
        </uni-forms-item>
      </uni-forms>
      <button type="primary" @click="submit">提交</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { getUserDetLoginApi, saveTableApi } from "@/api/system/user";
import type {UserVO} from "@/api/system/user/types";

const form = ref<any>(null);

const user = ref<UserVO>({
  id: "",
  name: "",
  login: "",
  createdDate: "",
  sex: "",
  phonenumber: "",
  email: "",
  positionIdListStr: "",
  roleIdListStr: ""
});

const sexs = ref([
  {
    text: '男',
    value: "0"
  },
  {
    text: '女',
    value: "1"
  }
]);

const rules = reactive({
  username: [
    {
      required: true,
      message: '用户昵称不能为空'
    }
  ],
  phonenumber: [
    {
      required: true,
      message: '手机号码不能为空'
    },
    {
      pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
      message: '请输入正确的手机号码'
    }
  ],
  email: [
    {
      required: true,
      message: '邮箱地址不能为空'
    },
    {
      type: 'email',
      message: '请输入正确的邮箱地址'
    }
  ]
});

const name:string|any = ref("");

onMounted( async () => {
  name.value = uni.getStorageSync("username");
  // 获取登录用户名
  const response = await getUserDetLoginApi(name.value);
  console.log(response)
  user.value = response;
});

const submit = async () => {
  try {
    const isValid = await form.value.validate();
    if (isValid) {
      await saveTableApi(user.value);
      uni.showToast({
        title: "保存成功",
        image: "https://cdn.uviewui.com/uview/demo/toast/success.png",
        duration: 2000,
      });
    }
  } catch (error) {
    // 处理验证失败的情况
  }
};
</script>

<style lang="scss">
  page {
    background-color: #ffffff;
  }

  .example {
    padding: 15px;
    background-color: #fff;
  }

  .segmented-control {
    margin-bottom: 15px;
  }

  .button-group {
    margin-top: 15px;
    display: flex;
    justify-content: space-around;
  }

  .form-item {
    display: flex;
    align-items: center;
    flex: 1;
  }

  .button {
    display: flex;
    align-items: center;
    height: 35px;
    line-height: 35px;
    margin-left: 10px;
  }
</style>
