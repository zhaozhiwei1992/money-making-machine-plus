import request from '@/config/axios'
import { UserType, UserLoginType } from './types'

const loginApi = (data: UserLoginType): Promise<IResponse<UserType>>  => {
    return request.post({ url: '/mobile/login', data });
};

var loginOutApi = function () {
    return request.get({ url: '/mobile/loginOut' });
};

var getUserListApi = function (_a) {
    var params = _a.params;
    return request.get({ url: '/users', params: params });
};

var getAdminRoleApi = function (params) {
    return request.get({ url: '/role/list', params: params });
};

var getTestRoleApi = function (params) {
    return request.get({ url: '/role/list', params: params });
};

var getMenuRouteListApi = function (params) {
    return request.get({ url: '/menus/route', params: params });
};

export {
    loginApi,
    loginOutApi,
    getUserListApi,
    getAdminRoleApi,
    getTestRoleApi,
    getMenuRouteListApi
}