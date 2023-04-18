package com.z.framework.job.web.vm;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskParamVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 定时任务名称
     */
    private String name;

    /**
     * 表达式
     */
    private String cronExpression;

    /**
     * 定时任务入口
     */
    private String startClass;

    /**
     * 是否启用
     */
    private String enable;

}
