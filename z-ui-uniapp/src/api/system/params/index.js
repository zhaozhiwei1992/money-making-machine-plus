import axios from '@/config/axios';
var getTableListApi = function (params) {
    params = { page: params.pageIndex, size: params.pageSize };
    return axios.get({ url: '/params', params: params });
};

var saveTableApi = function (data) {
    return axios.post({ url: '/params', data: data });
};

// 获取指定数据详情
var getTableDetApi = function (id) {
    return axios.get({ url: '/params/detail', params: { id: id } });
};

// 批量删除
var delTableListApi = function (ids) {
    return axios.delete({ url: '/params', data: ids });
};

export {
    getTableListApi,
    saveTableApi,
    getTableDetApi,
    delTableListApi
}