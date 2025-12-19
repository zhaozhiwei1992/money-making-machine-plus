
package com.z.module.screen.service;

import com.z.module.screen.domain.GoViewProjectDO;
import com.z.module.screen.repository.GoViewProjectRepository;
import com.z.module.screen.web.mapper.GoViewProjectConvert;
import com.z.module.screen.web.vo.GoViewProjectCreateReqVO;
import com.z.module.screen.web.vo.GoViewProjectUpdateReqVO;
import com.z.module.screen.web.vo.PageParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GoViewProjectServiceTest {

    @Mock
    private GoViewProjectRepository goViewProjectRepository;

    @Mock
    private GoViewProjectConvert goViewProjectConvert;

    @InjectMocks
    private GoViewProjectService goViewProjectService;

    private GoViewProjectDO goViewProjectDO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        goViewProjectDO = new GoViewProjectDO();
        goViewProjectDO.setId(1L);
    }

    @Test
    public void testCreateProject() {
        GoViewProjectCreateReqVO createReqVO = new GoViewProjectCreateReqVO();
        when(goViewProjectConvert.convert(createReqVO)).thenReturn(goViewProjectDO);
        when(goViewProjectRepository.save(any(GoViewProjectDO.class))).thenReturn(goViewProjectDO);

        Long projectId = goViewProjectService.createProject(createReqVO);

        assertEquals(1L, projectId);
    }

    @Test
    public void testUpdateProject() {
        GoViewProjectUpdateReqVO updateReqVO = new GoViewProjectUpdateReqVO();
        updateReqVO.setId(1L);

        when(goViewProjectRepository.findById(1L)).thenReturn(Optional.of(goViewProjectDO));
        when(goViewProjectConvert.convert(updateReqVO)).thenReturn(goViewProjectDO);
        when(goViewProjectRepository.save(any(GoViewProjectDO.class))).thenReturn(goViewProjectDO);

        assertDoesNotThrow(() -> goViewProjectService.updateProject(updateReqVO));
    }

    @Test
    public void testDeleteProject() {
        when(goViewProjectRepository.findById(1L)).thenReturn(Optional.of(goViewProjectDO));
        assertDoesNotThrow(() -> goViewProjectService.deleteProject(1L));
    }

    @Test
    public void testGetProject() {
        when(goViewProjectRepository.findById(1L)).thenReturn(Optional.of(goViewProjectDO));
        GoViewProjectDO result = goViewProjectService.getProject(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testGetMyProjectPage() {
        PageParam pageParam = new PageParam();
        pageParam.setPage(1);
        pageParam.setLimit(10);
        Page<GoViewProjectDO> page = new PageImpl<>(Collections.singletonList(goViewProjectDO));

        when(goViewProjectRepository.findAllByCreatedBy(any(PageRequest.class), any(String.class))).thenReturn(page);

        Page<GoViewProjectDO> result = goViewProjectService.getMyProjectPage(pageParam);

        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
    }
}
