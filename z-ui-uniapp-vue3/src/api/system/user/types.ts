export interface UserVO {
  id: string
  name: string
  login: string
  createdDate: string
  sex: string
  phonenumber: string
  email: string
  positionIdListStr: string
  roleIdListStr: string
}

export interface PasswordResetVO {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}
