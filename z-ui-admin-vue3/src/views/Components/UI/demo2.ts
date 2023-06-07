import request from '@/config/axios'
import { useEmitt } from '@/hooks/web/useEmitt'
import { unref } from 'vue'

const { emitter } = useEmitt()

// 保存数据测试
export const save = (btnObj) => {
  // 获取编辑区数据
  useEmitt({
    name: 'editform.getValueEnd',
    callback: (editData: any) => {
      console.log('编辑data', editData)
      const data = unref(editData)
      request.post({ url: '/examples', data }).then((res) => {
        console.log(btnObj.action, ' 保存返回: ', res)
      })
    }
  })
  emitter.emit('editform.getValue', { componentId: 'editform' })
}
