<template>
  <el-card v-loading="loading" class="box-card">
    <template #header>
      <span class="el-icon-picture-outline">流程图</span>
    </template>
    <MyProcessViewer
      key="designer"
      :activityData="activityList"
      :processInstanceData="process_instance"
      :taskData="tasks"
      :value="bpmn_xml"
      v-bind="bpmnControlForm"
    />
  </el-card>
</template>
<script lang="ts" name="BpmProcessInstanceBpmnViewer" setup>
import { ElCard } from 'element-plus'
import { ref, onMounted } from 'vue'
import { propTypes } from '@/utils/propTypes'
import { MyProcessViewer } from '@/components/bpmnProcessDesigner/package'
import * as ActivityApi from '@/api/bpm/activity'

const props = defineProps({
  loading: propTypes.bool, // 是否加载中
  id: propTypes.string, // 流程实例的编号
  process_instance: propTypes.any, // 流程实例的信息
  tasks: propTypes.array, // 流程任务的数组
  bpmn_xml: propTypes.string // BPMN XML
})

const bpmnControlForm = ref({
  prefix: 'flowable'
})
const activityList = ref([]) // 任务列表
// const bpmnXML = computed(() => { // TODO 芋艿：不晓得为啊哈不能这么搞
//   if (!props.processInstance || !props.processInstance.processDefinition) {
//     return
//   }
//   return DefinitionApi.getProcessDefinitionBpmnXML(props.processInstance.processDefinition.id)
// })

/** 初始化 */
onMounted(async () => {
  if (props.id) {
    activityList.value = await ActivityApi.getActivityList({
      process_instance_id: props.id
    })
  }
})
</script>
<style>
.box-card {
  width: 100%;
  margin-bottom: 20px;
}
</style>
