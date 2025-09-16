<template>
  <view class="uni-container">
    <uni-section title="" type="line">
      <view class="uni-padding-wrap uni-common-mt">
        <uni-segmented-control :current="current" :values="items" @clickItem="onClickItem" />
      </view>
      <view class="content">
        <view v-if="current === 0">
          <uni-card>
            <uni-forms :modelValue="userInfo" label-position="top" :rules="rules" ref="loginForm">
              <uni-forms-item label="用户名" name="username">
                <uni-easyinput type="text" v-model="userInfo.username" placeholder="请输入姓名" />
              </uni-forms-item>
              <uni-forms-item label="密码" name="password">
                <uni-easyinput type="text" v-model="userInfo.password" placeholder="请输入姓名" />
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

<script>
import { loginApi } from '@/api/login';
export default {
  data() {
    return {
      items: ['用户密码登录', '手机号登录'],
      current: 0,
      userInfo: {
        username: '',
        password: '',
      },
      rules: {
        // 对username字段进行必填验证
        username: {
          // username 字段的校验规则
          rules: [
            {
              required: true,
              errorMessage: '请填写用户名',
            }
          ],
          validateTrigger: 'submit'
        },
        password: {
          rules: [
            {
              required: true,
              errorMessage: '请填写密码',
            }
          ],
          validateTrigger: 'submit'
        }
      }
    };
  },
  methods: {
    mobileSignIn: function () {
      // 通过微信接口获取手机号
      // 登录
    },
    signIn: function () {
      // 通过表单用户密码登录
      loginApi(this.userInfo).then(res => {
        // 返回token 写入缓存
        uni.setStorageSync("token", res.data.token);
        uni.setStorageSync("username", res.data.username);
        // 跳转首页, 首页是tabBar需要用switchTab
        uni.switchTab({ url: '/pages/index/index' });
      });
    },
    onClickItem(e) {
      if (this.current != e.currentIndex) {
        this.current = e.currentIndex;
      }
    }
  },
  onReady() {
    //如果需要兼容微信小程序，并且校验规则中含有方法等，只能通过setRules方法设置规则。
    this.$refs.loginForm.setRules(this.rules);
  },
};
</script>
