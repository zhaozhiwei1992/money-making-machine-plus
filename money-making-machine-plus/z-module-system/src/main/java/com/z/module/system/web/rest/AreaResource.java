package com.z.module.system.web.rest;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.z.framework.common.util.GenericTreeBuilderUtil;
import com.z.module.system.domain.Area;
import com.z.module.system.repository.AreaRepository;
import com.z.module.system.web.vo.AreaVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: null.java
 * @Package com.longtu.web.resource
 * @Description: TODO
 * @date 2022/7/13 上午11:31
 */
@Tag(name = "区域维护API")
@RestController
@RequestMapping("/system")
@Transactional(rollbackFor = Exception.class)
public class AreaResource {

    private final Logger log = LoggerFactory.getLogger(AreaResource.class);

    public AreaResource(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    private final AreaRepository areaRepository;

    /**
     * {@code POST  /areas} : Create a new area.
     *
     * @param area the area to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new area, or with
     * status {@code 400 (Bad Request)} if the area has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(description = "新增区域")
    @PostMapping("/areas")
    @PreAuthorize("hasAuthority('system:area:add')")
    public Area createArea(@RequestBody Area area) throws URISyntaxException {
        if (Objects.isNull(area.getParentId())) {
            area.setParentId(0L);
        }

        return areaRepository.save(area);
    }

    /**
     * {@code GET  /areas} : get all the areas.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of areas in body.
     */
    @Operation(description = "获取所有区域")
    @GetMapping("/areas")
    @PreAuthorize("hasAuthority('system:area:view')")
    public HashMap<String, Object> getAllareas(
            Pageable pageable, Area area) {
        log.debug("REST request to get a page of UiComponents");
        //创建实例
        List<Area> allareasOrderByOrdernumAsc;
        if (StringUtils.hasText(area.getName())) {
            //创建匹配器，即如何使用查询条件
            //构建对象
            ExampleMatcher matcher = ExampleMatcher
                    .matchingAll()
                    //改变默认字符串匹配方式：模糊查询
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                    //改变默认大小写忽略方式：忽略大小写
                    .withIgnoreCase(true)
                    .withIgnoreNullValues()
                    //名字采用“开始匹配”的方式查询
                    .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.startsWith())
                    //忽略属性：是否关注。因为是基本类型，需要忽略掉
                    .withIgnorePaths("id", "createdDate", "lastModifiedDate");
            Example<Area> ex = Example.of(area, matcher);
            allareasOrderByOrdernumAsc = areaRepository.findAll(ex);
        }else{
            allareasOrderByOrdernumAsc = areaRepository.findAllByOrderByIdAsc();
        }
        final List<AreaVO> collect = allareasOrderByOrdernumAsc.stream().map(m -> {
            final AreaVO areaDTO = new AreaVO();
            BeanUtils.copyProperties(m, areaDTO);
            areaDTO.setName(areaDTO.getId() + "-" + areaDTO.getName());
            return areaDTO;
        }).collect(Collectors.toList());
        final GenericTreeBuilderUtil<AreaVO> areaDTOGenericTreeBuilderUtil = new GenericTreeBuilderUtil<>(AreaVO.class);
        final List<AreaVO> areaDTOS = areaDTOGenericTreeBuilderUtil.buildTree(collect);

        //区域属性转换成 下划线 再给前端, mapstruct? 直接采用Jackson的JsonNaming注解搞了先
        return new HashMap<String, Object>() {{
            put("list", areaDTOS);
            put("total", 0);
        }};

    }

    /**
     * @data: 2022/7/28-上午11:35
     * @User: zhaozhiwei
     * @method: getareasTree
     * @return: java.util.List<cn.hutool.core.lang.tree.Tree < java.lang.Long>>
     * @Description: 树形区域, 给layui展现, 或者配置页面挂接区域使用
     * "title": "营业厅",
     * "icon": "fa fa-desktop ",
     * // 是否默认展开
     * "spread": false,
     * "href": " ",
     * "children": [　　//二级区域 children
     * {
     * "title": "指标",
     * "icon": "fa fa-flag-checkered",
     * "href": "${pageContext.request.contextPath}/static/modules/main/setting/userList.jsp",
     * "spread": false
     * },
     */
    @Operation(description = "获取区域树")
    @GetMapping("/areas/tree")
    @PreAuthorize("hasAuthority('system:area:view')")
    public List<Tree<Long>> getareasTree() {
        log.debug("REST request to get areas Tree");

        List<Area> allareasOrderByOrdernumAsc;

        allareasOrderByOrdernumAsc = areaRepository.findAll();

        //树形结构一些特殊配置
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        // 自定义属性名 都要默认值的
        treeNodeConfig.setWeightKey("orderNum");
        treeNodeConfig.setDeep(3);

        //转换器
        List<Tree<Long>> treeNodes = TreeUtil.build(
                allareasOrderByOrdernumAsc,
                0L,
                treeNodeConfig,
                (areaObj, tree) -> {
                    tree.setId(areaObj.getId());
                    tree.setParentId(areaObj.getParentId());
                    tree.setName(areaObj.getName());
                }
        );

        //      children默认给空, 防止前端解析报错
        for (Tree<Long> treeNode : treeNodes) {
            if (Objects.isNull(treeNode.getChildren())) {
                treeNode.setChildren(Collections.emptyList());
            }
        }

        log.info("左侧树构建: {}", treeNodes);
        return treeNodes;
    }

    @Operation(description = "删除区域")
    @DeleteMapping("/areas")
    @PreAuthorize("hasAuthority('system:area:delete')")
    public String deleteArea(@RequestBody List<Long> idList) {
        log.debug("REST request to delete Examples, ids: {}", idList);
        this.areaRepository.deleteAllByIdIn(idList);
        return "success";
    }
}
