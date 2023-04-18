package com.z.module.system.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * A Example.
 */
@Entity
@Table(name = "t_example")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Example extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "sex")
    private String sex;

}
