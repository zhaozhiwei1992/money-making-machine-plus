import axios from '@/config/axios'

var getTableListApi = function (params) {
    params = { page: params.pageIndex, size: params.pageSize };
    return axios.get({ url: '/users', params: params });
};

var saveTableApi = function (data) {
    return axios.post({ url: '/users', data: data });
};

// 获取指定数据详情
var getTableDetApi = function (id) {
    // url?id=xx
    return axios.get({ url: '/users/detail', params: { id: id } });
};

// 批量删除
var delTableListApi = function (ids) {
    // 适配后端, 直接使用RequestBody接收数据, 不能使用{ids}, 这种表示data里的是个map对象,key为ids
    return axios.delete({ url: '/users', data: ids });
};

export {
    getTableListApi,
    saveTableApi,
    getTableDetApi,
    delTableListApi
}