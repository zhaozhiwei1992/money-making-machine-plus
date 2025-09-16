package com.z.framework.common.web.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Title: SelectOptionVO
 * @Package com/z/module/system/web/vo/SelectOptionVO.java
 * @Description: 适配前台展现的下拉框
 *         options: [
 *           {
 *             label: '重要',
 *             value: 3,
 *             children: [
 *               {
 *                 label: '重要',
 *                 value: 3
 *               },
 *               {
 *                 label: '良好',
 *                 value: 2
 *               },
 *               {
 *                 label: '一般',
 *                 value: 1
 *               }
 *             ]
 *           },
 *           {
 *             label: '良好',
 *             value: 2
 *           },
 *           {
 *             label: '一般',
 *             value: 1
 *           }
 *         ]
 * @author zhaozhiwei
 * @date 2024/7/25 下午1:23
 * @version V1.0
 */
@Data
public class SelectOptionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long parentId;

    private String label;

    private String value;

    private List<SelectOptionVO> children;
}
