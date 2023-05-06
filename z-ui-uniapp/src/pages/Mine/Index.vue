<template>
  <view>
    <u-row>
      <u-col>
        <span>用户名: xxx</span>
      </u-col>
      <u-col>
        <span>个人信息</span>
      </u-col>
    </u-row>
  </view>
	<view>
    <u-section title="个人信息" sub-title="查看更多"></u-section>
    <u-section title="账号设置" sub-title="查看更多"></u-section>
    <u-section title="消息通知" sub-title="查看更多"></u-section>
	</view>
</template>


<script>
	export default {
		data() {
			return {
				show: false,
			}
		}
	}
</script>