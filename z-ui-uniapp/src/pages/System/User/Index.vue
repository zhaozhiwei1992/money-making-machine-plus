<template>
	<view class="uni-container">
		<!-- uview-ui 这table都不能用坑 -->
		<!-- <u-table :columns="columns" :data="tableData"></u-table> -->
		<uni-card>
			<uni-table border stripe>
				<uni-tr>
					<uni-th v-for="(item, index) in columns" :key="index" :width="item.width" :align="item.align">{{ item.title
					}}</uni-th>
				</uni-tr>
				<uni-tr v-for="(item, index) in tableData" :key="index">
					<uni-td v-for="(col, inx) in columns" :key="inx">{{ item[col.field] }}</uni-td>
				</uni-tr>
			</uni-table>
			<uni-pagination title="标题文字" :total="pageCount" :pageSize="pageSize" :current="currentPage"></uni-pagination>
		</uni-card>
	</view>
</template>

<script>
import { getTableListApi } from '@/api/system/user';
export default {
	data() {
		return {
			columns: [
				{ title: 'ID', field: 'id', align: 'center', width: '100' },
				{ title: '用户名', field: 'name', align: 'center', width: '100' },
				{ title: '登录名', field: 'login', align: 'center', width: '100' },
				{ title: '状态', field: 'activited', align: 'center', width: '100' }
			],
			tableData: [],
			title: '用户管理',
			loading: false,      // Indicates if data is being loaded
			currentPage: 1,     // Current page number
			pageSize: 10,       // Number of items per page
			pageCount: 0,       // Total number of pages
			finished: false     // Indicates if all data has been loaded
		};
	},
	onLoad() {
		// 页面跳转进来后可以获取url参数
		this.loadData();
	},
	methods: {
		loadData() {
			// Simulate an asynchronous API call to load data
			// Replace this with your actual data loading logic
			this.loading = true;

			setTimeout(() => {

				getTableListApi({ pageIndex: this.currentPage, pageSize: this.pageSize }).then(res => {
					const responseData = res.data;
					// Update the list data
					this.tableData = responseData.list;
					this.pageCount = responseData.total;
					// Reset loading and finished state
					this.loading = false;
					this.finished = false;
				});
			}, 1000);
		}
	},
};
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
