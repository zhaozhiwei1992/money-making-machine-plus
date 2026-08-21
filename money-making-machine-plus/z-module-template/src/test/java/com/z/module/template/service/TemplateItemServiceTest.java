package com.z.module.template.service;

import com.z.module.template.api.TemplateItemDTO;
import com.z.module.template.domain.TemplateItem;
import com.z.module.template.repository.TemplateItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 模板条目服务单测（演示：服务层不依赖 Spring 容器，纯 JUnit + Mockito 即可覆盖）。
 */
@ExtendWith(MockitoExtension.class)
class TemplateItemServiceTest {

    @Mock
    private TemplateItemRepository templateItemRepository;

    private TemplateItemService templateItemService;

    @BeforeEach
    void setUp() {
        templateItemService = new TemplateItemService(templateItemRepository);
    }

    private TemplateItem newItem(Long id, String name, String code) {
        TemplateItem item = new TemplateItem();
        item.setId(id);
        item.setName(name);
        item.setCode(code);
        return item;
    }

    @Test
    void create_savesEntityAndReturnsDto() {
        TemplateItemDTO dto = new TemplateItemDTO();
        dto.setName("示例");
        dto.setCode("DEMO-001");
        when(templateItemRepository.save(any(TemplateItem.class)))
                .thenAnswer(invocation -> {
                    TemplateItem saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        TemplateItemDTO result = templateItemService.create(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("示例");
        verify(templateItemRepository).save(any(TemplateItem.class));
    }

    @Test
    void findAll_returnsDtoPage() {
        Page<TemplateItem> entityPage = new PageImpl<>(
                List.of(newItem(1L, "示例", "DEMO-001")));
        when(templateItemRepository.findAll(any(PageRequest.class))).thenReturn(entityPage);

        Page<TemplateItemDTO> result = templateItemService.findAll(PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void findById_mapsEntityToDto() {
        when(templateItemRepository.findById(1L)).thenReturn(Optional.of(newItem(1L, "示例", "DEMO-001")));

        TemplateItemDTO result = templateItemService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("DEMO-001");
    }
}