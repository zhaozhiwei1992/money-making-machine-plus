import axios from 'axios';
import axios_config from './config'
import qs from 'qs';

const result_code = axios_config.result_code, base_url = axios_config.base_url.base;

// 创建axios实例
const service = axios.create({
    // 后端地址, http://ip:port/api/xx, 就不用每个都写/api了
    baseURL: axios_config.server_url + base_url,
    // 请求超时时间
    timeout: axios_config.request_timeout 
});

// request拦截器
service.interceptors.request.use(function (config) {
    if (config.method === 'post' &&
        config.headers['Content-Type'] ===
            'application/x-www-form-urlencoded') {
        config.data = qs.default.stringify(config.data);
    }
    // 请求后端接口时header里把token带上
    ;
    config.headers['Authorization'] = uni.getStorageSync('token');
    // get参数编码
    if (config.method === 'get' && config.params) {
        var url = config.url;
        url += '?';
        var keys = Object.keys(config.params);
        // console.log('axios获取get请求参数key:', keys)
        for (var i = 0, param_key = keys; i < param_key.length; i++) {
            var key = param_key[i];
            if (config.params[key] !== void 0 && config.params[key] !== null) {
                url += "".concat(key, "=").concat(encodeURIComponent(config.params[key]), "&");
            }
        }
        url = url.substring(0, url.length - 1);
        config.params = {};
        config.url = url;
    }
    return config;
}, function (error) {
    // Do something with request error
    console.log(error); // for debug
    Promise.reject(error);
});

// response 拦截器
service.interceptors.response.use(function (response) {
    if (response.config.responseType === 'blob') {
        // 如果是文件流，直接过
        return response;
    }
    else if (response.data.code === result_code) {
        return response.data;
    }
    else {
        uni.showToast({
            title: response.data.message,
            image: 'https://cdn.uviewui.com/uview/demo/toast/error.png',
            duration: 2000
        });
    }
}, function (error) {
    console.log('err' + error); // for debug
    uni.showToast({
        title: error.message,
        image: 'https://cdn.uviewui.com/uview/demo/toast/error.png',
        duration: 2000
    });
    return Promise.reject(error);
});

// 只暴露一个接口, 直接用default, 多个就得{}了
export default service;