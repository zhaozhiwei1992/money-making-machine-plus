<template>
  <view class="uni-container">
    <uni-section :title="username" type="line">
      <uni-list>
        <uni-list-item title="个人信息" showArrow link="redirectTo" to="./chat" @click="onClick" />
      </uni-list>
    </uni-section>
    <uni-section title="" type="line">
      <uni-list>
        <uni-list-item title="账号设置" showArrow link="redirectTo" to="./chat" @click="onClick" />
        <uni-list-item title="消息通知" showArrow link="redirectTo" to="./chat" @click="onClick" />
      </uni-list>
    </uni-section>
  </view>
</template>


<script>
export default {
  data() {
    return {
      username: '',
    }
  },
	onLoad() {
		// 页面跳转进来后可以获取url参数
    this.username = uni.getStorageSync("username");
	},
}
</script>