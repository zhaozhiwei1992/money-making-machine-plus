<template>
  <view>
    <view>
      <span>快速导航</span>
      <u-grid :border="false" col="4" @click="sysManageClick">
        <u-grid-item v-for="(listItem, listIndex) in list" :key="listIndex">
          <u-icon
            :customStyle="{ paddingTop: 20 + 'rpx' }"
            :name="listItem.name"
            :size="22"
          ></u-icon>
          <text class="grid-text">{{ listItem.title }}</text>
        </u-grid-item>
      </u-grid>
      <u-toast ref="uToast" />
    </view>
    <view>
      <span>通知公告</span>
    </view>
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
      // this.$refs.uToast.success(`点击了第${name}个`);
      // 根据选点击的按钮跳转页面
      // 页面必须是在page.json中注册的
      if(0 === name){
        uni.navigateTo({url:'/pages/System/Param/Index'});
      }else if(1 === name){
        uni.navigateTo({url:'/pages/System/Task/Index'});
      }else if(2 === name){
        uni.navigateTo({url:'/pages/System/Role/Index'});
      }else if(3 === name){
        uni.navigateTo({url:'/pages/System/User/Index'});
      }
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
