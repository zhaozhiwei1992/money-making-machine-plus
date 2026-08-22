# 计划：AI 模块依赖瘦身 → litellm 单一路由 + agent-scope java 引入（第一期）

> 用户原始任务：AI 相关依赖太多、模型接入"花里胡哨"。已拍板：① 通过 litellm 中转，所有模型统一走 OpenAI 兼容格式接口；② 删掉其他各厂商 starter / 自研适配。
> 已确认边界：向量库保留（后续测不同库）、图片/PPT/音乐（Midjourney/Suno/PPT）作为独立业务保留、工作流两种方案（TinyFlow 保留 + 引入 agent-scope java）。
> 由 pi-plan 只读探索生成，未改动任何源码。执行用 /implement 本文件路径。
> **硬性节奏**：每次改动小、可 review；每步编译/测试通过才进下一步；每步可独立提交/回滚。

## 1. 背景与目标

**一句话目标**：把 AI 模块的 20 平台多厂商接入收敛为"litellm 中转 + OpenAI 格式单路由"：只保留 spring-ai openai starter（base-url 指向 litellm），删除其他 10 个模型 starter + MCP；代码层 `AiModelFactoryImpl` / `AiUtils` 大 switch 收敛；同时为"智能体驱动工作流"引入 agent-scope java（本期仅依赖引入 + 最小装配，完整业务后续期）。

**验收标准（可验证）**：
- `AiModelFactoryImpl` / `AiUtils` 不再 import 任何被删 starter 的类，编译通过
- `z-module-ai` / `z-starter-ai` pom 中模型类依赖只剩 openai（+ 向量库三件套 + Tika + TinyFlow）
- `mvn -o -pl z-public-server -am test` 全绿（含聚合 verify）
- `AiPlatformEnum` 收敛到业务子集（OPENAI + MIDJOURNEY + SUNO），DB 存量数据有迁移/兼容策略
- agent-scope 坐标可在线解析（内网 Nexus 验证），`z-starter-ai` 引入后编译 + 上下文启动通过

**本期不做**：agent-scope 完整智能体业务、图片/PPT/音乐业务开发、screen/system/ai 分层违规修复（已入账遗留债务）。

## 2. 现状（只读探索结果）

**AI 依赖爆炸全景（4 来源，均在 `z-module-ai/pom.xml` + `z-framework/z-starter-ai/pom.xml`）**：

| 来源 | 依赖 | 处置 |
|------|------|------|
| Spring AI 官方（9 模型 starter） | model-openai | ✅ 保留（litellm 单路由） |
| | model-azure-openai / anthropic / deepseek / ollama / stability-ai / zhipuai / minimax | ❌ 删 |
| | model-qdrant / redis / milvus 向量库 | ✅ 保留（边界①） |
| alibaba | spring-ai-alibaba-starter-dashscope | ❌ 删 |
| springaicommunity | qianfan / moonshot（各 1.0.0 非官方） | ❌ 删 |
| 其他 | Tika 文档解析 / TinyFlow / jsonschema-generator + victools | ✅ 保留 |
| | spring-ai-starter-mcp-server-webmvc / mcp-client（yml enabled: false） | ❌ 删（边界④推荐） |
| z-starter-ai 自建 | dashscope-sdk-java（仅 BaiLianServiceAdapter 用，yml type=dify 未启用） | ❌ 删 |
| | dify-java-client + webflux | ✅ 保留（ChatResource 走 DifyServiceAdapter） |

**代码层联动点（删除依赖后必须同步收敛）**：

