package com.z.module.ai.web.rest;

import com.z.framework.ai.AbstractAIService;
import com.z.framework.ai.model.chat.ChatMessage;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.*;

@Tag(name = "唠嗑API")
@RestController
@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequestMapping("/ai")
public class ChatResource {

    @Autowired
    private AbstractAIService abstractChatService;

    @RequestMapping(value = "/chat-stream", method = RequestMethod.POST, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map> chatStream(@RequestBody ChatMessage chatMessage) throws Exception {
        Flux<Map<String, Object>> mapFlux = abstractChatService.sendChatFlowMessageStream(chatMessage);
        List<Map> metadataList = new ArrayList<>();
        Flux<Map> rMap = mapFlux.map(event -> {
            Map r = new HashMap(event);
            if (event.get("event") != null) {
                if ("message".equals(event.get("event"))) {
                    if (event.get("answer") != null) {
                        r.put("answer_type", "测试回写");
                        r.put("metadata", metadataList);
                    }
                }else if ("node_finished".equals(event.get("event"))) {
                    // 节点结束
                    Map data = (Map) event.get("data");
                    if ("knowledge-retrieval".equals(data.get("node_type"))) {
                        // 知识检索结束时取引用文档
                        Map outputs = (Map) data.get("outputs");
                        List result = (List) outputs.get("result");

                        for (int i = 0; i < result.size(); i++) {
                            Map d = (Map) result.get(i);
                            String document_name = (String) d.get("title");
                            String document_content = (String) d.get("content");
                            Map docMap = new HashMap();
                            document_name = document_name.substring(0, document_name.length() - 4);
                            docMap.put("name", document_name);
                            docMap.put("url", "http://xx" + document_name);
                            docMap.put("content", document_content);
                            metadataList.add(docMap);
                        }
                    }
                }else if ("message_end".equals(event.get("event"))) {
                    r.put("message_end_1", "1");
                }
            }
            return r;
        });
        return rMap;
    }
}
