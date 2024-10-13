package com.z.module.ai.web.rest;

import com.z.module.ai.domain.History;
import com.z.module.ai.repository.HistoryRepository;
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
import java.util.Objects;
import java.util.Optional;

@Tag(name = "历史API")
@RestController
@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequestMapping("/ai")
public class HistoryResource {

    private final HistoryRepository historyRepository;

    public HistoryResource(HistoryRepository roleRepository) {
        this.historyRepository = roleRepository;
    }

    /**
     * {@code POST  /admin/histories}  : Creates a new role.
     * <p>
     * Creates a new role if the login and email are not already used, and sends an
     * mail with an activation link.
     * The role needs to be activated on creation.
     *
     * @param history the role to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new role, or with
     * status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(description = "新增历史")
    @PostMapping("/histories")
//    @PreAuthorize("hasAuthority('ai:history:add')")
    public History createHistory(@RequestBody History history) throws URISyntaxException {
        log.debug("REST request to save History : {}", history);
        History newHistory;

        if(Objects.isNull(history.getId())){
            newHistory = historyRepository.save(history);
        }else{
            Optional<History> byId = historyRepository.findById(history.getId());
            newHistory = byId.orElse(new History());
            newHistory.setRemark(history.getRemark());
            historyRepository.save(newHistory);
        }

        return newHistory;
    }

    /**
     * {@code GET /admin/histories} : get all histories with all the details - calling this are only allowed for the
     * administrators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all histories.
     */

    @Operation(description = "获取历史")
    @GetMapping("/histories")
//    @PreAuthorize("hasAuthority('ai:history:view')")
    public HashMap<String, Object> getAllHistorys(Pageable pageable, History role) {
        log.debug("REST request to get all History for an admin");

        // 根据id, 升序
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), sort);

        Page<History> rolePage;
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
        Example<History> ex = Example.of(role, matcher);
        rolePage = historyRepository.findAll(ex, pageable);

        return new HashMap<String, Object>() {{
            put("list", rolePage.getContent());
            put("total", Long.valueOf(rolePage.getTotalElements()).intValue());
        }};
    }

    @Operation(description = "删除历史")
    @DeleteMapping("/histories")
//    @PreAuthorize("hasAuthority('ai:history:delete')")
    public String deleteHistory(@RequestBody List<Long> idList) {
        log.debug("REST request to delete Examples, ids: {}", idList);
        this.historyRepository.deleteAllByIdIn(idList);
        return "success";
    }

    @Operation(description = "获取历史列表信息")
    @GetMapping("/histories/list")
//    @PreAuthorize("hasAuthority('ai:history:view')")
    public List<History> getAllHistoryList() {
        return historyRepository.findAll();
    }
}
