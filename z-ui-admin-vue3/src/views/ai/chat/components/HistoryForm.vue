<script setup lang="ts">
import { onMounted, reactive, ref, unref } from 'vue'
import {
  ElTimeline,
  ElTimelineItem,
  ElCard,
  ElRow,
  ElCol,
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput
} from 'element-plus'
import { Delete, Edit } from '@element-plus/icons-vue'
import { getHistories, delHistoryApi } from '@/api/ai/history'
import { HistoryVO } from '@/api/ai/history/types'
import { saveTableApi } from '@/api/ai/history'

// 获取历史记录
const historyList: HistoryVO[] = reactive([])

onMounted(async () => {
  // 查询所有历史信息
  const res = await getHistories()
  historyList.push(...res)
})

// 子传父
const emit = defineEmits(['historyClick'])
const historyClick = (historyId: number) => {
  emit('historyClick', historyId)
}

const delHistory = async (historyId: number) => {
  await delHistoryApi([historyId])
  // 刷新页面, 重新赋值
  historyList.length = 0
  const res = await getHistories()
  historyList.push(...res)
}

const form = reactive({
  remark: '',
  id: 0
})

const dialogFormVisible = ref(false)

const modifyHistory = async (historyId: number, remark: string) => {
  // 弹出编辑框
  dialogFormVisible.value = true
  form.remark = remark
  form.id = historyId
}

const modifyHistorySave = async () => {
  const historyVO: HistoryVO = {
    id: form.id,
    remark: form.remark,
    engineId: 0,
    createdBy: '',
    createTime: new Date(),
    lastModifiedDate: ''
  }
  await saveTableApi(historyVO)
  // 编辑完成后后台更新, 并刷新页面
  historyList.length = 0
  const res = await getHistories()
  historyList.push(...res)
  dialogFormVisible.value = false
}
</script>

<template>
  <div>
    <ElTimeline style="max-width: 600px">
      <ElTimelineItem
        v-for="history in historyList"
        :timestamp="history.lastModifiedDate"
        :key="history.id"
        placement="top"
      >
        <ElCard>
          <ElRow>
            <ElCol :span="21">
              <p @click="historyClick(history.id)">{{ history.remark }}</p>
            </ElCol>
            <ElCol :span="3">
              <ElButton
                :icon="Edit"
                text
                circle
                size="small"
                @click="modifyHistory(history.id, history.remark)"
              />
              <ElButton :icon="Delete" text circle size="small" @click="delHistory(history.id)" />
            </ElCol>
          </ElRow>
        </ElCard>
      </ElTimelineItem>
    </ElTimeline>
    <!-- 弹出表单 -->
    <el-dialog v-model="dialogFormVisible" title="修改名称" width="500">
      <el-form :model="form">
        <el-form-item>
          <el-input v-model="form.remark" autocomplete="off" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">Cancel</el-button>
          <el-button type="primary" @click="modifyHistorySave"> Confirm </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="less" scoped></style>
