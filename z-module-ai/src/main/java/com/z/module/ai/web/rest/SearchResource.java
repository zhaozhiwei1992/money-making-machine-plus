package com.z.module.ai.web.rest;

import com.z.module.ai.service.SearchService;
import com.z.module.ai.web.vo.SearchVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

/**
 * @Title: SearchResource
 * @Package com/z/module/ai/web/rest/SearchResource.java
 * @Description: 提供前台输入内容点击搜索后接口
 * @author zhaozhiwei
 * @date 2024/9/12 17:24
 * @version V1.0
 */
@Tag(name = "搜索API")
@RestController
@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequestMapping("/ai")
public class SearchResource {

    private final SearchService searchService;

    public SearchResource(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * {@code POST  /admin/search}  : Creates a new role.
     * <p>
     * Creates a new role if the login and email are not already used, and sends an
     * mail with an activation link.
     * The role needs to be activated on creation.
     *
     * @param searchVO the role to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new role, or with
     * status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(description = "新增搜索")
    @PostMapping("/search")
    public SearchVO createSearchVO(@RequestBody SearchVO searchVO) throws URISyntaxException {
        log.debug("REST request to save SearchVO : {}", searchVO);

        return searchService.search(searchVO);
    }
}
