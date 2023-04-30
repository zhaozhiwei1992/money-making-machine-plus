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
		<u-picker :show="show" :columns="columns"></u-picker>
		<u-button @click="show = true">打开</u-button>
	</view>
</template>


<script>
	export default {
		data() {
			return {
				show: false,
                columns: [
                    ['中国', '美国', '日本']
                ],
			}
		}
	}
</script>