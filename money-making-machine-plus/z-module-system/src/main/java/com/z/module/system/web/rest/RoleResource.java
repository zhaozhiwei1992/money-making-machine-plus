package com.z.module.system.web.rest;

import com.z.module.system.domain.Authority;
import com.z.module.system.domain.RoleMenu;
import com.z.module.system.repository.AuthorityRepository;
import com.z.module.system.repository.RoleMenuRepository;
import com.z.module.system.web.mapper.RoleSelectMapper;
import com.z.module.system.web.vo.RoleVO;
import com.z.framework.common.web.vo.SelectOptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "角色API")
@RestController
@RequestMapping("/system")
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class RoleResource {

    private static final String ROLE_START = "ROLE_";

    private final AuthorityRepository roleRepository;

    public RoleResource(AuthorityRepository roleRepository, RoleMenuRepository roleMenuRepository,
                        RoleSelectMapper roleSelectMapper) {
        this.roleRepository = roleRepository;
        this.roleMenuRepository = roleMenuRepository;
        this.roleSelectMapper = roleSelectMapper;
    }

    /**
     * {@code POST  /admin/roles}  : Creates a new role.
     * <p>
     * Creates a new role if the login and email are not already used, and sends an
     * mail with an activation link.
     * The role needs to be activated on creation.
     *
     * @param roleVO the role to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new role, or with
     * status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(description = "新增角色")
    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('system:role:add')")
    public Authority createAuthority(@RequestBody RoleVO roleVO) throws URISyntaxException {
        log.debug("REST request to save Authority : {}", roleVO);

        if (!roleVO.getCode().startsWith(ROLE_START)) {
            roleVO.setCode(ROLE_START + roleVO.getCode());
        }

        final Authority authority = new Authority();
        BeanUtils.copyProperties(roleVO, authority);

        Authority newAuthority = roleRepository.save(authority);

        // 保存角色菜单信息
        roleMenuRepository.deleteAllByRoleIdIn(Arrays.asList(newAuthority.getId()));
        final List<List<String>> menuIdListString = roleVO.getMenuIdList();
        final Set<Long> menuIdList = new HashSet<>();
        menuIdListString.forEach(menuIds -> menuIdList.addAll(menuIds.stream().map(Long::valueOf).collect(Collectors.toList())));
        final List<RoleMenu> roleMenus = menuIdList.stream().map(menuId -> {
            final RoleMenu roleMenu = new RoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(authority.getId());
            return roleMenu;
        }).collect(Collectors.toList());
        roleMenuRepository.saveAll(roleMenus);

        return newAuthority;
    }

    private final RoleMenuRepository roleMenuRepository;

    /**
     * {@code GET /admin/roles} : get all roles with all the details - calling this are only allowed for the
     * administrators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all roles.
     */

    @Operation(description = "获取角色")
    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('system:role:view')")
    public HashMap<String, Object> getAllAuthoritys(Pageable pageable, Authority role) {
        log.debug("REST request to get all Authority for an admin");

//        final List<Authority> all = roleRepository.findAll();
        // 根据id, 升序
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), sort);

        Page<Authority> rolePage;
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
        Example<Authority> ex = Example.of(role, matcher);
        rolePage = roleRepository.findAll(ex, pageable);
        final List<Authority> content = rolePage.getContent();

        // 获取关联菜单信息
        final List<RoleMenu> roleMenuList = roleMenuRepository.findByRoleIdIn(content.stream().map(Authority::getId).collect(Collectors.toList()));
        final List<RoleVO> roleVOList = content.stream().map(authority -> {
            final RoleVO roleVO = new RoleVO();
            BeanUtils.copyProperties(authority, roleVO);
            final List<RoleMenu> collect = roleMenuList.stream().filter(roleMenu -> roleMenu.getRoleId().equals(roleVO.getId())).collect(Collectors.toList());
            roleVO.setMenuIdList(Arrays.asList(collect.stream().map(m -> String.valueOf(m.getMenuId())).collect(Collectors.toList())));
            return roleVO;
        }).collect(Collectors.toList());

        return new HashMap<String, Object>() {{
            put("list", roleVOList);
            put("total", Long.valueOf(rolePage.getTotalElements()).intValue());
        }};
    }

    @Operation(description = "删除角色")
    @DeleteMapping("/roles")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public String deleteAuthority(@RequestBody List<Long> idList) {
        log.debug("REST request to delete Examples, ids: {}", idList);
        this.roleRepository.deleteAllByIdIn(idList);
        return "success";
    }

    @Operation(description = "获取角色列表信息")
    @GetMapping("/roles/list")
    @PreAuthorize("hasAuthority('system:role:view')")
    public List<Map<String, Object>> getAllDictList() {
        final List<Authority> all = roleRepository.findAll();
        final List<Map<String, Object>> resultMap = all.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("value", m.getCode());
            map.put("label", m.getName());
            return map;
        }).collect(Collectors.toList());
        return resultMap;
    }

    private final RoleSelectMapper roleSelectMapper;

    @Operation(description = "获取角色树")
    @GetMapping("/roles/select")
    @PreAuthorize("hasAuthority('system:role:view')")
    public List<SelectOptionVO> getRolesSelect() {
        log.debug("REST request to get Roles Select");

        List<Authority> authorityList = roleRepository.findAll();
        final List<SelectOptionVO> convert = roleSelectMapper.convert(authorityList);

        log.info("左侧树构建: {}", convert);
        return convert;
    }

}
