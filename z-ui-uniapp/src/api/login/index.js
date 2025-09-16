import axios from '@/config/axios';

var loginApi = function (data) {
    return axios.post({ url: '/mobile/login', data });
};

var loginOutApi = function () {
    return axios.get({ url: '/mobile/loginOut' });
};

var getUserListApi = function (_a) {
    var params = _a.params;
    return axios.get({ url: '/users', params: params });
};

var getAdminRoleApi = function (params) {
    return axios.get({ url: '/role/list', params: params });
};

var getTestRoleApi = function (params) {
    return axios.get({ url: '/role/list', params: params });
};

var getMenuRouteListApi = function (params) {
    return axios.get({ url: '/menus/route', params: params });
};

export {
    loginApi,
    loginOutApi,
    getUserListApi,
    getAdminRoleApi,
    getTestRoleApi,
    getMenuRouteListApi
}