1. `framework/ai/core/model/AiModelFactoryImpl` —— **引用爆炸点**：import 覆盖所有被删 starter 类（DashScopeApi/ChatModel/EmbeddingModel/ImageModel、QianFanApi/ChatModel/EmbeddingModel/ImageModel、MoonshotApi/ChatModel、AnthropicApi/ChatModel、AzureOpenAiChatModel/EmbeddingModel、DeepSeekApi/ChatModel、OllamaApi/ChatModel/EmbeddingModel、MiniMaxApi/ChatModel/EmbeddingModel、StabilityAiApi/ImageModel、ZhiPuAiApi/ChatModel/ImageApi），约 20 平台 300+ 行 switch、169 处 case/return。
2. `utils/AiUtils.buildChatOptions` —— 20 平台分支各构造不同 ChatOptions（DashScopeChatOptions / QianFanChatOptions / MoonshotChatOptions / AnthropicChatOptions / AzureOpenAiChatOptions / DeepSeekChatOptions / MiniMaxChatOptions / OllamaOptions / ZhiPuAiChatOptions / OpenAiChatOptions），删依赖后只剩 OpenAiChatOptions。
3. `enums/model/AiPlatformEnum` —— 20 枚举（TongYi/YiYan/DeepSeek/ZhiPu/XingHuo/DouBao/HunYuan/SiliconFlow/MiniMax/Moonshot/BaiChuan/OpenAI/AzureOpenAI/Anthropic/Gemini/Ollama/StableDiffusion/Midjourney/Suno…）。**DB 契约**：`domain/model/AiApiKeyDO.platform` 存枚举字符串，前端 ApiKey 下拉/保存依赖。
4. 自研适配包 `framework/ai/core/model/` 下 9 个：
   - 删：baichuan / doubao / gemini / hunyuan / siliconflow（+ xinghuo 包内的 `XingHuoChatModel`）—— 仅依赖 openai 核心类，删除不影响编译，但收敛后无存在意义；
   - **保留：`xinghuo/api/XunFeiPptApi`**（PPT 业务！在待删的 xinghuo 包内，需移出或仅删 ChatModel）、midjourney / suno / wenduoduo（独立业务 HTTP 客户端，不依赖模型 starter）。
5. `config/AIAutoConfiguration`（z-starter-ai）—— 自动装配点涉及 dashscope/openai bean，需调整。
6. `application.yml`：`spring.ai` 段含 openai（base-url=api.gptsapi.net 中转）、azure/anthropic/ollama/stabilityai/dashscope/minimax/moonshot/deepseek/qianfan/zhipuai 配置 + 多个真实 api-key；`spring.ai.model.rerank`（dashscope 关联）；`spring.ai.mcp` 段（server/client enabled: false）。

**agent-scope java 调研结论**：
- 坐标：`io.agentscope:agentscope` v2.0.0（all-in-one）；v2.0.1 推荐 `agentscope-harness` 入口 + `agentscope-extensions-model-openai` 扩展（官方明确支持 OpenAI Chat Completions 风格 + OpenAI 兼容端点 DeepSeek/GLM/Kimi/MiniMax 等，且有专门 litellm 集成示例）→ **可直接对接 litellm**。
- 要求 **JDK 17+**（项目 JDK21 ✅）；v2 HarnessAgent 特性：workspace / 长期记忆 / session 持久化 / 子智能体 / 沙箱；v1 自定义工作流基于 Spring AI Alibaba StateGraph；官方有 dify 集成/对比页（做"用户画工作流"是 TinyFlow，做"智能体驱动"是 agent-scope，两条线不冲突）。
- **风险前置**：本地 `~/.m2` 无 `io/agentscope`，首次引入必须在线拉取（项目默认 `-o` 离线构建会失败）→ **步骤 0 先验证内网 Nexus 是否有该 artifact**；失败则暂停 agent-scope，仅完成瘦身。

## 3. 实施步骤（每步小改动 + 独立验证）

> 删除 pom 依赖后编译必然红——这是预期状态，步骤 2-5 在同一 commit 内收尾恢复绿色。

- **步骤 0（前置验证，只读）**：验证内网 Nexus 能解析 `io.agentscope:agentscope-harness:2.0.1` + `agentscope-extensions-model-openai:2.0.1`（`mvn dependency:get` 或 curl 仓库查询）；同时确认 AiPlatformEnum → 前端下拉的暴露接口（`/ai/platform/...`）引用点，供步骤 2 定迁移范围。
- **步骤 1**：`z-module-ai/pom.xml` 删除依赖：spring-ai model-azure-openai/anthropic/deepseek/ollama/stability-ai/zhipuai/minimax + alibaba dashscope + qianfan/moonshot + MCP 两件。保留 openai / qdrant / redis / milvus / Tika / TinyFlow / jsonschema。
- **步骤 2**：`AiPlatformEnum` 收敛：删 15+ 非业务枚举，保留 **OPENAI / MIDJOURNEY / SUNO**（图片/音乐业务）+ 兼容处理：`validatePlatform` 对 DB 存量旧值（TongYi 等）给出迁移提示或老值映射；前端下拉随之只显示 3 项（若前端有硬编码平台列表需同步，见步骤 0 确认）。**此步骤依赖用户确认方案 A（保留枚举兼容迁移）还是方案 B（直接删 + Liquibase 清表）**——见"待确认"。
- **步骤 3**：`AiModelFactoryImpl` 重写收敛：
  - `getOrCreateChatModel` / `getDefaultChatModel` → 仅 openai 分支（`buildOpenAiChatModel`，base-url/api-key 走配置）；
  - `getOrCreateImageModel` / `getDefaultImageModel` → 仅 openai 分支（`buildOpenAiImageModel`）；
  - Embedding → 仅 openai 分支；Vector store 分支保留（qdrant/milvus/redis + SimpleVectorStore 兜底）；
  - `getOrCreateMidjourneyApi` / `getOrCreateSunoApi` / PPT Api 保留（业务）；
  - 删全部其他平台 build 方法（约 15 个）+ 相关 import。
