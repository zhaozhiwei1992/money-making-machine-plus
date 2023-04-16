package com.example.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 定时任务配置信息\n@author zhaozhiwei
 */
@Entity
@Table(name = "sys_task_param")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TaskParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 定时任务名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 表达式
     */
    @Column(name = "cron_expression")
    private String cronExpression;

    /**
     * 定时任务入口
     */
    @Column(name = "start_class")
    private String startClass;

    /**
     * 是否启用
     */
    @Column(name = "enable")
    private Boolean enable;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TaskParam id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public TaskParam name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCronExpression() {
        return this.cronExpression;
    }

    public TaskParam cronExpression(String cronExpression) {
        this.setCronExpression(cronExpression);
        return this;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getStartClass() {
        return this.startClass;
    }

    public TaskParam startClass(String startClass) {
        this.setStartClass(startClass);
        return this;
    }

    public void setStartClass(String startClass) {
        this.startClass = startClass;
    }

    public Boolean getEnable() {
        return this.enable;
    }

    public TaskParam enable(Boolean enable) {
        this.setEnable(enable);
        return this;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskParam)) {
            return false;
        }
        return id != null && id.equals(((TaskParam) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskParam{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", cronExpression='" + getCronExpression() + "'" +
            ", startClass='" + getStartClass() + "'" +
            ", enable='" + getEnable() + "'" +
            "}";
    }
}
