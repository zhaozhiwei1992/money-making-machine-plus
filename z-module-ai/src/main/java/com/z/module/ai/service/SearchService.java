package com.z.module.ai.service;

import com.z.module.ai.domain.HistoryDetail;
import com.z.module.ai.repository.HistoryDetailRepository;
import com.z.module.ai.web.vo.SearchVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class SearchService {
    private final HistoryDetailRepository historyDetailRepository;

    public SearchService(HistoryDetailRepository historyDetailRepository) {
        this.historyDetailRepository = historyDetailRepository;
    }

    public SearchVO search(SearchVO searchVO) {
        Long historyId = searchVO.getHistoryId();
        if(!Objects.isNull(historyId)){
            List<HistoryDetail> allByHistoryIdOrderByIdAsc = historyDetailRepository.findAllByHistoryIdOrderByIdAsc(historyId);
        }
//        1. 录入信息后点击查询按钮，后台根据用户选择引擎调用第三方接口进行提问。同时会将提问记录保留到历史表中。
//        2. 首次提问会生成唯一id, 需要反馈给前台，后续提问都要基于这个id，方便保留提问历史。
//        3. 基于历史的提问，每次调用接口需要将历史提问及回复提供给引擎，方便保持上下文。

        return null;
    }
}