- **步骤 4**：`AiUtils.buildChatOptions` 收敛为单分支 OpenAiChatOptions；联动修订 `AiModelService`（embedding/vector store 逻辑，SimpleVectorStore 兜底保留、qdrant 保持注释）、`AiChatMessageService` 等所有引用被删枚举/类的地方。
- **步骤 5**：自研适配包处置：删 `baichuan / doubao / gemini / hunyuan / siliconflow` + `xinghuo` 包内 `XingHuoChatModel`；**将 `xinghuo/api/XunFeiPptApi` 移至独立业务包**（如 `framework/ai/biz/ppt/` 或保留原包仅删 ChatModel——以最小侵入为准，见实施时定）；`z-starter-ai` 删 dashscope-sdk-java（连 BaiLianServiceAdapter 或仅删依赖，以 yml type=dify 为准）并调整 `AIAutoConfiguration` 装配点。
- **步骤 6**：`application.yml` `spring.ai` 段清理：删 azure/anthropic/ollama/stabilityai/dashscope/minimax/moonshot/deepseek/qianfan/zhipuai 配置与 key；openai 段 base-url 改指向 litellm（地址待用户提供）、api-key 换 litellm master key；删 `spring.ai.model.rerank` 与 `spring.ai.mcp` 段。
- **步骤 7（验证）**：`mvn -o -pl z-module-ai -am clean package` 专项 + `mvn -o -pl z-public-server -am test` 全量（含 19 模块守卫测试）。
- **步骤 8（agent-scope，依赖步骤 0 验证通过）**：`z-starter-ai/pom.xml` 加 `agentscope-harness` 2.0.1 + `agentscope-extensions-model-openai` 2.0.1；最小装配：一个 `AgentScopeAutoConfiguration`（HarnessAgent + litellm OpenAI 端点），在线构建（不带 `-o`）编译 + 上下文启动验证。完整智能体业务 → 后续期。
- **步骤 9**：架构记录：`~/workspace/项目管理/开发文档/赚钱工具/00_项目概览/08_AI依赖瘦身与单一路由实施记录.org`；提示用户确认记忆落盘（本次拍板边界）。

## 4. 风险与回滚

| 风险 | 应对 |
|------|------|
| 删除依赖后编译大面积红（预期） | 步骤 1-5 同 commit 收尾，按 import 错误逐个收敛，不遗留未用 import |
| AiPlatformEnum 收敛破坏 DB 存量数据 / 前端下拉 | 步骤 0 先确认接口引用面；步骤 2 用兼容策略（老值提示或迁移）；待用户确认方案 A/B |
| 误删业务类（XunFeiPptApi 在 xinghuo 包内） | 步骤 5 明确移出/仅删 ChatModel，保留业务类 |
| agent-scope 首次拉取失败（内网 Nexus 无 artifact） | 步骤 0 前置验证，失败则本期只做瘦身，agent-scope 后续再引 |
| yml 敏感 key 清理不净 | 步骤 6 全段替换，git diff 自检确认无残留 key |
| litellm 地址/key 未提供 | 步骤 6 前向用户要 base-url 与 master key（占位值不可提交） |

**回滚**：每步独立提交（用户自行 git commit），任一步失败可单独 revert 该 commit；pom 删除前状态可随时还原。

## 5. 待确认（实施开始前）

1. **AiPlatformEnum 收敛方案**：A=保留枚举值但标注废弃 + 实现统一 openai（DB 零迁移）/ B=删枚举 + Liquibase 迁移（推荐，符合"不要花里胡哨"语义，但需确认 DB 中 AiApiKey 数据可否清/迁）。
2. **litellm openai 段地址与 master key**（开放给 OpenAI 中转的 base-url、key；不落库，放 yml 或环境变量）。
3. agent-scope 引入时机：本期步骤 8 就要，还是先瘦身绿了再单独规划？（推荐本期只做依赖 + 最小装配）
4. MCP 双 starter 删除（已推荐删，yml 未启用，无引用）——如保留请说明用途。