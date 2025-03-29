package com.z.framework.ai;

import com.z.framework.ai.model.chat.*;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: AIServiceAdapter
 * @Package com/z/module/ai/adapter/AIServiceAdapter.java
 * @Description: 对接各类平台，如dify, 阿里百炼, 华为等
 * 一般来说平台分了三种类型接口, chat, flow, 智能体
 * @date 2025/3/29 11:05
 */
public interface ChatService {

    /**
     * 发送对话消息（阻塞模式）
     *
     * @param message 消息
     * @return 响应
     * @throws IOException IO异常
     */
    ChatMessageResponse sendChatMessage(ChatMessage message) throws IOException;

    /**
     * 发送对话消息（流式模式）
     * 注：Agent模式下不允许blocking
     *
     * @param message  消息
     * @throws IOException IO异常
     */
    Flux<Map<String, Object>> sendChatMessageStream(ChatMessage message) throws IOException;

    /**
     * 停止对话消息
     *
     * @param taskId 任务 ID
     * @param user   用户标识
     * @return 响应
     * @throws IOException IO异常
     */
    String stopChatMessage(String taskId, String user) throws IOException;

    /**
     * 消息反馈（点赞）
     *
     * @param messageId 消息 ID
     * @param rating    评分
     * @param user      用户标识
     * @param content   内容
     * @return 响应
     * @throws IOException IO异常
     */
    String feedbackMessage(String messageId, String rating, String user, String content) throws IOException;

    /**
     * 获取下一轮建议问题列表
     *
     * @param messageId 消息 ID
     * @param user      用户标识
     * @return 响应
     * @throws IOException IO异常
     */
    SuggestedQuestionsResponse getSuggestedQuestions(String messageId, String user) throws IOException;

    /**
     * 获取会话历史消息
     *
     * @param conversationId 会话 ID
     * @param user           用户标识
     * @param firstId        当前页第一条聊天记录的 ID
     * @param limit          一次请求返回多少条聊天记录
     * @return 响应
     * @throws IOException IO异常
     */
    MessageListResponse getMessages(String conversationId, String user, String firstId, Integer limit, String apiKey) throws IOException;

    /**
     * 获取会话列表
     *
     * @param user   用户标识
     * @param lastId 当前页最后面一条记录的 ID
     * @param limit  一次请求返回多少条记录
     * @param sortBy 排序字段
     * @return 响应
     * @throws IOException IO异常
     */
    ConversationListResponse getConversations(String user, String lastId, Integer limit, String sortBy) throws IOException;

    /**
     * 删除会话
     *
     * @param conversationId 会话 ID
     * @param user           用户标识
     * @return 响应
     * @throws IOException IO异常
     */
    String deleteConversation(String conversationId, String user) throws IOException;

    /**
     * 会话重命名
     *
     * @param conversationId 会话 ID
     * @param name           名称
     * @param autoGenerate   自动生成标题
     * @param user           用户标识
     * @return 响应
     * @throws IOException IO异常
     */
    Conversation renameConversation(String conversationId, String name, Boolean autoGenerate, String user) throws IOException;

    /**
     * 语音转文字
     *
     * @param file 文件
     * @param user 用户标识
     * @return 响应
     * @throws IOException IO异常
     */
    AudioToTextResponse audioToText(File file, String user) throws IOException;

    /**
     * 语音转文字
     *
     * @param inputStream 输入流
     * @param fileName    文件名
     * @param user        用户标识
     * @return 响应
     * @throws IOException IO异常
     */
    AudioToTextResponse audioToText(InputStream inputStream, String fileName, String user) throws IOException;

    /**
     * 文字转语音
     *
     * @param messageId 消息 ID
     * @param text      文本
     * @param user      用户标识
     * @return 音频数据
     * @throws IOException IO异常
     */
    byte[] textToAudio(String messageId, String text, String user) throws IOException;

    /**
     * 获取应用元数据
     *
     * @return 响应
     * @throws IOException IO异常
     */
    AppMetaResponse getAppMeta() throws IOException;

}
