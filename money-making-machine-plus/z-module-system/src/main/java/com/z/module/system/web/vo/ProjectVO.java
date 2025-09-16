package com.z.module.system.web.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectVO {
    private String name;
    private String icon;
    private String message;
    private String personal;
    private Date time; // 注意：Java中的Date类型与JavaScript中的Date类型不完全相同
}