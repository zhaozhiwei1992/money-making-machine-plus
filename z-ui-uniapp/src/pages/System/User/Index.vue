<template>
	<view class="content">
		<view>
			<!-- 按钮 -->
			<u-button type="primary">新增</u-button>
			<u-button type="primary">删除</u-button>
		</view>
		<view>
			<text class="title">{{ title }}</text>
			<u-table>
				<u-tr>
					<u-td>用户名</u-td>
					<u-td>登录名</u-td>
					<u-td>区划</u-td>
					<u-td>启用状态</u-td>
				</u-tr>
				<u-tr>
					<u-td>11</u-td>
					<u-td>11</u-td>
					<u-td>11</u-td>
					<u-td>11</u-td>
				</u-tr>
				<u-tr>
					<u-td>11</u-td>
					<u-td>11</u-td>
					<u-td>11</u-td>
					<u-td>11</u-td>
				</u-tr>
			</u-table>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			title: '用户管理'
		}
	},
	onLoad() {
		// 页面跳转进来后可以获取url参数

	},
	methods: {

	}
}
</script>

<style>
.content {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
}

.logo {
	height: 200rpx;
	width: 200rpx;
	margin: 200rpx auto 50rpx auto;
}

.text-area {
	display: flex;
	justify-content: center;
}

.title {
	font-size: 36rpx;
	color: #8f8f94;
}
</style>
