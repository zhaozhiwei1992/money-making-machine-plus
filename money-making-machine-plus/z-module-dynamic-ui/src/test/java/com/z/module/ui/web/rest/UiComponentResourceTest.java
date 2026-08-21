
package com.z.module.ui.web.rest;

import com.z.module.ui.domain.UiComponent;
import com.z.module.ui.repository.UiComponentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UiComponentResourceTest {

    private MockMvc mockMvc;

    @Mock
    private UiComponentRepository uiComponentRepository;

    @InjectMocks
    private UiComponentResource uiComponentResource;

    private UiComponent uiComponent;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // 库模块无 SpringBootApplication，用 standalone MockMvc 测试 controller；
        // 手动注册 Pageable 解析器（@WebMvcTest 自动有，此处需显式）
        mockMvc = MockMvcBuilders.standaloneSetup(uiComponentResource)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        uiComponent = new UiComponent();
        uiComponent.setId(1L);
        uiComponent.setMenuId(1L);
    }

    @Test
    public void testCreateUiComponent() throws Exception {
        when(uiComponentRepository.save(any(UiComponent.class))).thenReturn(uiComponent);

        mockMvc.perform(post("/ui/components")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"menuId\": 1 }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testUpdateUiComponent() throws Exception {
        when(uiComponentRepository.existsById(1L)).thenReturn(true);
        when(uiComponentRepository.save(any(UiComponent.class))).thenReturn(uiComponent);

        mockMvc.perform(put("/ui/components/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1, \"menuId\": 1 }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testGetUiComponents() throws Exception {
        Page<UiComponent> page = new PageImpl<>(Collections.singletonList(uiComponent));
        when(uiComponentRepository.findAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/ui/components"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list[0].id").value(1L))
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    public void testGetUiComponent() throws Exception {
        when(uiComponentRepository.findById(1L)).thenReturn(Optional.of(uiComponent));

        mockMvc.perform(get("/ui/components/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testGetUiComponentByMenuId() throws Exception {
        List<UiComponent> list = Collections.singletonList(uiComponent);
        when(uiComponentRepository.findByMenuIdOrderByOrderNumAsc(1L)).thenReturn(list);

        mockMvc.perform(get("/ui/components/menu/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testDeleteUiComponent() throws Exception {
        mockMvc.perform(delete("/ui/components/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[1]"))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));
    }
}
