import request from '@/config/axios'
import { useEmitt } from '@/hooks/web/useEmitt'
import { reactive } from 'vue'

const { emitter } = useEmitt()

// 保存数据测试
export const add = async (btnObj) => {
  // 获取编辑区数据
  const data: any = reactive([])
  useEmitt({
    name: 'editform.getValueEnd',
    callback: (editData: any) => {
      data.push(editData)
    }
  })
  emitter.emit('editform.getValue', { componentId: 'editform' })
  return await request.post({ url: '/examples', data })
}
