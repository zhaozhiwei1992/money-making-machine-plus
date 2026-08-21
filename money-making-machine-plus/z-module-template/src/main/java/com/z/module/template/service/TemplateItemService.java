package com.z.module.template.service;

import com.z.module.template.api.TemplateItemDTO;
import com.z.module.template.domain.TemplateItem;
import com.z.module.template.repository.TemplateItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 模板条目服务：最小 CRUD 演示。
 * <p>
 * 分层约定：web/rest 只做参数绑定与返回包装，业务逻辑收敛在本层；
 * api 的 DTO 与 domain 实体在此转换，实体不泄漏到 REST 边界。
 */
@Service
@Transactional
@Slf4j
public class TemplateItemService {

    private final TemplateItemRepository templateItemRepository;

    public TemplateItemService(TemplateItemRepository templateItemRepository) {
        this.templateItemRepository = templateItemRepository;
    }

    public TemplateItemDTO create(TemplateItemDTO dto) {
        log.debug("Create TemplateItem : {}", dto);
        TemplateItem item = toEntity(dto);
        return toDto(templateItemRepository.save(item));
    }

    public TemplateItemDTO update(Long id, TemplateItemDTO dto) {
        log.debug("Update TemplateItem : {}, {}", id, dto);
        TemplateItem item = templateItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("模板条目不存在: " + id));
        item.setName(dto.getName());
        item.setCode(dto.getCode());
        item.setRemark(dto.getRemark());
        return toDto(templateItemRepository.save(item));
    }

    @Transactional(readOnly = true)
    public Page<TemplateItemDTO> findAll(Pageable pageable) {
        return templateItemRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public TemplateItemDTO findById(Long id) {
        return templateItemRepository.findById(id).map(this::toDto).orElse(null);
    }

    public void delete(List<Long> idList) {
        log.debug("Delete TemplateItems : {}", idList);
        templateItemRepository.deleteAllByIdIn(idList);
    }

    private TemplateItem toEntity(TemplateItemDTO dto) {
        TemplateItem item = new TemplateItem();
        item.setName(dto.getName());
        item.setCode(dto.getCode());
        item.setRemark(dto.getRemark());
        return item;
    }

    private TemplateItemDTO toDto(TemplateItem item) {
        TemplateItemDTO dto = new TemplateItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setCode(item.getCode());
        dto.setRemark(item.getRemark());
        dto.setCreatedDate(item.getCreatedDate());
        return dto;
    }
}