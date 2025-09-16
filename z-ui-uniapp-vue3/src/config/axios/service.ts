import type {
  AxiosInstance,
  InternalAxiosRequestConfig,
  AxiosRequestHeaders,
  AxiosResponse,
  AxiosError
} from 'axios'

import axios from 'axios'

import qs from 'qs'

import { config } from './config'

const { result_code, base_url } = config

export const PATH_URL = 'http://127.0.0.1:8090/api'

//带着cookie, 验证码放在了session里, 不加这个每次session都是新的
axios.defaults.withCredentials = true

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: PATH_URL, // api 的 base_url
  timeout: config.request_timeout // 请求超时时间
})

// request拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    if (
      config.method === 'post' &&
      (config.headers as AxiosRequestHeaders)['Content-Type'] ===
        'application/x-www-form-urlencoded'
    ) {
      config.data = qs.stringify(config.data)
    }
    // 请求后端接口时header里把token带上
    (config.headers as AxiosRequestHeaders)['Authorization'] = uni.getStorageSync('token');
    // get参数编码
    if (config.method === 'get' && config.params) {
      let url = config.url as string
      url += '?'
      const keys = Object.keys(config.params)
      // console.log('axios获取get请求参数key:', keys)
      for (const key of keys) {
        if (config.params[key] !== void 0 && config.params[key] !== null) {
          url += `${key}=${encodeURIComponent(config.params[key])}&`
        }
      }
      url = url.substring(0, url.length - 1)
      config.params = {}
      config.url = url
    }
    return config
  },
  (error: AxiosError) => {
    // Do something with request error
    console.log(error) // for debug
    Promise.reject(error)
  }
)

// response 拦截器
service.interceptors.response.use(
  (response: AxiosResponse<any>) => {
    if (
      response.config.responseType === 'blob' ||
      response.config.responseType === 'arraybuffer' ||
      response.headers.responsetype === 'text'
    ) {
      // 如果是文件流，直接返回response
      return response
    } else if (response.status === result_code) {
      return response.data
    } else {
      return response.data
    }
  },
  (error: AxiosError) => {
    console.log('err' + error.response) // for debug
    return Promise.reject(error)
  }
)

export { service }
