package com.z.module.ai.web.rest;

import com.z.framework.common.web.vo.SelectOptionVO;
import com.z.module.ai.domain.Engine;
import com.z.module.ai.repository.EngineRepository;
import com.z.module.ai.web.mapper.EngineSelectMapper;
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

@Tag(name = "引擎API")
@RestController
@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequestMapping("/ai")
public class EngineResource {

    private final EngineRepository engineRepository;

    public EngineResource(EngineRepository engineRepository, EngineSelectMapper engineSelectMapper) {
        this.engineRepository = engineRepository;
        this.engineSelectMapper = engineSelectMapper;
    }

    /**
     * {@code POST  /admin/engines}  : Creates a new role.
     * <p>
     * Creates a new role if the login and email are not already used, and sends an
     * mail with an activation link.
     * The role needs to be activated on creation.
     *
     * @param engine the role to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new role, or with
     * status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(description = "新增引擎")
    @PostMapping("/engines")
    @PreAuthorize("hasAuthority('ai:engine:add')")
    public Engine createEngine(@RequestBody Engine engine) throws URISyntaxException {
        log.debug("REST request to save Engine : {}", engine);

        Engine newEngine = engineRepository.save(engine);

        return newEngine;
    }

    /**
     * {@code GET /admin/engines} : get all engines with all the details - calling this are only allowed for the
     * administrators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all engines.
     */

    @Operation(description = "获取引擎")
    @GetMapping("/engines")
    @PreAuthorize("hasAuthority('ai:engine:view')")
    public HashMap<String, Object> getAllEngines(Pageable pageable, Engine engine) {
        log.debug("REST request to get all Engine for an admin");

        // 根据id, 升序
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), sort);

        Page<Engine> enginePage;
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
                .withIgnorePaths("id", "createdDate", "lastModifiedDate", "status");

        //创建实例
        Example<Engine> ex = Example.of(engine, matcher);
        enginePage = engineRepository.findAll(ex, pageable);

        return new HashMap<String, Object>() {{
            put("list", enginePage.getContent());
            put("total", Long.valueOf(enginePage.getTotalElements()).intValue());
        }};
    }

    @Operation(description = "删除引擎")
    @DeleteMapping("/engines")
    @PreAuthorize("hasAuthority('ai:engine:delete')")
    public String deleteEngine(@RequestBody List<Long> idList) {
        log.debug("REST request to delete Examples, ids: {}", idList);
        this.engineRepository.deleteAllByIdIn(idList);
        return "success";
    }

    @Operation(description = "获取引擎列表信息")
    @GetMapping("/engines/list")
    @PreAuthorize("hasAuthority('ai:engine:view')")
    public List<Map<String, Object>> getAllDictList() {
        final List<Engine> all = engineRepository.findAll();
        final List<Map<String, Object>> resultMap = all.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("value", m.getId());
            map.put("label", m.getName());
            return map;
        }).collect(Collectors.toList());
        return resultMap;
    }

    private final EngineSelectMapper engineSelectMapper;

    @Operation(description = "获取引擎树")
    @GetMapping("/engines/select")
    @PreAuthorize("hasAuthority('ai:engine:view')")
    public List<SelectOptionVO> getEnginesSelect() {
        log.debug("REST request to get Engine Select");

        List<Engine> engineList = engineRepository.findAll();
        final List<SelectOptionVO> convert = engineSelectMapper.convert(engineList);

        log.info("左侧树构建: {}", convert);
        return convert;
    }

}
