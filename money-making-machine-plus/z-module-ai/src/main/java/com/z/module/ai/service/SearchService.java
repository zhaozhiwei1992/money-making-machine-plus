package com.z.module.ai.service;

import com.alibaba.fastjson.JSON;
import com.z.module.ai.domain.History;
import com.z.module.ai.domain.HistoryDetail;
import com.z.module.ai.repository.HistoryDetailRepository;
import com.z.module.ai.repository.HistoryRepository;
import com.z.module.ai.web.vo.SearchVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final HistoryRepository historyRepository;

    private final HistoryDetailRepository historyDetailRepository;

    public SearchService(HistoryRepository historyRepository, HistoryDetailRepository historyDetailRepository) {
        this.historyRepository = historyRepository;
        this.historyDetailRepository = historyDetailRepository;
    }

    public SearchVO search(SearchVO searchVO) {
        Long historyId = searchVO.getHistoryId();
        if(Objects.isNull(historyId) || historyId == 0){
            // 首次操作保留历史信息
            History history = new History();
            String content = searchVO.getContent();
            history.setRemark(content.substring(0, Math.min(content.length(), 100)));
            history.setEngineId(searchVO.getEngineId());
//        1. 录入信息后点击查询按钮，后台根据用户选择引擎调用第三方接口进行提问。同时会将提问记录保留到历史表中。
            History save = historyRepository.save(history);
//        2. 首次提问会生成唯一id, 需要反馈给前台，后续提问都要基于这个id，方便保留提问历史。
            searchVO.setHistoryId(save.getId());
        }
        // 保留新的提问
        HistoryDetail historyDetail = new HistoryDetail();
        historyDetail.setHistoryId(searchVO.getHistoryId());
        historyDetail.setDirect(0);
        historyDetail.setContent(searchVO.getContent());
        historyDetailRepository.save(historyDetail);

        List<HistoryDetail> allByHistoryIdOrderByIdAsc = historyDetailRepository.findAllByHistoryIdOrderByIdAsc(historyId);
//        3. 基于历史的提问，每次调用接口需要将历史提问及回复提供给引擎，方便保持上下文。
        String collect = allByHistoryIdOrderByIdAsc.stream().map(HistoryDetail::getContent).collect(Collectors.joining("\n"));
//        String completion = tongYiService.completion(collect);
//        String completion = QianWenChatClient.callWithMessage(collect);
        DifyChatClient difyChatClient = new DifyChatClient();
        String completion = difyChatClient.callWithMessage(searchVO.getContent());
//        String encodedQuery = null;
//        try {
//            encodedQuery = URLEncoder.encode(collect, StandardCharsets.UTF_8.toString());
//        } catch (UnsupportedEncodingException e) {
//            throw new RuntimeException(e);
//        }
//        String completion = new LocalChatClient().callWithMessage(encodedQuery);

        // 4. 引擎返回结果，将结果保存到历史记录中。
        HistoryDetail historyDetailBack = new HistoryDetail();
        historyDetailBack.setHistoryId(searchVO.getHistoryId());
        historyDetailBack.setDirect(1);
        Map map = JSON.parseObject(completion, Map.class);
        historyDetailBack.setContent(String.valueOf(map.get("answer")));
//        historyDetailBack.setContent("回复: " + searchVO.getContent());
        historyDetailRepository.save(historyDetailBack);

        return searchVO;
    }
}
