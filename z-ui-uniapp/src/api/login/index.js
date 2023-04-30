import axios from '@/config/axios';

var loginApi = function (data) {
    return axios.default.post({ url: '/api/login', data });
};

var loginOutApi = function () {
    return axios.default.get({ url: '/api/loginOut' });
};

var getUserListApi = function (_a) {
    var params = _a.params;
    return axios.default.get({ url: '/api/users', params: params });
};

var getAdminRoleApi = function (params) {
    return axios.default.get({ url: '/role/list', params: params });
};

var getTestRoleApi = function (params) {
    return axios.default.get({ url: '/role/list', params: params });
};

var getMenuRouteListApi = function (params) {
    return axios.default.get({ url: '/api/menus/route', params: params });
};

export default {
    loginApi,
    loginOutApi,
    getUserListApi,
    getAdminRoleApi,
    getTestRoleApi,
    getMenuRouteListApi
}