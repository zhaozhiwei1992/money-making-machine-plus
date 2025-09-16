import { defineStore } from 'pinia'
import { store } from '../index'

// @ts-ignore
// 通过缓存在前台存储 dict信息, 方便后续使用
import { CACHE_KEY, useCache } from '@/hooks/web/useCache'

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
      return this.dictObj
    },
    getIsSetDict(): boolean {
      return this.isSetDict
    }
  },
  actions: {
    setDictObj(dictObj: Recordable) {
      this.dictObj = dictObj
    },
    setIsSetDict(isSetDict: boolean) {
      this.isSetDict = isSetDict
    },
    getDictByType(type: string) {
      return this.dictObj[type]
    }
  }
})

export const useDictStoreWithOut = () => {
  return useDictStore(store)
}
