<template>
  <view>
    <uni-section title="快速导航" type="line" padding>
			<uni-grid :column="4" :show-border="false" :square="false" :highlight="true" @change="sysManageClick">
				<uni-grid-item v-for="(listItem, index) in list" :index="index" :key="index">
					<view class="grid-item-box" style="background-color: #fff;">
						<uni-icons :type="listItem.name" :size="30" color="#777" />
            <text class="grid-text">{{ listItem.title }}</text>
					</view>
				</uni-grid-item>
			</uni-grid>
		</uni-section>
    <uni-section title="通知公告" type="line" padding>
			<uni-grid :column="4" :show-border="false" :square="false" :highlight="true" @change="sysManageClick">
				<uni-grid-item v-for="(listItem, index) in list" :index="index" :key="index">
					<view class="grid-item-box" style="background-color: #fff;">
						<uni-icons :type="listItem.name" :size="30" color="#777" />
            <text class="grid-text">{{ listItem.title }}</text>
					</view>
				</uni-grid-item>
			</uni-grid>
		</uni-section>
  </view>
</template>

<script>
export default {
  data() {
    return {
      list: [
        {
          name: 'gear',
          title: '系统参数',
        },
        {
          name: 'calendar',
          title: '定时任务',
        },
        {
          name: 'contact',
          title: '用户管理',
        },
      ],
    };
  },
  methods: {
    sysManageClick(ele) {
      // this.$refs.uToast.success(`点击了第${name}个`);
      const index = ele.detail.index;
      // 根据选点击的按钮跳转页面
      // 页面必须是在page.json中注册的
      if (0 === index) {
        uni.navigateTo({ url: '/pages/System/Param/Index' });
      } else if (1 === index) {
        uni.navigateTo({ url: '/pages/System/Task/Index' });
      } else if (2 === index) {
        uni.navigateTo({ url: '/pages/System/User/Index' });
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
