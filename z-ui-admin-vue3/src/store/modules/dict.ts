import { defineStore } from 'pinia'
import { store } from '../index'

// @ts-ignore
// 通过缓存在前台存储 dict信息, 方便后续使用

export interface DictState {
  isSetDict: boolean
  dictObj: Recordable
}

export const useDictStore = defineStore('dict', {
  state: (): DictState => ({
    isSetDict: false,
    dictObj: {}
  }),
  getters: {
    getDictObj(): Recordable {
      console.log('获取字典对象', this.dictObj)
      return this.dictObj
    },
    getIsSetDict(): boolean {
      return this.isSetDict
    }
  },
  actions: {
    setDictObj(dictObj: Recordable) {
      this.dictObj = dictObj
      console.log('设置字典对象', dictObj)
    },
    setIsSetDict(isSetDict: boolean) {
      this.isSetDict = isSetDict
    },
    getDictByType(type: string) {
      const result = this.dictObj[type]
      console.log(`获取字典 [${type}]:`, result)
      return result
    }
  }
})

export const useDictStoreWithOut = () => {
  return useDictStore(store)
}
