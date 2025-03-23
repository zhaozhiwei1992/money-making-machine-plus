import { config } from '@/config/axios/config'
import { MockMethod } from 'vite-plugin-mock'

const { result_code } = config

const timeout = 1000

export default [
  // 字典接口
  {
    url: '/captcha/numCode',
    method: 'get',
    timeout,
    response: () => {
      return {
        code: result_code,
        data: 'xxxxx'
      }
    }
  }
] as MockMethod[]
