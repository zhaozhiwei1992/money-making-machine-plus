<template>
  <view class="uni-container">
    <uni-section title="" type="line">
      <view class="uni-padding-wrap uni-common-mt">
        <uni-segmented-control
          :current="current"
          :values="items"
          @clickItem="onClickItem"
        />
      </view>
      <view class="content">
        <view v-if="current === 0">
          <uni-card>
            <uni-forms
              :modelValue="userInfo"
              label-position="top"
              :rules="rules"
              ref="loginForm"
            >
              <uni-forms-item label="用户名" name="username">
                <uni-easyinput
                  type="text"
                  v-model="userInfo.username"
                  placeholder="请输入姓名"
                />
              </uni-forms-item>
              <uni-forms-item label="密码" name="password">
                <uni-easyinput
                  type="text"
                  v-model="userInfo.password"
                  placeholder="请输入姓名"
                />
              </uni-forms-item>
            </uni-forms>
            <button @click="signIn">登录</button>
          </uni-card>
        </view>
        <view v-if="current === 1">
          <button @click="mobileSignIn">手机号一键登录</button>
        </view>
      </view>
    </uni-section>
  </view>
  <!-- 注意，如果需要兼容微信小程序，最好通过setRules方法设置rules规则 -->
</template>

<script setup lang="ts">
import { onMounted, reactive, readonly, ref } from "vue";
import type { UserType } from "@/api/login/types";
import { loginApi, loginByPhoneApi, loginByWxApi } from "@/api/login";

const loginForm = ref<any>(null);

const items = reactive(["用户密码登录", "手机号登录"]);
const current = ref(0);
const userInfo = reactive({
  username: "",
  password: "",
});
const rules = reactive({
  username: {
    rules: [
      {
        required: true,
        errorMessage: "请填写用户名",
      },
    ],
    validateTrigger: "submit",
  },
  password: {
    rules: [
      {
        required: true,
        errorMessage: "请填写密码",
      },
    ],
    validateTrigger: "submit",
  },
});

// 微信登录
const wxSignIn = async () => {
  const code: string = await new Promise((resolve, reject) => {
    // uni.login非异步，所以用Promise包装
    uni.login({
      provider: "weixin",
      onlyAuthorize: true, // 微信登录仅请求授权认证
      success: (res) => {
        resolve(res.code);
      },
      fail: (err) => {
        reject(err);
      },
    });
  });
  // 登录逻辑
  const res = await loginByWxApi(code);
  uni.setStorageSync("token", res.token);
  uni.setStorageSync("username", res.username);
  // 跳转首页, 首页是tabBar需要用switchTab
  uni.switchTab({ url: "/pages/index/index" });
};

// 手机号登录
const mobileSignIn = async () => {
  // 通过微信接口获取手机号
  const phonenumber = "13800000000";
  // 登录逻辑
  const res = await loginByPhoneApi(phonenumber);
  uni.setStorageSync("token", res.token);
  uni.setStorageSync("username", res.username);
  // 跳转首页, 首页是tabBar需要用switchTab
  uni.switchTab({ url: "/pages/index/index" });
};

const signIn = async () => {
  const isValid = await loginForm.value.validate();
  if (isValid) {
    // 通过表单用户密码登录
    const res = await loginApi(userInfo);
    // 返回token 写入缓存
    console.log(res);
    uni.setStorageSync("token", res.token);
    uni.setStorageSync("username", res.username);
    // 跳转首页, 首页是tabBar需要用switchTab
    uni.switchTab({ url: "/pages/index/index" });
  }
};

const onClickItem = (e: any) => {
  if (current !== e.currentIndex) {
    current.value = e.currentIndex;
  }
};

onMounted(() => {});
</script>
