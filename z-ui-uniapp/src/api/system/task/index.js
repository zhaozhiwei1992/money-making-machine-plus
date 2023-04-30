import axios from '@/config/axios';

var getTableListApi = function (params) {
    // 转换, 适配jpa PageRequest
    params = { page: params.pageIndex, size: params.pageSize };
    // 这里直接写 params等价与 params: {page:xx, size:xx}
    return axios.default.get({ url: '/api/task-params', params: params });
};

var saveTableApi = function (data) {
    return axios.default.post({ url: '/api/task-params', data: data });
};

// 获取指定数据详情
var getTableDetApi = function (id) {
    // url?id=xx
    return axios.default.get({ url: '/api/task-params/detail', params: { id: id } });
};

// 批量删除
var delTableListApi = function (ids) {
    // 适配后端, 直接使用RequestBody接收数据, 不能使用{ids}, 这种表示data里的是个map对象,key为ids
    return axios.default.delete({ url: '/api/task-params', data: ids });
};

export {
    getTableListApi,
    saveTableApi,
    getTableDetApi,
    delTableListApi
}