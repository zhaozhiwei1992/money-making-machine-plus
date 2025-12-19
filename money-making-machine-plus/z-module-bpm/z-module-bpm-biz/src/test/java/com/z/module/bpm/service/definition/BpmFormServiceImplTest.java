
package com.z.module.bpm.service.definition;

import com.z.module.bpm.domain.definition.BpmFormDO;
import com.z.module.bpm.repository.definition.BpmFormRepository;
import com.z.module.bpm.web.vo.definition.form.BpmFormCreateReqVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BpmFormServiceImplTest {

    @InjectMocks
    private BpmFormServiceImpl bpmFormService;

    @Mock
    private BpmFormRepository bpmFormRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateForm() {
        BpmFormCreateReqVO reqVO = new BpmFormCreateReqVO();
        reqVO.setName("Test Form");

        BpmFormDO form = new BpmFormDO();
        form.setId(1L);
        form.setName("Test Form");

        when(bpmFormRepository.save(any(BpmFormDO.class))).thenReturn(form);

        Long formId = bpmFormService.createForm(reqVO);

        assertEquals(1L, formId);
    }

    @Test
    public void testGetForm() {
        BpmFormDO form = new BpmFormDO();
        form.setId(1L);
        form.setName("Test Form");

        when(bpmFormRepository.findById(1L)).thenReturn(Optional.of(form));

        BpmFormDO result = bpmFormService.getForm(1L);

        assertEquals("Test Form", result.getName());
    }
}
