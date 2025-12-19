
package com.z.module.bpm.service.definition;

import com.z.module.bpm.domain.definition.BpmUserGroupDO;
import com.z.module.bpm.repository.definition.BpmUserGroupRepository;
import com.z.module.bpm.web.mapper.definition.BpmUserGroupConvert;
import com.z.module.bpm.web.vo.definition.group.BpmUserGroupCreateReqVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BpmUserGroupServiceImplTest {

    @InjectMocks
    private BpmUserGroupServiceImpl bpmUserGroupService;

    @Mock
    private BpmUserGroupRepository bpmUserGroupRepository;

    @Mock
    private BpmUserGroupConvert bpmUserGroupConvert;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUserGroup() {
        BpmUserGroupCreateReqVO reqVO = new BpmUserGroupCreateReqVO();
        reqVO.setName("Test Group");

        BpmUserGroupDO group = new BpmUserGroupDO();
        group.setId(1L);
        group.setName("Test Group");

        when(bpmUserGroupConvert.convert(reqVO)).thenReturn(group);
        when(bpmUserGroupRepository.save(any(BpmUserGroupDO.class))).thenReturn(group);

        Long groupId = bpmUserGroupService.createUserGroup(reqVO);

        assertEquals(1L, groupId);
    }

    @Test
    public void testGetUserGroup() {
        BpmUserGroupDO group = new BpmUserGroupDO();
        group.setId(1L);
        group.setName("Test Group");

        when(bpmUserGroupRepository.findById(1L)).thenReturn(Optional.of(group));

        BpmUserGroupDO result = bpmUserGroupService.getUserGroup(1L);

        assertEquals("Test Group", result.getName());
    }
}
