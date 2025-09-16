'use strict';
// 对请求option进行备份
var assign =
  (this && this.__assign) ||
  function () {
    assign =
        // Object.assign如果存在就直接用, 或者用自定义的备份
      Object.assign ||
      function (t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
          s = arguments[i];
          for (var p in s)
            if (Object.prototype.hasOwnProperty.call(s, p)) {
              t[p] = s[p];
            }
        }
        // t相当于对s做了一个备份
        return t;
      };
    return assign.apply(this, arguments);
  };

import axios_service from './service';
import axios_config from './config';
var default_headers = axios_config.default_headers;
var request = function (option) {
  var url = option.url,
    method = option.method,
    params = option.params,
    data = option.data,
    headersType = option.headersType,
    responseType = option.responseType;
  return (0, axios_service)({
    url: url,
    method: method,
    params: params,
    data: data,
    responseType: responseType,
    headers: {
      'Content-Type': headersType || default_headers,
    },
  });
};

export default {
  get: function (option) {
    return request(assign({ method: 'get' }, option));
  },
  post: function (option) {
    return request(assign({ method: 'post' }, option));
  },
  delete: function (option) {
    return request(assign({ method: 'delete' }, option));
  },
  put: function (option) {
    return request(assign({ method: 'put' }, option));
  },
};
