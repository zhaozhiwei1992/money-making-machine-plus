<script setup lang="ts">
import { ref } from 'vue'
import { ElCard, ElRow, ElCol, ElButton } from 'element-plus'
import NormalForm from './NormalForm.vue'

// 获取历史记录
const historyContentList = ref([
  { id: 1, direct: '0', content: 'Tom committed 2018/4/12 20:46' },
  { id: 2, direct: '1', content: 'Tom committed 2018/4/12 20:46' },
  { id: 3, direct: '0', content: 'Tom committed 2018/4/12 20:46' },
  { id: 4, direct: '1', content: 'Tom committed 2018/4/12 20:46' },
  { id: 5, direct: '0', content: 'Tom committed 2018/4/12 20:46' },
  { id: 6, direct: '1', content: 'Tom committed 2018/4/12 20:46' }
])

const copy = () => {
  // 复制
}
</script>

<template>
  <div>
    <ElRow>
      <ul>
        <li v-for="historyContent in historyContentList" :key="historyContent.id">
          <!-- direct: 0:请求(靠右) 1:回复(靠坐) -->
          <ElRow v-if="historyContent.direct == '0'" class="content-row">
            <ElCol :span="21">
              <ElCard class="content-card-0">
                <p>{{ historyContent.content }}</p>
              </ElCard>
            </ElCol>
            <ElCol :span="3" />
          </ElRow>
          <ElRow v-if="historyContent.direct == '1'" class="content-row">
            <ElCol :span="3" />
            <ElCol :span="21">
              <ElCard class="content-card-1">
                <p>{{ historyContent.content }}</p>
                <template #footer>
                  <!-- <ElButton size="small" icon="Edit" @click="copy"> 复制 </ElButton> -->
                  hh
                </template>
              </ElCard>
            </ElCol>
          </ElRow>
        </li>
      </ul>
    </ElRow>
    <ElRow>
      <NormalForm />
    </ElRow>
  </div>
</template>

<style lang="less" scoped>
.content-row {
  margin-bottom: 10px;
}

.content-card-0 {
  background-color: #f5f7fa;
  width: 45vw;
}

.content-card-1 {
  // 用蓝色背景填充
  background-color: deepskyblue;
  width: 45vw;
}
</style>
