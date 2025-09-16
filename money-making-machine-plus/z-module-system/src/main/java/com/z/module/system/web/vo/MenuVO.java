package com.z.module.system.web.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
public class MenuVO implements Serializable {
   private static final long serialVersionUID = 1L;
   private Long id;
   private String createdBy;
   private Instant createdDate;
   private String lastModifiedBy;
   private Instant lastModifiedDate;
   private String url;
   private String name;
   private String iconCls;
   private Integer orderNum;
   private Boolean keepAlive;
   private Boolean requireAuth;
   private Long parentId;
   private Boolean enabled;
   private String config;
   private String component;
   private String template;
   private Integer menuType;
   private String permissionCode;
   private List<MenuVO> children;
}
