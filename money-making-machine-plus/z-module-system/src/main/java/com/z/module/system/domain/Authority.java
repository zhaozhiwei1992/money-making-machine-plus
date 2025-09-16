package com.z.module.system.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * An authority (a security role) used by Spring Security.
 * 角色
 */
@Entity
@Table(name = "sys_authority")
@Data
@EqualsAndHashCode(callSuper = true)
public class Authority extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @NotNull
    @Size(max = 50)
    @Column(length = 50)
    private String code;

    @NotNull
    @Size(max = 50)
    @Column(length = 50)
    private String name;
}
