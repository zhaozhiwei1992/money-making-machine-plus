// 在这里边写业务方法, 方便进行动态调用

interface ButtonType {
  id: number
  name: string
  action: string
  type: any
  size: any
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
