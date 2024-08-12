package com.z.module.system.web.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
public class RoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;

    private String code;

    private String name;

    // 挂接菜单id集合
    private List<List<String>> menuIdList;
}
