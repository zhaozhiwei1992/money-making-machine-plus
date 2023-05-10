<template>
  <view class="content">
    <view>
      <!-- 按钮 -->
      <u-row gutter="16">
        <u-col span="4">
      		<u-button type="primary" size="mini">启用</u-button>
        </u-col>
        <u-col span="4">
      		<u-button type="primary" size="mini">停用</u-button>
        </u-col>
      </u-row>
    </view>
    <view>
      <text class="title">{{ title }}</text>
      <!-- <u-table :data="tableData" :column-names="['ID', '任务名称', '任务状态']">
				<template v-slot:default="{ item }">
					<u-table-col>{{ item.id }}</u-table-col>
					<u-table-col>{{ item.name }}</u-table-col>
					<u-table-col>{{ item.status }}</u-table-col>
				</template>
			</u-table> -->
			<!-- 这sd uview-ui table有问题,不显示 -->
      <u-table>
        <u-tr>
          <u-td>学校</u-td>
          <u-td>班级</u-td>
          <u-td>年龄</u-td>
        </u-tr>
        <u-tr>
          <u-td>浙江大学</u-td>
          <u-td>二年级</u-td>
          <u-td>22</u-td>
        </u-tr>
        <u-tr>
          <u-td>清华大学</u-td>
          <u-td>05班</u-td>
          <u-td>20</u-td>
        </u-tr>
      </u-table>
    </view>
  </view>
</template>

<script>
import task from '@/api/system/task';
export default {
  data() {
    return {
      title: '定时任务管理',
      tableData: [],
    };
  },
  onLoad() {
    // 页面跳转进来后可以获取url参数
    const responseData = [
      { id: 1, name: 'Item 1', status: 10 },
      { id: 2, name: 'Item 2', status: 20 },
      { id: 3, name: 'Item 3', status: 30 },
    ];

    // 获取定时任务数据
    // const res = task.getTableListApi({pageIndex: 1, pageSize: 10});
    // const responseData = res.data;

    // 将获取到的数据赋值给表格数据
    this.tableData = responseData;
  },
  methods: {},
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
