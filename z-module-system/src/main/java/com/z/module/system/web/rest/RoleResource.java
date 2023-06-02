package com.z.module.system.web.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.module.system.domain.Authority;
import com.z.module.system.repository.AuthorityRepository;
import com.z.module.system.service.RoleMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "角色API")
@RestController
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class RoleResource {

    private static final String ROLE_START = "ROLE_";

    private final AuthorityRepository roleRepository;

    public RoleResource(AuthorityRepository roleRepository,
                        RoleMenuService roleMenuService) {
        this.roleRepository = roleRepository;
        this.roleMenuService = roleMenuService;
    }

    /**
     * {@code POST  /admin/roles}  : Creates a new role.
     * <p>
     * Creates a new role if the login and email are not already used, and sends an
     * mail with an activation link.
     * The role needs to be activated on creation.
     *
     * @param roleDTO the role to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new role, or with status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(description = "新增角色")
    @PostMapping("/roles")
    public ResponseEntity<ResponseData<Authority>> createAuthority(@RequestBody Authority roleDTO) throws URISyntaxException {
        log.debug("REST request to save Authority : {}", roleDTO);

        if(!roleDTO.getCode().startsWith(ROLE_START)){
            roleDTO.setCode(ROLE_START + roleDTO.getCode());
        }

        Authority newAuthority = roleRepository.save(roleDTO);

        return ResponseData.ok(newAuthority);
    }

    /**
     * {@code GET /admin/roles} : get all roles with all the details - calling this are only allowed for the administrators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all roles.
     */

    @Operation(description = "获取角色")
    @GetMapping("/roles")
    public ResponseEntity<ResponseData<HashMap<String, Object>>> getAllAuthoritys(Pageable pageable, String key) {
        log.debug("REST request to get all Authority for an admin");

//        final List<Authority> all = roleRepository.findAll();
        // 根据id, 升序
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber()-1, pageable.getPageSize(), sort);

        Page<Authority> rolePage;
        // 搜索
        if(StrUtil.isNotEmpty(key)){
            final Authority role = new Authority();
            final List<String> cols = Arrays.asList("name", "code");
            //      2. 将传入属性, 填充给界面显示字段
            final Map<String, String> map = cols.stream().collect(Collectors.toMap(s -> s, key2 -> key));
            //      3. 动态构建查询条件
            BeanUtil.fillBeanWithMap(map, role, true);
            log.info("填充后对象信息 {}", role);

            //创建匹配器，即如何使用查询条件
            //构建对象
            ExampleMatcher matcher = ExampleMatcher
                    .matchingAny()
                    //改变默认字符串匹配方式：模糊查询
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                    //改变默认大小写忽略方式：忽略大小写
                    .withIgnoreCase(true)
                    //名字采用“开始匹配”的方式查询
                    .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.startsWith())
                    //忽略属性：是否关注。因为是基本类型，需要忽略掉
                    .withIgnorePaths("id");

            //创建实例
            Example<Authority> ex = Example.of(role, matcher);
            rolePage = roleRepository.findAll(ex, pageable);
        }else{
            rolePage = roleRepository.findAll(pageable);
        }

        return ResponseData.ok(new HashMap<String, Object>(){{
            put("list", rolePage.getContent());
            put("total", Long.valueOf(rolePage.getTotalElements()).intValue());
        }});
    }

    @Operation(description = "删除角色")
    @DeleteMapping("/roles")
    public ResponseEntity<ResponseData<String>> deleteAuthority(@RequestBody List<Long> idList) {
        log.debug("REST request to delete Examples, ids: {}", idList);
        this.roleRepository.deleteAllByIdIn(idList);
        return ResponseData.ok("success");
    }

    private final RoleMenuService roleMenuService;

    @Operation(description = "保存角色菜单信息")
    @PostMapping(value = "/roles/menu")
    public ResponseEntity<ResponseData<String>> save(
            @RequestParam(value = "roleList") List<Long> roleList,
            @RequestParam(value = "menuList") List<Long> menuList
    ) {

        roleMenuService.saveRoleMenu(roleList, menuList);
        return ResponseData.ok("success");
    }


    @Operation(description = "获取角色列表信息")
    @GetMapping("/roles/list")
    public ResponseEntity<ResponseData<List<Map<String, Object>>>> getAllDictList() {
        final List<Authority> all = roleRepository.findAll();
        final List<Map<String, Object>> resultMap = all.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("value", m.getCode());
            map.put("label", m.getName());
            return map;
        }).collect(Collectors.toList());
        return ResponseData.ok(resultMap);
    }

}
