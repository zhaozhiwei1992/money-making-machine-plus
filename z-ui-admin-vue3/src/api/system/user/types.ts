export type TableData = {
  id: string
  name: string
  login: string
  imageUrl: string
  mofDivCode: string
  appid: number
}

export interface UserVO {
  id: string
  name: string
  login: string
  password: string
  imageUrl: string
  mofDivCode: string
  appid: number
  createTime: Date
  roleIdListStr: string | string[]
  positionIdListStr: string | string[]
  departmentIdListStr: string | string[]
  phonenumber: string
  email: string
}

export interface PasswordResetVO {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}
