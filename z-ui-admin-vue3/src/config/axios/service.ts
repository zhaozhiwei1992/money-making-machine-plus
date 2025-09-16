import axios, {
  AxiosError,
  AxiosInstance,
  AxiosRequestHeaders,
  AxiosResponse,
  InternalAxiosRequestConfig
} from 'axios'

import qs from 'qs'

import { config } from './config'

import { ElMessage } from 'element-plus'

import { getAccessToken } from '@/utils/auth'

const { result_code } = config

export const PATH_URL = import.meta.env.VITE_API_BASEPATH || '/api'

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
    ;(config.headers as AxiosRequestHeaders)['Authorization'] = getAccessToken()
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
    if (response.config.responseType === 'blob' || response.config.responseType === 'arraybuffer') {
      // 如果是文件流，直接返回response
      console.log('文件流', response)
      return response
    } else if (response.status === result_code) {
      return response.data
    } else {
      // ElMessage.error(response.data.msg)
      ElMessage.error('获取数据异常')
    }
  },
  (error: AxiosError) => {
    console.log('err' + error.message) // for debug
    if (error.response?.data) {
      ElMessage.error(error.response?.data)
    } else {
      ElMessage.error(error.message)
    }
    return Promise.reject(error)
  }
)

export { service }
