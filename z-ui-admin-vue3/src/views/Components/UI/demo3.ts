import request from '@/config/axios'
import { useEmitt } from '@/hooks/web/useEmitt'
import { unref } from 'vue'

const { emitter } = useEmitt()

// 保存数据测试
export const save = (btnObj) => {
  // 获取可编辑列表区区数据
  useEmitt({
    name: 'editDataTable.getDataListEnd',
    callback: (editData: any) => {
      console.log('可编辑列表data', editData)
      const data = unref(editData)
      request.post({ url: '/examples', data }).then((res) => {
        console.log(btnObj.action, ' 保存返回: ', res)
      })
    }
  })
  emitter.emit('editDataTable.getDataList', { componentId: 'editdatatable' })
}

export const addRow = (btnObj) => {
  // 触发增加行事件
  emitter.emit('edittable.addRow', {})
}

export const delRow = (btnObj) => {
  emitter.emit('edittable.delRow')
}
