import request from '@/config/axios';
import type { UserLoginType, UserType } from './types';

export const loginApi = (data: UserLoginType): Promise<UserType>  => {
    return request.post({ url: '/mobile/login', data });
};

export const loginByWxApi = (code: string): Promise<UserType>  => {
    return request.post({ url: '/mobile/wx/login', data: code });
};

export const loginByPhoneApi = (phonenum: string): Promise<UserType>  => {
    return request.post({ url: '/mobile/phonnum/login', data: phonenum });
};

export var loginOutApi = function () {
    return request.get({ url: '/mobile/loginOut' });
};

export var getUserListApi = function (_a) {
    var params = _a.params;
    return request.get({ url: '/users', params: params });
};

export var getAdminRoleApi = function (params) {
    return request.get({ url: '/role/list', params: params });
};

export var getTestRoleApi = function (params) {
    return request.get({ url: '/role/list', params: params });
};

export var getMenuRouteListApi = function (params) {
    return request.get({ url: '/menus/route', params: params });
};
