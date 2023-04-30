import axios from '@/config/axios';
var getTableListApi = function (params) {
    params = { page: params.pageIndex, size: params.pageSize };
    return axios.default.get({ url: '/api/params', params: params });
};

var saveTableApi = function (data) {
    return axios.default.post({ url: '/api/params', data: data });
};

// 获取指定数据详情
var getTableDetApi = function (id) {
    return axios.default.get({ url: '/api/params/detail', params: { id: id } });
};

// 批量删除
var delTableListApi = function (ids) {
    return axios.default.delete({ url: '/api/params', data: ids });
};

export {
    getTableListApi,
    saveTableApi,
    getTableDetApi,
    delTableListApi
}