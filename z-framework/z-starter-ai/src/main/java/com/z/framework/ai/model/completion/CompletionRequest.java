package com.z.framework.ai.model.completion;

import com.z.framework.ai.enums.ResponseMode;
import com.z.framework.ai.model.file.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 文本生成请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletionRequest {
    /**
     * 输入参数，允许传入 App 定义的各变量值
     */
    private Map<String, Object> inputs;

    /**
     * 响应模式
     */
    private ResponseMode responseMode;

    /**
     * 用户标识
     */
    private String user;

    /**
     * 文件列表
     */
    private List<FileInfo> files;
}
