package com.z.module.system.web.rest;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.z.framework.common.util.GenericTreeBuilderUtil;
import com.z.module.system.domain.Department;
import com.z.module.system.repository.DepartmentRepository;
import com.z.module.system.web.mapper.DepartmentSelectMapper;
import com.z.module.system.web.vo.DepartmentVO;
import com.z.framework.common.web.vo.SelectOptionVO;
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
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "部门维护API")
@RestController
@RequestMapping("/system")
@Transactional(rollbackFor = Exception.class)
public class DepartmentResource {

    private final Logger log = LoggerFactory.getLogger(DepartmentResource.class);

    private final DepartmentRepository departmentRepository;

    public DepartmentResource(DepartmentRepository departmentRepository, DepartmentSelectMapper departmentSelectMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentSelectMapper = departmentSelectMapper;
    }

    /**
     * {@code POST  /departments} : Create a new department.
     *
     * @param department the department to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new department, or with
     * status {@code 400 (Bad Request)} if the department has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(description = "新增部门")
    @PostMapping("/departments")
    @PreAuthorize("hasAuthority('system:dept:add')")
    public Department createDepartment(@RequestBody Department department) throws URISyntaxException {

        ExampleMatcher matcher = ExampleMatcher.matching();
        final Department filterObj = new Department();
        if (Objects.isNull(department.getId())) {
            final Example<Department> of = Example.of(filterObj, matcher);
            final long count = departmentRepository.count(of);
            department.setOrderNum(Integer.parseInt(String.valueOf(count + 1)));
        }

        if (Objects.isNull(department.getParentId())) {
            department.setParentId(0L);
        }

        Department result = departmentRepository.save(department);
        return result;
    }

    /**
     * {@code GET  /departments} : get all the departments.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of departments in body.
     */
    @Operation(description = "获取所有部门")
    @GetMapping("/departments")
    @PreAuthorize("hasAuthority('system:dept:view')")
    public HashMap<String, Object> getAllDepartments(
            Pageable pageable, Department department) {
        log.debug("REST request to get a page of UiComponents");

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

        //创建实例
        Example<Department> ex = Example.of(department, matcher);
        final List<Department> allDepartmentsOrderByOrderNumAsc = departmentRepository.findAll(ex);
        final List<DepartmentVO> collect = allDepartmentsOrderByOrderNumAsc.stream().map(m -> {
            final DepartmentVO departmentDTO = new DepartmentVO();
            BeanUtils.copyProperties(m, departmentDTO);
            return departmentDTO;
        }).collect(Collectors.toList());
        final GenericTreeBuilderUtil<DepartmentVO> departmentDTOGenericTreeBuilderUtil = new GenericTreeBuilderUtil<>(DepartmentVO.class);
        final List<DepartmentVO> departmentDTOS = departmentDTOGenericTreeBuilderUtil.buildTree(collect);

        //部门属性转换成 下划线 再给前端, mapstruct? 直接采用Jackson的JsonNaming注解搞了先
        return new HashMap<String, Object>() {{
            put("list", departmentDTOS);
            put("total", 0);
        }};
    }

    @Operation(description = "获取部门树")
    @GetMapping("/departments/tree")
    @PreAuthorize("hasAuthority('system:dept:view')")
    public List<Tree<Long>> getDepartmentsTree() {
        log.debug("REST request to get Departments Tree");

        List<Department> allDepartmentsOrderByOrdernumAsc = departmentRepository.findAllByOrderByOrderNumAsc();

        //树形结构一些特殊配置
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        // 自定义属性名 都要默认值的
        treeNodeConfig.setWeightKey("orderNum");
        treeNodeConfig.setDeep(3);

        //转换器
        List<Tree<Long>> treeNodes = TreeUtil.build(
                allDepartmentsOrderByOrdernumAsc,
                0L,
                treeNodeConfig,
                (departmentObj, tree) -> {
                    tree.setId(departmentObj.getId());
                    tree.setParentId(departmentObj.getParentId());
                    tree.setName(departmentObj.getName());
                    tree.putExtra("title", departmentObj.getName());
                    // 防止index相同导致department全部展开
                    if (departmentObj.getParentId() == 0) {
                        tree.putExtra("index", departmentObj.getId());
                    }
                    tree.putExtra("ordernum", departmentObj.getOrderNum());
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

    /**
     * {@code GET  /departments/:id} : get the "id" department.
     *
     * @param id the id of the department to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the department, or with status {@code
     * 404 (Not Found)}.
     */
    @GetMapping("/departments/{id}")
    @PreAuthorize("hasAuthority('system:dept:view')")
    public Department getDepartment(@PathVariable Long id) {
        log.debug("REST request to get Department : {}", id);
        Optional<Department> department = departmentRepository.findById(id);
        return department.get();
    }

    @Operation(description = "删除部门")
    @DeleteMapping("/departments")
    @PreAuthorize("hasAuthority('system:dept:delete')")
    public String deleteDepartment(@RequestBody List<Long> idList) {
        log.debug("REST request to delete Examples, ids: {}", idList);
        this.departmentRepository.deleteAllByIdIn(idList);
        return "success";
    }

    private final DepartmentSelectMapper departmentSelectMapper;

    @Operation(description = "获取部门树")
    @GetMapping("/departments/select")
    @PreAuthorize("hasAuthority('system:dept:view')")
    public List<SelectOptionVO> getDepartmentsSelect() {
        log.debug("REST request to get Position Select");

        List<Department> departmentList = departmentRepository.findAll();
        final List<SelectOptionVO> convert = departmentSelectMapper.convert(departmentList);
        final GenericTreeBuilderUtil<SelectOptionVO> genericTreeBuilderUtil = new GenericTreeBuilderUtil<>(SelectOptionVO.class);
        final List<SelectOptionVO> list = genericTreeBuilderUtil.buildTree(convert);

        log.info("左侧树构建: {}", list);
        return list;
    }
}
