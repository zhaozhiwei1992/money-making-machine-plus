// 获取.env.{profile}中的配置
// 下述方式就不行, 为啥??
const VITE_SERVER_URL = import.meta.env.VITE_SERVER_URL || 'https://localhost:8090'

const config: {
  base_url: {
    base: string
    dev: string
    pro: string
    test: string
  }
  result_code: number | string
  default_headers: AxiosHeaders
  request_timeout: number
} = {
  /**
   * api请求基础路径
   */
  base_url: {
    // 开发环境接口前缀
    base: '/api',

    // 打包开发环境接口前缀
    dev: VITE_SERVER_URL + '/api',

    // 打包生产环境接口前缀
    pro: VITE_SERVER_URL + '/api',

    // 打包测试环境接口前缀
    test: VITE_SERVER_URL + '/api'
  },

  /**
   * 接口成功返回状态码
   */
  result_code: 200,

  /**
   * 接口请求超时时间
   */
  request_timeout: 60000,

  /**
   * 默认接口请求类型
   * 可选值：application/x-www-form-urlencoded multipart/form-data
   */
  default_headers: 'application/json'
}

export { config }
