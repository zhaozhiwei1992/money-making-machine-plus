package com.example.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 系统参数信息\n\n一些需要经常性手动调整, 跟业务相关的配置放这里\n程序相关的可以方式spring配置文件\n\n@author zhaozhiwei
 */
@Entity
@Table(name = "sys_param")
@Data
public class SystemParam extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置编码\n\n使用时通过编码获取参数
     */
    @Column(name = "code")
    private String code;

    /**
     * 配置名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 参数信息\n可以是普通value或者json等，使时自行解析
     */
    @Column(name = "value")
    private String value;

    /**
     * 备注\n根据需要对参数更细致的描述
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 是否启用
     */
    @Column(name = "enable")
    private Boolean enable;

}
