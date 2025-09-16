package com.z.module.ui.web.vo;

import cn.hutool.core.lang.tree.Tree;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * A UiEditform.
 */
@Data
public class UiEditFormVO implements Serializable {

    private Long id;

    private Long menuId;

    private String code;

    private String name;

    private Integer orderNum;

    private Boolean isSource;

    private String source;

    private Boolean isEdit;

    private Boolean requirement;

    private String type;

    private String placeholder;

    /**
     * @data: 2022/5/8-下午12:14
     * @User: zhaozhiwei
     * @method:
     * @param null :
     * @return:
     * @Description: 一些特殊属性传入前端
     */
    private Map<String, Object> config;

    /**
     * @data: 2022/5/8-下午12:13
     * @User: zhaozhiwei
     * @method:
     * @param null :
     * @return:
     * @Description: 增加前端数据下拉值集
     */
    private List<Tree<Long>> mapping;
}
