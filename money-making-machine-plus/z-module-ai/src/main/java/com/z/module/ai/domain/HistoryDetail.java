package com.z.module.ai.domain;

import com.z.framework.common.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Title: HistoryDetail
 * @Package com/z/module/ai/domain/HistoryDetail.java
 * @Description: 记录用户提问及AI回复的历史记录
 * @author zhaozhiwei
 * @date 2024/9/12 15:56
 * @version V1.0
 */
@Entity
@Table(name = "ai_history_detail")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class HistoryDetail extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 历史记录id
    private Long historyId;

    // 0: 请求，1: 回答
    private int direct;

    // 请求或者回答内容
    private String content;

}
