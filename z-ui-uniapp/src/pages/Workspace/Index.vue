<template>
  <view class="container">
    <u-toast ref="uToast" />
    <uni-section title="待办事项" type="line" padding>
      <text class="grid-text">这里放待办任务, list形式可点击跳转</text>
    </uni-section>
    <uni-section title="统计报表" type="line" padding>
      <text class="grid-text">这里放一些简单的报表, list形式可点击跳转</text>
    </uni-section>
  </view>
</template>

<script>
export default {
  data() {
    return {
      list: [
        {
          name: 'photo',
          title: '系统参数',
        },
        {
          name: 'lock',
          title: '定时任务',
        },
        {
          name: 'star',
          title: '角色管理',
        },
        {
          name: 'hourglass',
          title: '用户管理',
        },
      ],
    };
  },
  methods: {
    sysManageClick(name) {
      this.$refs.uToast.success(`点击了第${name}个`);
    },
    reportClick(name) {
      this.$refs.uToast.success(`点击了第${name}个`);
    },
  },
};
</script>

<style lang="scss">
.grid-text {
  font-size: 14px;
  color: #909399;
  padding: 10rpx 0 20rpx 0rpx;
  /* #ifndef APP-PLUS */
  box-sizing: border-box;
  /* #endif */
}
</style>
