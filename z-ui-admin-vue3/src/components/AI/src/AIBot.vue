<script setup lang="ts">
import { getAccessToken } from '@/utils/auth'
import { propTypes } from '@/utils/propTypes'
import {
  ChatRound,
  Clock,
  Close,
  FullScreen,
  Monitor,
  Plus,
  Position,
  ScaleToOriginal
} from '@element-plus/icons-vue'
import { fetchEventSource } from '@microsoft/fetch-event-source'
import type { BubbleListItemProps, BubbleListProps } from 'element-plus-x/bubbleList/types'
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { BubbleList, Sender } from 'vue-element-plus-x'

type listType = BubbleListItemProps & {
  key: number
  role: 'user' | 'ai'
}
const list = ref<BubbleListProps<listType>['list']>(initChatItem())

function initChatItem(): listType[] {
  const messages: listType[] = []
  const role = 'ai'
  const placement = 'start'
  const key = 1
  const content = '你好，我是你的AI助理，请问有啥可以帮到你?'
  const loading = false
  const shape = 'corner'
  const variant = role === 'ai' ? 'filled' : 'outlined'
  const isMarkdown = false
  const typing = false
  const avatar =
    role === 'ai'
      ? 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
      : 'https://avatars.githubusercontent.com/u/76239030?v=4'

  messages.push({
    key,
    role,
    placement,
    content,
    loading,
    shape,
    variant,
    isMarkdown,
    typing,
    avatar,
    avatarSize: '24px'
  })
  return messages
}

// 生成问答
function generateChatItems(): listType[] {
  const messages: listType[] = []
  for (let i = 1; i < 3; i++) {
    const role = i % 2 === 0 ? 'ai' : 'user'
    const placement = role === 'ai' ? 'start' : 'end'
    const key = i + 1
    const content = role === 'ai' ? '思考中......' : senderValue.value
    // senderValue.value = ''
    const loading = false
    const shape = 'corner'
    const variant = role === 'ai' ? 'filled' : 'outlined'
    const isMarkdown = true
    const typing = role === 'ai' ? true : false
    const avatar =
      role === 'ai'
        ? 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
        : 'https://avatars.githubusercontent.com/u/76239030?v=4'

    messages.push({
      key,
      role,
      placement,
      content,
      loading,
      shape,
      variant,
      isMarkdown,
      typing,
      avatar,
      avatarSize: '24px'
    })
  }
  return messages
}
const chatStream = async () => {
  const res = generateChatItems()
  list.value.push(...res)
  const ctrl = new AbortController()
  fetchEventSource('/api/ai/chat-stream', {
    method: 'post',
    headers: {
      'Content-Type': 'application/json',
      Authorization: getAccessToken(),
      accept: 'application/json, text/plain, */*'
    },
    body: JSON.stringify({
      query: senderValue.value,
      user: 'admin',
      apiKey: 'app-ZzwOVY1dCDyM2V05yhGuqx9D'
      // apiKey: 'sk-81ff6c0404aa4918b7c867cb9f536d73',
      // appId: '8bff07f9a30a4a3e934ee98341e66cfe'
    }),
    openWhenHidden: true,
    signal: ctrl.signal, // 绑定终止信号
    onmessage(evt) {
      if (evt.data) {
        let orderData = JSON.parse(evt.data)
        console.log(orderData)
        if ('思考中......' === list.value[list.value.length - 1].content) {
          list.value[list.value.length - 1].content = ''
        }
        list.value[list.value.length - 1].content +=
          orderData.event === 'message' ? orderData.answer : ''
      }
    },
    onerror(err) {
      throw err
    },
    onclose() {
      //如果是新对话,发送消息成功,历史对话记录默认新增一条并高亮
      // if (self.isHistoryIndex == -1) {
      //   self.getHistoryList('new')
      // }
      return
    }
  })
}

