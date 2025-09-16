<script lang="ts" setup>
import { useMessage } from '@/hooks/web/useMessage'
import { handleTree2 } from '@/utils/tree'
import { useClipboard } from '@vueuse/core'

import { getViewCode } from '@/api/code-generator'
import { PreviewVO } from '@/api/code-generator/types'
import hljs from 'highlight.js'
import { default as java, default as xml } from 'highlight.js/lib/languages/java'
import javascript from 'highlight.js/lib/languages/javascript'
import sql from 'highlight.js/lib/languages/sql'
import typescript from 'highlight.js/lib/languages/typescript'
import 'highlight.js/styles/github.css'
import { emit } from 'process'
import { useI18n } from 'vue-i18n'

defineOptions({ name: 'InfraCodegenPreviewCode' })

const props = defineProps({
  // 是否展现
  isOpen: {
    type: Boolean as PropType<boolean>,
    required: true
  },
  tableName: {
    type: String as PropType<string>,
    required: true
  }
})

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const loading = ref(false) // 加载中的状态
const preview = reactive({
  fileTree: [], // 文件树
  activeName: '' // 激活的文件名
})
const previewCodegen = ref<PreviewVO[]>()

/** 点击文件 */
const handleNodeClick = async (data, node) => {
  if (node && !node.isLeaf) {
    return false
  }
  preview.activeName = data.id
}

/** 生成 files 目录 **/
interface filesType {
  id: string
  label: string
  parentId: string
}

// 提供 open 方法，用于打开弹窗
defineExpose({ open })

/** 处理文件 */
const handleFiles = (datas: PreviewVO[]) => {
  let exists = {} // key：file 的 id；value：true
  let files: filesType[] = []
  // 遍历每个元素
  for (const data of datas) {
    let paths = data.filePath.split('/')
    let fullPath = '' // 从头开始的路径，用于生成 id
    // 遍历每个 path， 拼接成树
    for (let i = 0; i < paths.length; i++) {
      // 已经添加到 files 中，则跳过
      let oldFullPath = fullPath
      // 下面的 replaceAll 的原因，是因为上面包处理了，导致和 tabs 不匹配，所以 replaceAll 下
      fullPath = fullPath.length === 0 ? paths[i] : fullPath.replaceAll('.', '/') + '/' + paths[i]
      if (exists[fullPath]) {
        continue
      }
      // 添加到 files 中
      exists[fullPath] = true
      files.push({
        id: fullPath,
        label: paths[i],
        parentId: oldFullPath || '/' // "/" 为根节点
      })
    }
  }
  return files
}

/** 复制 **/
const copy = async (text: string) => {
  const { copy, copied, isSupported } = useClipboard({ source: text })
  if (!isSupported) {
    message.error(t('common.copyError'))
    return
  }
  await copy()
  if (unref(copied)) {
    message.success(t('common.copySuccess'))
  }
}

/**
 * 代码高亮
 */
const highlightedCode = (item) => {
  const language = item.filePath.substring(item.filePath.lastIndexOf('.') + 1)
  console.log('language', language)
  const result = hljs.highlight(language, item.code || '', true)
  console.log('result', result)
  return result.value || '&nbsp;'
}

/** 初始化 **/
onMounted(async () => {
  // 注册代码高亮的各种语言
  hljs.registerLanguage('java', java)
  hljs.registerLanguage('xml', xml)
  hljs.registerLanguage('html', xml)
  hljs.registerLanguage('vue', xml)
  hljs.registerLanguage('javascript', javascript)
  hljs.registerLanguage('sql', sql)
  hljs.registerLanguage('typescript', typescript)

  try {
    loading.value = true
    // 生成代码
    const data = await getViewCode(props.tableName)
    previewCodegen.value = data
    // 处理文件
    let file = handleFiles(data)
    preview.fileTree = handleTree2(file, 'id', 'parentId', 'children', '/')
    // 点击首个文件
    preview.activeName = data[0].filePath
  } finally {
    loading.value = false
  }
})

const emit = defineEmits(['update:isOpen'])

const localIsOpen = computed({
  get() {
    return props.isOpen // 读取父组件的 props
  },
  set(newValue) {
    emit('update:isOpen', newValue) // 通知父组件更新
  }
})
</script>
<template>
  <Dialog
    v-model="localIsOpen"
    align-center
    class="app-infra-codegen-preview-container"
    title="代码预览"
    width="80%"
    @close="closeDialog"
  >
    <div class="flex">
      <!-- 代码目录树 -->
      <el-card
        v-loading="loading"
        :gutter="8"
        class="w-1/3"
        element-loading-text="生成文件目录中..."
        shadow="hover"
      >
        <el-scrollbar height="calc(100vh - 88px - 40px)">
          <el-tree
            ref="treeRef"
            :data="preview.fileTree"
            :expand-on-click-node="false"
            default-expand-all
            highlight-current
            node-key="id"
            @node-click="handleNodeClick"
          />
        </el-scrollbar>
      </el-card>
      <!-- 代码 -->
      <el-card
        v-loading="loading"
        :gutter="16"
        class="ml-3 w-2/3"
        element-loading-text="加载代码中..."
        shadow="hover"
      >
        <el-tabs v-model="preview.activeName">
          <el-tab-pane
            v-for="item in previewCodegen"
            :key="item.filePath"
            :label="item.filePath.substring(item.filePath.lastIndexOf('/') + 1)"
            :name="item.filePath"
          >
            <el-button class="float-right" text type="primary" @click="copy(item.code)">
              {{ t('common.copy') }}
            </el-button>
            <el-scrollbar height="800px">
              <pre><code v-html="highlightedCode(item)"></code></pre>
            </el-scrollbar>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </Dialog>
</template>
<style lang="scss">
.app-infra-codegen-preview-container {
  .el-scrollbar .el-scrollbar__wrap .el-scrollbar__view {
    display: inline-block;
    white-space: nowrap;
  }
}
</style>
