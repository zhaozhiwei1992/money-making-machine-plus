<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="角色名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入角色名称" />
      </el-form-item>
      <el-form-item label="角色头像" prop="avatar">
        <UploadImg v-model="formData.avatar" height="60px" width="60px" />
      </el-form-item>
      <el-form-item label="绑定模型" prop="model_id" v-if="!isUser">
        <el-select v-model="formData.model_id" placeholder="请选择模型" clearable>
          <el-option
            v-for="model in models"
            :key="model.id"
            :label="model.name"
            :value="model.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="角色类别" prop="category" v-if="!isUser">
        <el-input v-model="formData.category" placeholder="请输入角色类别" />
      </el-form-item>
      <el-form-item label="角色描述" prop="description">
        <el-input type="textarea" v-model="formData.description" placeholder="请输入角色描述" />
      </el-form-item>
      <el-form-item label="角色设定" prop="system_message">
        <el-input type="textarea" v-model="formData.system_message" placeholder="请输入角色设定" />
      </el-form-item>
      <el-form-item label="引用知识库" prop="knowledge_ids">
        <el-select v-model="formData.knowledge_ids" placeholder="请选择知识库" clearable multiple>
          <el-option
            v-for="item in knowledgeList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="引用工具" prop="tool_ids">
        <el-select v-model="formData.tool_ids" placeholder="请选择工具" clearable multiple>
          <el-option v-for="item in toolList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="引用 MCP" prop="tool_ids">
        <el-select v-model="formData.mcp_client_names" placeholder="请选择 MCP" clearable multiple>
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.AI_MCP_CLIENT_NAME)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="是否公开" prop="public_status" v-if="!isUser">
        <el-radio-group v-model="formData.public_status">
          <el-radio
            v-for="dict in getBoolDictOptions(DICT_TYPE.INFRA_BOOLEAN_STRING)"
            :key="dict.value"
            :value="dict.value"
          >
            {{ dict.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="角色排序" prop="sort" v-if="!isUser">
        <el-input-number v-model="formData.sort" placeholder="请输入角色排序" class="!w-1/1" />
      </el-form-item>
      <el-form-item label="开启状态" prop="status" v-if="!isUser">
        <el-radio-group v-model="formData.status">
          <el-radio
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
            :key="dict.value"
            :value="dict.value"
          >
            {{ dict.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { KnowledgeApi, KnowledgeVO } from '@/api/ai/knowledge/knowledge'
import { ChatRoleApi, ChatRoleVO } from '@/api/ai/model/chatRole'
import { ModelApi, ModelVO } from '@/api/ai/model/model'
import { ToolApi, ToolVO } from '@/api/ai/model/tool'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import { CommonStatusEnum } from '@/utils/constants'
import { DICT_TYPE, getBoolDictOptions, getIntDictOptions, getStrDictOptions } from '@/utils/dict'
import { AiModelTypeEnum } from '@/views/ai/utils/constants'
import { FormRules } from 'element-plus'

/** AI 聊天角色 表单 */
defineOptions({ name: 'ChatRoleForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  model_id: undefined,
  name: undefined,
  avatar: undefined,
  category: undefined,
  sort: undefined,
  description: undefined,
  system_message: undefined,
  public_status: true,
  status: CommonStatusEnum.ENABLE,
  knowledge_ids: [] as number[],
  tool_ids: [] as number[],
  mcp_client_names: [] as string[]
})
const formRef = ref() // 表单 Ref
const models = ref([] as ModelVO[]) // 聊天模型列表
const knowledgeList = ref([] as KnowledgeVO[]) // 知识库列表
const toolList = ref([] as ToolVO[]) // 工具列表

/** 是否【我】自己创建，私有角色 */
const isUser = computed(() => {
  return formType.value === 'my-create' || formType.value === 'my-update'
})

const formRules = reactive<FormRules>({
  name: [{ required: true, message: '角色名称不能为空', trigger: 'blur' }],
  avatar: [{ required: true, message: '角色头像不能为空', trigger: 'blur' }],
  category: [{ required: true, message: '角色类别不能为空', trigger: 'blur' }],
  sort: [{ required: true, message: '角色排序不能为空', trigger: 'blur' }],
  description: [{ required: true, message: '角色描述不能为空', trigger: 'blur' }],
  system_message: [{ required: true, message: '角色设定不能为空', trigger: 'blur' }],
  public_status: [{ required: true, message: '是否公开不能为空', trigger: 'blur' }]
})

/** 打开弹窗 */
// TODO @fan：title 是不是收敛到 type 判断生成 title，会更合理
const open = async (type: string, id?: number, title?: string) => {
  dialogVisible.value = true
  dialogTitle.value = title || t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await ChatRoleApi.getChatRole(id)
    } finally {
      formLoading.value = false
    }
  }
  // 获得下拉数据
  models.value = await ModelApi.getModelSimpleList(AiModelTypeEnum.CHAT)
  // 获取知识库列表
  knowledgeList.value = await KnowledgeApi.getSimpleKnowledgeList()
  // 获取工具列表
  toolList.value = await ToolApi.getToolSimpleList()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as ChatRoleVO
    // tip: my-create、my-update 是 chat 角色仓库调用
    // tip: create、else 是后台管理调用
    if (formType.value === 'my-create') {
      await ChatRoleApi.createMy(data)
      message.success(t('common.createSuccess'))
    } else if (formType.value === 'my-update') {
      await ChatRoleApi.updateMy(data)
      message.success(t('common.updateSuccess'))
    } else if (formType.value === 'create') {
      await ChatRoleApi.createChatRole(data)
      message.success(t('common.createSuccess'))
    } else {
      await ChatRoleApi.updateChatRole(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    model_id: undefined,
    name: undefined,
    avatar: undefined,
    category: undefined,
    sort: undefined,
    description: undefined,
    system_message: undefined,
    public_status: true,
    status: CommonStatusEnum.ENABLE,
    knowledge_ids: [],
    tool_ids: [],
    mcp_client_names: []
  }
  formRef.value?.resetFields()
}
</script>