// 语音处理部分
const senderRef = ref()
const senderValue = ref('')
function onRecordingChange(recording: boolean) {
  if (recording) {
    ElMessage.success('开始录音')
  } else {
    ElMessage.success('结束录音')
  }
}

defineProps({
  color: propTypes.string.def('')
})

const showAI = () => {
  show.value = !show.value
}

// 状态
const show = ref(false)
const isMaximized = ref(false)
const isDocked = ref(true)
const isFloating = ref(false)
const containerElement = ref<HTMLElement | null>(null)
const containerCreated = ref(false)
const fullscreen = ref(false)
const historyWidth = ref(0)
const chatWidth = ref(24)
// 空白占位
const chatWriteWidth = ref(1)

// 计算属性
const containerClass = computed(() => '.ai-dialog-container')

// 操作函数
const toggleMaximize = () => {
  isMaximized.value = !isMaximized.value
  fullscreen.value = !fullscreen.value
  if (fullscreen.value) {
    historyWidth.value = 4
    chatWriteWidth.value = 4
    chatWidth.value = 12
  } else {
    historyWidth.value = 0
    chatWriteWidth.value = 1
    chatWidth.value = 24
  }
}

const toggleDock = () => {
  isDocked.value = !isDocked.value
  isFloating.value = false
}

const toggleFloat = () => {
  isFloating.value = !isFloating.value
  isDocked.value = false
}

const newConversation = () => {
  // 新建对话逻辑
  console.log('新建对话')
}

const viewHistory = () => {
  // 查看历史记录逻辑
  console.log('查看历史')
}

onMounted(() => {
  // 创建一个容器并添加到body
  const container = document.createElement('div')
  container.className = 'ai-dialog-container'
  document.body.appendChild(container)
  containerElement.value = container
  containerCreated.value = true
})

onUnmounted(() => {
  // 清理
  if (containerElement.value) {
    document.body.removeChild(containerElement.value)
    containerElement.value = null
    containerCreated.value = false
  }
})
</script>

