package com.z.module.system.web.rest;

import com.z.module.system.domain.Position;
import com.z.module.system.repository.PositionRepository;
import com.z.module.system.web.mapper.PositionSelectMapper;
import com.z.framework.common.web.vo.SelectOptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "岗位API")
@RestController
@RequestMapping("/system")
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class PositionResource {

    private final PositionRepository positionRepository;

    public PositionResource(PositionRepository roleRepository, PositionSelectMapper positionSelectMapper) {
        this.positionRepository = roleRepository;
        this.positionSelectMapper = positionSelectMapper;
    }

    /**
     * {@code POST  /admin/positions}  : Creates a new role.
     * <p>
     * Creates a new role if the login and email are not already used, and sends an
     * mail with an activation link.
     * The role needs to be activated on creation.
     *
     * @param position the role to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new role, or with
     * status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(description = "新增岗位")
    @PostMapping("/positions")
    @PreAuthorize("hasAuthority('system:post:add')")
    public Position createPosition(@RequestBody Position position) throws URISyntaxException {
        log.debug("REST request to save Position : {}", position);

        Position newPosition = positionRepository.save(position);

        return newPosition;
    }

    /**
     * {@code GET /admin/positions} : get all positions with all the details - calling this are only allowed for the
     * administrators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all positions.
     */

    @Operation(description = "获取岗位")
    @GetMapping("/positions")
    @PreAuthorize("hasAuthority('system:post:view')")
    public HashMap<String, Object> getAllPositions(Pageable pageable, Position role) {
        log.debug("REST request to get all Position for an admin");

        // 根据id, 升序
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), sort);

        Page<Position> rolePage;
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
        Example<Position> ex = Example.of(role, matcher);
        rolePage = positionRepository.findAll(ex, pageable);

        return new HashMap<String, Object>() {{
            put("list", rolePage.getContent());
            put("total", Long.valueOf(rolePage.getTotalElements()).intValue());
        }};
    }

    @Operation(description = "删除岗位")
    @DeleteMapping("/positions")
    @PreAuthorize("hasAuthority('system:post:delete')")
    public String deletePosition(@RequestBody List<Long> idList) {
        log.debug("REST request to delete Examples, ids: {}", idList);
        this.positionRepository.deleteAllByIdIn(idList);
        return "success";
    }

    @Operation(description = "获取岗位列表信息")
    @GetMapping("/positions/list")
    @PreAuthorize("hasAuthority('system:post:view')")
    public List<Map<String, Object>> getAllDictList() {
        final List<Position> all = positionRepository.findAll();
        final List<Map<String, Object>> resultMap = all.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("value", m.getCode());
            map.put("label", m.getName());
            return map;
        }).collect(Collectors.toList());
        return resultMap;
    }

    private final PositionSelectMapper positionSelectMapper;

    @Operation(description = "获取岗位树")
    @GetMapping("/positions/select")
    @PreAuthorize("hasAuthority('system:post:view')")
    public List<SelectOptionVO> getPositionsSelect() {
        log.debug("REST request to get Position Select");

        List<Position> positionList = positionRepository.findAll();
        final List<SelectOptionVO> convert = positionSelectMapper.convert(positionList);

        log.info("左侧树构建: {}", convert);
        return convert;
    }

}
