package com.z.module.system.domain;

import com.z.framework.common.config.Constants;
import com.z.framework.common.domain.AbstractAuditingEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.z.module.system.domain.listener.UserListener;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import jakarta.persistence.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A user.
 */
@Entity
@Table(name = "sys_user")
@Data
@EqualsAndHashCode(callSuper = true)
@EntityListeners(UserListener.class)
public class User extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @NotNull
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    private String login;

    //    @JsonIgnore
    @NotNull
//    @Size(min = 60, max = 60)
    @Size(max = 60)
    @Column(name = "password_hash", length = 60, nullable = false)
    private String password;

    @Size(max = 50)
    @Column(name = "name", length = 50)
    private String name;

    @Column(nullable = false)
    private boolean activated = true;

    // 去附件中读取头像信息
    private Long avatar;

    private String email;

    @Column(name = "phone_number", length = 11)
    private String phoneNumber;
}
