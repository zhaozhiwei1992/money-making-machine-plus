package com.z.framework.common.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Base abstract class for entities which will hold definitions for created, last modified, created by,
 * last modified by attributes.
 * <p>
 * 属性继承需要使用MappedSuperclass, 如果需要指定表列生成方式,需要使用Inheritance注解,不同生成方式表结构不同
 * Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
 * 如何使用jpa审计?
 * 1. 实体类上添加 @EntityListeners(AuditingEntityListener.class)
 * 2. 在需要的字段上加上 @CreatedDate、@CreatedBy、@LastModifiedDate、@LastModifiedBy 等注解。
 * 3. 在Xxx Application 启动类上添加 @EnableJpaAuditing
 */
// hibernate 5.2+版本不能同时使用mappedSuperclass和Inheritance注解了,坑爹
@MappedSuperclass
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@EntityListeners(AuditingEntityListener.class) // 使用审计必须有这个注解
public abstract class AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @CreatedBy
    @Column(name = "created_by", length = 50, updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private Instant createdDate = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));
}
