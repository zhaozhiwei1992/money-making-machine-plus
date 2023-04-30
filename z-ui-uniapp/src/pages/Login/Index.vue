<template>
  <view>
    <!-- 注意，如果需要兼容微信小程序，最好通过setRules方法设置rules规则 -->
    <u-form labelPosition="left" :rules="rules" ref="loginForm">
      <u-form-item label="用户名" prop="userInfo.username" borderBottom>
        <u-input v-model="userInfo.username" border="none" placeholder="用户名"></u-input>
      </u-form-item>
      <u-form-item label="密码" prop="userInfo.password" borderBottom>
        <u-input v-model="userInfo.password" border="none" placeholder="密码"></u-input>
      </u-form-item>
      <view>
        <u-row>
          <u-button type="primary" text="登录" @click="signIn"></u-button>
        </u-row>
        <u-row>
          <u-text mode="link" text="找回密码" href="https://www.uviewui.com" ></u-text> | <u-text mode="link" text="注册账号" href="https://www.uviewui.com" ></u-text>
        </u-row>
      </view>
      <view>
        <!-- 其它方式 -->
        <u-row>
          <span>其它方式登录</span>
        </u-row>
        <u-row>
          <!-- 这里搞两图片, img -->
          qq | 微信
        </u-row>
      </view>
    </u-form>
  </view>
</template>

<script>
import login from '@/api/login';
export default {
  data() {
    return {
      userInfo: {
        username: '',
        password: '',
      },
      rules: {
        'userInfo.username': {
          type: 'string',
          required: true,
          message: '请填写用户名',
          trigger: ['blur', 'change'],
        },
        'userInfo.password': {
          type: 'string',
          required: true,
          message: '请输入密码',
          trigger: ['blur', 'change'],
        },
      }
    };
  },
  methods: {
    signIn: function(){
      // 通过表单用户密码登录
      const res = login.loginApi(this.userInfo);
      // 返回token 写入缓存
      uni.setStorageSync("token", res.data.token);
      // 跳转首页
      uni.navigateTo({ url: '/pages/index/index' })
    }
  },
  onReady() {
    //如果需要兼容微信小程序，并且校验规则中含有方法等，只能通过setRules方法设置规则。
    this.$refs.loginForm.setRules(this.rules);
  },
};
</script>
