package com.z.module.system.domain;

import com.z.framework.common.config.Constants;
import com.z.framework.common.domain.AbstractAuditingEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
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
@Table(name = "t_user")
@Data
public class User extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private boolean activated = false;

    @Size(max = 256)
    @Column(name = "image_url", length = 256)
    private String imageUrl;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "t_user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id"
                    , foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))},
            inverseJoinColumns = {@JoinColumn(name = "authority_code", referencedColumnName = "code"
                    , foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT)
            )}
    )
    @BatchSize(size = 20)
    private Set<Authority> authorities = new HashSet<>();

    @Column(name = "appid", length = 20)
    private String appid;

    @Size(min = 9, max = 9, message = "区划只能为9位")
    @Column(name = "mof_div_code", length = 9)
    private String mofDivCode;
}
