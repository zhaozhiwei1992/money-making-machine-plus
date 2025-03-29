package com.z.framework.ai;

import com.z.framework.ai.model.workflow.*;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Map;

/**
 * @Title: AIServiceAdapter
 * @Package com/z/module/ai/adapter/AIServiceAdapter.java
 * @Description: 对接各类平台，如dify, 阿里百炼, 华为等
 * 一般来说平台分了三种类型接口, chat, flow, 智能体
 * @author zhaozhiwei
 * @date 2025/3/29 11:05
 * @version V1.0
 */
public interface WorkflowService {

    /**
     * 执行工作流（阻塞模式）
     *
     * @param request 请求
     * @return 响应
     * @throws IOException IO异常
     
     */
    WorkflowRunResponse runWorkflow(WorkflowRunRequest request) throws IOException;

    /**
     * 执行工作流（流式模式）
     *
     * @param request  请求
     * @throws IOException IO异常
     
     */
    Flux<Map<String, Object>> runWorkflowStream(WorkflowRunRequest request) throws IOException;

    /**
     * 停止工作流
     *
     * @param taskId 任务 ID
     * @param user   用户标识
     * @return 响应
     * @throws IOException IO异常
     
     */
    WorkflowStopResponse stopWorkflow(String taskId, String user) throws IOException;

    /**
     * 获取工作流执行情况
     *
     * @param workflowId 工作流ID
     * @return 执行情况
     * @throws IOException IO异常
     
     */
    WorkflowRunStatusResponse getWorkflowRun(String workflowId) throws IOException;

    /**
     * 获取工作流日志
     *
     * @param keyword 关键字
     * @param status  状态
     * @param page    页码
     * @param limit   每页条数
     * @return 日志列表
     * @throws IOException IO异常
     
     */
    WorkflowLogsResponse getWorkflowLogs(String keyword, String status, Integer page, Integer limit) throws IOException;

}