<template>
  <div @click="showAI()">
    <el-icon><ChatRound /></el-icon>
  </div>

  <!-- 使用teleport将对话框传送到自定义容器中 -->
  <teleport :to="containerClass" :disabled="!containerCreated">
    <el-dialog
      v-model="show"
      title="AI助手"
      width="25%"
      draggable
      :fullscreen="fullscreen"
      :append-to-body="false"
      :destroy-on-close="false"
      :show-close="false"
      :class="[
        'ai-dialog',
        { 'ai-dialog-maximized': isMaximized, 'ai-dialog-floating': isFloating }
      ]"
    >
      <template #header="{ titleId, titleClass }">
        <div class="ai-dialog-header">
          <h4 :id="titleId" :class="titleClass">AI助手</h4>
          <div class="ai-dialog-controls">
            <el-tooltip content="新建对话" placement="top">
              <el-button circle @click="newConversation" type="primary" text size="small">
                <el-icon><Plus /></el-icon>
              </el-button>
            </el-tooltip>

            <el-tooltip content="查看历史" placement="top">
              <el-button circle @click="viewHistory" text size="small">
                <el-icon><Clock /></el-icon>
              </el-button>
            </el-tooltip>

            <el-tooltip :content="isFloating ? '嵌入右边' : '浮动窗口'" placement="top">
              <el-button circle @click="toggleFloat" text size="small">
                <el-icon><Position /></el-icon>
              </el-button>
            </el-tooltip>

            <el-tooltip :content="isDocked ? '解除嵌入' : '嵌入右边'" placement="top">
              <el-button circle @click="toggleDock" text size="small">
                <el-icon><Monitor /></el-icon>
              </el-button>
            </el-tooltip>

            <el-tooltip :content="isMaximized ? '恢复大小' : '最大化'" placement="top">
              <el-button circle @click="toggleMaximize" text size="small">
                <el-icon>
                  <FullScreen v-if="!isMaximized" />
                  <ScaleToOriginal v-else />
                </el-icon>
              </el-button>
            </el-tooltip>

            <el-tooltip placement="top">
              <el-button circle @click="showAI" text size="small">
                <el-icon><Close /></el-icon>
              </el-button>
            </el-tooltip>
          </div>
        </div>
      </template>

      <!-- 这里增加具体聊天的实现页面 -->
      <el-row>
        <el-col
          :span="historyWidth"
          v-if="fullscreen === true"
          style="position: relative; background-color: #dcdcdc"
        >
          <el-timeline style="max-width: 600px">
            <el-timeline-item timestamp="2018/4/12" placement="top">
              <el-card>
                <h4>Update Github template</h4>
                <p>Tom committed 2018/4/12 20:46</p>
              </el-card>
            </el-timeline-item>
            <el-timeline-item timestamp="2018/4/3" placement="top">
              <el-card>
                <h4>Update Github template</h4>
                <p>Tom committed 2018/4/3 20:46</p>
              </el-card>
            </el-timeline-item>
            <el-timeline-item timestamp="2018/4/2" placement="top">
              <el-card>
                <h4>Update Github template</h4>
                <p>Tom committed 2018/4/2 20:46</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
          <div>
            <span>仅显示最近20条历史</span>
          </div>
          <div style="position: absolute; bottom: 20px; width: calc(100% - 0px)">
            <el-button type="primary">新建会话</el-button>
          </div>
        </el-col>
        <el-col :span="chatWriteWidth" />
        <el-col :span="chatWidth">
          <div
            style="
              display: flex;
              top: 1vh;
              flex-direction: column;
              height: 68vh;
              justify-content: space-between;
              position: relative;
            "
          >
            <BubbleList :list="list" />
            <Sender
              ref="senderRef"
              v-model:value="senderValue"
              :auto-size="{ minRows: 3, maxRows: 5 }"
              allow-speech
              @recording-change="onRecordingChange"
              style="position: absolute; bottom: 20px; width: calc(100% - 0px)"
              @submit="chatStream"
            />
          </div>
        </el-col>
        <el-col :span="chatWriteWidth" />
      </el-row>
    </el-dialog>
  </teleport>
</template>

<style>
/* 自定义容器样式 */
.ai-dialog-container {
  position: fixed;
  right: 5%;
  bottom: 0;
  width: 25%;
  height: 80vh;
  z-index: 2000;
}

/* 对话框样式 */
.ai-dialog .el-dialog {
  margin: 0 !important;
  width: 100% !important;
  height: 80vh !important;
  position: absolute !important;
  bottom: 0 !important;
  overflow: hidden;
}

/* 最大化样式 */
.ai-dialog-maximized .el-dialog {
  width: 80% !important;
  height: 90vh !important;
  left: 10% !important;
  right: 10% !important;
  top: 5vh !important;
  bottom: 5vh !important;
}

/* 浮动样式 */
.ai-dialog-floating .el-dialog {
  width: 360px !important;
  height: 500px !important;
  right: 20px !important;
  bottom: 20px !important;
}

.ai-dialog .el-dialog__body {
  height: calc(80vh - 100px) !important;
  overflow-y: auto !important;
}

.ai-dialog-maximized .el-dialog__body {
  height: calc(90vh - 100px) !important;
}

.ai-dialog-floating .el-dialog__body {
  height: calc(500px - 100px) !important;
}

/* 自定义头部样式 */
.ai-dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.ai-dialog-controls {
  display: flex;
  gap: 5px;
}

.ai-dialog .el-dialog__header {
  padding: 10px !important;
  margin-right: 0 !important;
}

.ai-dialog .el-dialog__headerbtn {
  top: 14px !important;
}

/* 禁用背景遮罩 */
.ai-dialog-container + .el-overlay {
  background-color: transparent !important;
  pointer-events: none !important;
}
</style>
