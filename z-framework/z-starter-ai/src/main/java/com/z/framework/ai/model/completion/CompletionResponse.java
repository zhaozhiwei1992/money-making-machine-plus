package com.z.framework.ai.model.completion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 文本生成响应（阻塞模式）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletionResponse {
    /**
     * 消息唯一 ID
     */
    private String id;

    /**
     * App 模式，固定为 chat
     */
    private String mode;

    /**
     * 完整回复内容
     */
    private String answer;

    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    /**
     * 模型用量信息
     */
    private Usage usage;

    /**
     * 引用和归属分段列表
     */
    private Object retrieverResources;

    /**
     * 消息创建时间戳
     */
    private Long createdAt;

    /**
     * 模型用量信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        /**
         * 提示 tokens
         */
        private Integer promptTokens;

        /**
         * 完成 tokens
         */
        private Integer completionTokens;

        /**
         * 总 tokens
         */
        private Integer totalTokens;
    }
}
