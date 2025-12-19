
package com.z.module.ai.service;

import com.z.module.ai.domain.History;
import com.z.module.ai.domain.HistoryDetail;
import com.z.module.ai.repository.HistoryDetailRepository;
import com.z.module.ai.repository.HistoryRepository;
import com.z.module.ai.web.vo.SearchVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SearchServiceTest {

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private HistoryDetailRepository historyDetailRepository;

    @InjectMocks
    private SearchService searchService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchWithNewHistory() {
        SearchVO searchVO = new SearchVO();
        searchVO.setContent("test query");
        searchVO.setEngineId(1L);

        History savedHistory = new History();
        savedHistory.setId(1L);
        when(historyRepository.save(any(History.class))).thenReturn(savedHistory);

        when(historyDetailRepository.findAllByHistoryIdOrderByIdAsc(any())).thenReturn(Collections.emptyList());

        SearchVO result = searchService.search(searchVO);

        assertEquals(1L, result.getHistoryId());
    }

    @Test
    public void testSearchWithExistingHistory() {
        SearchVO searchVO = new SearchVO();
        searchVO.setHistoryId(1L);
        searchVO.setContent("another query");
        searchVO.setEngineId(1L);

        HistoryDetail historyDetail = new HistoryDetail();
        historyDetail.setContent("another query");
        when(historyDetailRepository.findAllByHistoryIdOrderByIdAsc(1L)).thenReturn(Collections.singletonList(historyDetail));

        SearchVO result = searchService.search(searchVO);

        assertEquals(1L, result.getHistoryId());
    }
}
