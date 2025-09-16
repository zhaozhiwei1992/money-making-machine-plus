<template>
  <view class="mine-container" :style="{ height: `${windowHeight}px` }">
    <!--顶部个人信息栏-->
    <view class="header-section">
      <view class="flex padding justify-between">
        <view class="flex align-center">
          <view v-if="!avatar" class="cu-avatar xl round bg-white">
            <view class="iconfont icon-people text-gray icon"></view>
          </view>
          <image
            v-if="avatar"
            @click="handleToAvatar"
            :src="avatar"
            class="cu-avatar xl round"
            mode="widthFix"
          >
          </image>
          <view v-if="!name" @click="handleToLogin" class="login-tip">
            点击登录
          </view>
          <view v-if="name" @click="handleToInfo" class="user-info">
            <view class="u_title"> 用户名：{{ name }} </view>
          </view>
        </view>
        <view @click="handleToInfo" class="flex align-center">
          <text>个人信息</text>
          <view class="iconfont icon-right"></view>
        </view>
      </view>
    </view>

    <view class="grid-body">
      <uni-grid :column="4" :showBorder="false" @change="changeGrid">
        <uni-grid-item
          v-for="(item, index) in dynamicGrid"
          :index="index"
          :key="index"
        >
          <view class="grid-item-box">
            <uni-icons :type="item.type" size="30"></uni-icons>
            <text class="text">{{ item.text }}</text>
          </view>
        </uni-grid-item>
      </uni-grid>
    </view>

    <view class="content-section">
      <uni-list>
        <uni-list-item showArrow title="编辑资料" clickable @click="handleToEditInfo" />
        <uni-list-item showArrow title="常见问题" clickable @click="handleHelp" />
        <uni-list-item showArrow title="关于我们" clickable @click="handleAbout" />
        <uni-list-item showArrow title="应用设置" clickable @click="handleToSetting" />
      </uni-list>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, computed, onMounted, ref } from "vue";
const name = ref("");
const version = ref("");

const dynamicGrid = reactive([
  { type: "staff-filled", text: "交流群" },
  { type: "headphones", text: "在线客服" },
  { type: "chat", text: "反馈社区" },
  { type: "star", text: "点赞我们" },
]);

onMounted(() => {
  name.value = uni.getStorageSync("username");
  version.value = uni.getStorageSync("version");
});

const avatar = computed(() => "");
const windowHeight = computed(() => uni.getSystemInfoSync().windowHeight - 50);

const handleToInfo = () => {
  uni.navigateTo({ url: "/pages/mine/info/index" });
};
const handleToEditInfo = (e) => {
  uni.navigateTo({ url: "/pages/mine/info/edit" });
};

const handleToSetting = (e) => {
  uni.navigateTo({ url: "/pages/mine/setting/index" });
};

const handleToLogin = (e) => {
  uni.navigateTo({ url: "/pages/login" });
};

const handleToAvatar = (e) => {
  uni.navigateTo({ url: "/pages/mine/avatar/index" });
};

const handleLogout = () => {
  // this.$modal.confirm('确定注销并退出系统吗？').then(() => {
  //   this.$store.dispatch('LogOut').then(() => {
  //     this.$tab.reLaunch('/pages/index')
  //   })
  // })
};

const changeGrid = (e: any) => {
  let { index } = e.detail;
  if (index === 0) {
    handleJiaoLiuQun();
  } else if (index === 1) {
    handleBuilding();
  } else if (index === 2) {
    //
  } else {
  }
};

const handleHelp = () => {
  uni.navigateTo({ url: "/pages/mine/help/index" });
};

const handleAbout = () => {
  uni.navigateTo({ url: "/pages/mine/about/index" });
};

const handleJiaoLiuQun = () => {
  uni.showToast({ title: "QQ群：①123、②321" });
};

const handleBuilding = () => {
  uni.showToast({
    title: "模块建设中~",
    image: "https://cdn.uviewui.com/uview/demo/toast/error.png",
    duration: 2000,
  });
};
</script>

<style lang="scss">
page {
  background-color: #f5f6f7;
}

.text {
  text-align: center;
  font-size: 26rpx;
  margin-top: 10rpx;
}

.grid-item-box {
  flex: 1;
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px 0;
}

.mine-container {
  width: 100%;
  height: 100%;

  .header-section {
    padding: 15px 15px 45px 15px;
    background-color: #3c96f3;
    color: white;

    .login-tip {
      font-size: 18px;
      margin-left: 10px;
    }

    .cu-avatar {
      border: 2px solid #eaeaea;

      .icon {
        font-size: 40px;
      }
    }

    .user-info {
      margin-left: 15px;

      .u_title {
        font-size: 18px;
        line-height: 30px;
      }
    }
  }

  .content-section {
    position: relative;
    top: -50px;

    .mine-actions {
      margin: 15px 15px;
      padding: 20px 0px;
      border-radius: 8px;
      background-color: white;

      .action-item {
        .icon {
          font-size: 28px;
        }

        .text {
          display: block;
          font-size: 13px;
          margin: 8px 0px;
        }
      }
    }
  }
}
</style>
