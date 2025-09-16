package com.z.module.system.web.rest;

import com.z.module.system.domain.Permission;
import com.z.module.system.repository.PermissionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "权限API")
@RestController
@RequestMapping("/system")
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class PermissionResource {

    private final PermissionRepository permissionRepository;

    public PermissionResource(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    /**
     * {@code POST  /admin/permissions}  : Creates a new role.
     * <p>
     * Creates a new role if the login and email are not already used, and sends an
     * mail with an activation link.
     * The role needs to be activated on creation.
     *
     * @param permission the role to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new role, or with
     * status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(description = "新增权限")
    @PostMapping("/permissions")
    public Permission createPermission(@RequestBody Permission permission) throws URISyntaxException {
        log.debug("REST request to save Permission : {}", permission);

        Permission newPermission = permissionRepository.save(permission);

        return newPermission;
    }

    /**
     * {@code GET /admin/permissions} : get all permissions with all the details - calling this are only allowed for the
     * administrators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all permissions.
     */

    @Operation(description = "获取权限")
    @GetMapping("/permissions")
    public HashMap<String, Object> getAllPermissions(Pageable pageable, Permission role) {
        log.debug("REST request to get all Permission for an admin");

//        final List<Permission> all = roleRepository.findAll();
        // 根据id, 升序
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), sort);

        Page<Permission> permissionPage;
        //创建匹配器，即如何使用查询条件
        //构建对象
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                //改变默认字符串匹配方式：模糊查询
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                //改变默认大小写忽略方式：忽略大小写
                .withIgnoreCase(true)
                //名字采用“开始匹配”的方式查询
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.startsWith())
                //忽略属性：是否关注。因为是基本类型，需要忽略掉
                .withIgnorePaths("id", "createdDate", "lastModifiedDate");

        //创建实例
        Example<Permission> ex = Example.of(role, matcher);
        permissionPage = permissionRepository.findAll(ex, pageable);

        return new HashMap<String, Object>() {{
            put("list", permissionPage.getContent());
            put("total", Long.valueOf(permissionPage.getTotalElements()).intValue());
        }};
    }

    @Operation(description = "删除权限")
    @DeleteMapping("/permissions")
    public String deletePermission(@RequestBody List<Long> idList) {
        log.debug("REST request to delete Examples, ids: {}", idList);
        this.permissionRepository.deleteAllByIdIn(idList);
        return "success";
    }

    @Operation(description = "获取权限列表信息")
    @GetMapping("/permissions/list")
    public List<Map<String, Object>> getAllDictList() {
        final List<Permission> all = permissionRepository.findAll();
        final List<Map<String, Object>> resultMap = all.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("value", m.getCode());
            map.put("label", m.getName());
            return map;
        }).collect(Collectors.toList());
        return resultMap;
    }

}
