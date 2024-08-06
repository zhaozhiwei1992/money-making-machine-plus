package com.z.module.system.web.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: SelectOptionVO
 * @Package com/z/module/system/web/vo/SelectOptionVO.java
 * @Description: 适配前台展现的下拉框
 * options: [
 * {
 * label: '重要',
 * value: 3,
 * children: [
 * {
 * label: '重要',
 * value: 3
 * },
 * {
 * label: '良好',
 * value: 2
 * },
 * {
 * label: '一般',
 * value: 1
 * }
 * ]
 * },
 * {
 * label: '良好',
 * value: 2
 * },
 * {
 * label: '一般',
 * value: 1
 * }
 * ]
 * 基础要素特殊处理, 使用ele_code作为父子级关系
 * @date 2024/7/25 下午1:23
 */
@Data
public class EleSelectOptionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String parentId;

    private String label;

    private String value;

    private List<EleSelectOptionVO> children;
}
