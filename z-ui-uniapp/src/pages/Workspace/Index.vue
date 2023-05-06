<template>
  <view>
    <view>
      <span>待办任务列表</span>
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
      <span>已完成任务列表</span>
      <u-grid :border="false" col="4" @click="reportClick">
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
      <span>统计报表</span>
      <u-grid :border="false" col="4" @click="reportClick">
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
