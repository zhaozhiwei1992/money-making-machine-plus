package com.z.module.system.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 存储上传的信息,图片,文件等, 可以直接存库里,或者通过url读取
 */
@Entity
@Table(name = "sys_upload")
@Data
@EqualsAndHashCode(callSuper = true)
public class Upload extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 256)
    private String path;

    // 使用blob存储文件信息
    @Lob
    @Column(columnDefinition = "MediumBlob")
    private byte[] value;

    // 文件名
    private String name;

    // 扩展名, jpg, txt等
    private String ext;
}
