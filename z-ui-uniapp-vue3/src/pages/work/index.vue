<template>
  <view class="work-container">
    <!-- 轮播图 -->
    <uni-swiper-dot
      class="uni-swiper-dot-box"
      :info="data"
      :current="current"
      field="content"
    >
      <swiper
        class="swiper-box"
        :current="swiperDotIndex"
        @change="changeSwiper"
      >
        <swiper-item v-for="(item, index) in data" :key="index">
          <view class="swiper-item" @click="clickBannerItem(item)">
            <image :src="item.image" mode="aspectFill" :draggable="false" />
          </view>
        </swiper-item>
      </swiper>
    </uni-swiper-dot>

    <!-- 宫格组件 -->
    <uni-section title="系统管理" type="line"></uni-section>
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
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";

const dynamicGrid = reactive([
  {
    type: "person-filled",
    text: "用户管理",
  },
  {
    type: "staff-filled",
    text: "角色管理",
  },
  {
    type: "color",
    text: "菜单管理",
  },
  {
    type: "settings-filled",
    text: "部门管理",
  },
  {
    type: "heart-filled",
    text: "岗位管理",
  },
  {
    type: "bars",
    text: "字典管理",
  },
  {
    type: "gear-filled",
    text: "参数设置",
  },
  {
    type: "chat-filled",
    text: "通知公告",
  },
  {
    type: "wallet-filled",
    text: "日志管理",
  },
]);

const data = reactive([
  { image: "/static/images/banner/banner02.jpg" },
  { image: "/static/images/banner/banner03.jpg" },
]);
const current = ref(0);
const swiperDotIndex = ref(0);

function clickBannerItem(item) {
  console.info(item);
}

function changeSwiper(e: any) {
  current.value = e.detail.current;
}

function changeGrid() {
  uni.showToast({
    title: "模块建设中~",
    image: "https://cdn.uviewui.com/uview/demo/toast/error.png",
    duration: 2000,
  });
}

// 如果需要在组件挂载后执行某些操作，可以使用 onMounted 钩子
onMounted(() => {
  // 组件挂载后的代码
});
</script>

<style lang="scss">
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

.swiper {
  height: 300rpx;
}

.swiper-box {
  height: 150px;
}

.swiper-item {
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #fff;
  height: 300rpx;
  line-height: 300rpx;
}

@media screen and (min-width: 500px) {
  .uni-swiper-dot-box {
    width: 400px;
    /* #ifndef APP-NVUE */
    margin: 0 auto;
    /* #endif */
    margin-top: 8px;
  }

  .image {
    width: 100%;
  }
}
</style>
