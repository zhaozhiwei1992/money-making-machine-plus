import request from '@/config/axios'

// 在这里边写业务方法, 方便进行动态调用

interface ButtonType {
  id: number
  name: string
  action: string
  type: any
  size: any
}

export const save = (data: any) => {
  // 输出表单数据
  alert(data.name)
}

export const add = (btnObj: ButtonType) => {
  alert(btnObj.name)
}

export const del = (btnObj: ButtonType) => {
  alert(btnObj.name)
}

export const audit = (btnObj: ButtonType) => {
  alert(btnObj.name)
}

// 获取列表数据
export const getTableListApi = async (params: any): Promise<IResponse> => {
  params = { ...params, page: params.pageIndex, size: params.pageSize }
  console.log('分页信息', params)
  return await request.get({ url: '/users', params })
}
