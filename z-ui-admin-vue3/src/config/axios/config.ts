// 获取.env.{profile}中的配置
// 下述方式就不行, 为啥??
const config: {
  result_code: number | string
  default_headers: AxiosHeaders
  request_timeout: number
} = {
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
