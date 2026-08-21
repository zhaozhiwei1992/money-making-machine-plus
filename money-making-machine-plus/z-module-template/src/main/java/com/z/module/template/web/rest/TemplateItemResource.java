package com.z.module.template.web.rest;

import com.z.framework.common.web.rest.errors.BadRequestAlertException;
import com.z.module.template.api.TemplateItemDTO;
import com.z.module.template.service.TemplateItemService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * 模板条目 REST 端点（最小 CRUD 演示）。
 * <p>
 * 遵循仓库惯例：构造器注入、@Operation 中文描述、分页返回
 * {list, total} 结构、删除接收 idList。
 */
@RestController
@RequestMapping("/template")
@Slf4j
public class TemplateItemResource {

    private static final String ENTITY_NAME = "templateItem";

    private final TemplateItemService templateItemService;

    public TemplateItemResource(TemplateItemService templateItemService) {
        this.templateItemService = templateItemService;
    }

    @Operation(description = "新建模板条目")
    @PostMapping("/items")
    public TemplateItemDTO create(@RequestBody TemplateItemDTO dto) {
        log.debug("REST request to create TemplateItem : {}", dto);
        if (dto.getId() != null) {
            throw new BadRequestAlertException("新建时不应携带 id", ENTITY_NAME, "id exists");
        }
        return templateItemService.create(dto);
    }

    @Operation(description = "更新模板条目")
    @PutMapping("/items/{id}")
    public TemplateItemDTO update(@PathVariable Long id, @RequestBody TemplateItemDTO dto) {
        log.debug("REST request to update TemplateItem : {}, {}", id, dto);
        if (dto.getId() == null || !id.equals(dto.getId())) {
            throw new BadRequestAlertException("路径 id 与 body id 不一致", ENTITY_NAME, "id mismatch");
        }
        return templateItemService.update(id, dto);
    }

    @Operation(description = "分页查询模板条目")
    @GetMapping("/items")
    public HashMap<String, Object> getAll(Pageable pageable) {
        log.debug("REST request to get a page of TemplateItems");
        Page<TemplateItemDTO> page = templateItemService.findAll(pageable);
        return new HashMap<String, Object>() {{
            put("list", page.getContent());
            put("total", page.getTotalElements());
        }};
    }

    @Operation(description = "查询单个模板条目")
    @GetMapping("/items/{id}")
    public TemplateItemDTO get(@PathVariable Long id) {
        log.debug("REST request to get TemplateItem : {}", id);
        return templateItemService.findById(id);
    }

    @Operation(description = "批量删除模板条目")
    @DeleteMapping("/items")
    public String delete(@RequestBody List<Long> idList) {
        log.debug("REST request to delete TemplateItems : {}", idList);
        templateItemService.delete(idList);
        return "success";
    }
}