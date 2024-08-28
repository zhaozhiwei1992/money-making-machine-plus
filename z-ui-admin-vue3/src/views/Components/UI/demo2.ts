// 保存数据测试
export const save = (btnObj, data) => {
  // 获取编辑区数据
  // 注册事件方式, 用起来更乱
  // useEmitt({
  //   name: 'editform.getValueEnd',
  //   callback: (editData: any) => {
  //     console.log('编辑data', editData)
  //     const data = unref(editData)
  //     request.post({ url: '/examples', data }).then((res) => {
  //       console.log(btnObj.action, ' 保存返回: ', res)
  //     })
  //   }
  // })
  // emitter.emit('editform.getValue', { componentId: 'editform' })
  console.log('保存数据', data)
}
