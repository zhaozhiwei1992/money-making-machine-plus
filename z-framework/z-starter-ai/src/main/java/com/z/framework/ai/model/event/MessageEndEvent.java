package com.z.framework.ai.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 消息结束事件
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MessageEndEvent extends BaseMessageEvent {

    /**
     * 元数据
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    /**
     * 模型用量信息
     */
    @JsonProperty("usage")
    private Map<String, Object> usage;

    /**
     * 引用和归属分段列表
     */
    @JsonProperty("retriever_resources")
    private List<Map<String, Object>> retrieverResources;
}
