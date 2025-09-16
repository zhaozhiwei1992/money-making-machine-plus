package com.z.module.system.web.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
public class AreaVO implements Serializable {
   private static final long serialVersionUID = 1L;
   private Long id;
   private String createdBy;
   private Instant createdDate;
   private String lastModifiedBy;
   private Instant lastModifiedDate;
   private String name;
   private Long parentId;
   private List<AreaVO> children;
}
