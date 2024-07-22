package com.z.module.system.web.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
public class DepartmentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;

    private Long parentId;

    private String name;

    private Integer orderNum;

    private String leader;

    private String phone;

    private String email;

    private String status;

    private List<DepartmentVO> children;
}