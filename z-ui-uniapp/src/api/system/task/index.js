import axios from '@/config/axios';

var getTableListApi = function (params) {
    // 转换, 适配jpa PageRequest
    params = { page: params.pageIndex, size: params.pageSize };
    // 这里直接写 params等价与 params: {page:xx, size:xx}
    return axios.get({ url: '/task-params', params: params });
};

var saveTableApi = function (data) {
    return axios.post({ url: '/task-params', data: data });
};

// 获取指定数据详情
var getTableDetApi = function (id) {
    // url?id=xx
    return axios.get({ url: '/task-params/detail', params: { id: id } });
};

// 批量删除
var delTableListApi = function (ids) {
    return axios.delete({ url: '/task-params', data: ids });
};

// 批量启用
var startTaskListApi = function (ids) {
    return axios.post({ url: '/task-params/start', data: ids });
};

// 批量停用
var stopTaskListApi = function (ids) {
    return axios.post({ url: '/task-params/stop', data: ids });
};

export {
    getTableListApi,
    saveTableApi,
    getTableDetApi,
    delTableListApi,
    startTaskListApi,
    stopTaskListApi
}