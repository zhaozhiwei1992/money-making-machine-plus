package com.z.framework.operatelog.service;

import com.z.framework.operatelog.domain.RequestLog;
import com.z.framework.operatelog.repository.RequestLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @Title: null.java
 * @Package: com.z.framework.operatelog.service
 * @Description: TODO
 * @author: zhaozhiwei
 * @date: 2025/4/14 21:40
 * @version: V1.0
 */
@Service
public class RequestLogService {

    @Autowired
    private RequestLogRepository requestLoggingRepository;


    @Async("requestLogAsyncExecutor")
    public void asyncSaveRequestLog(RequestLog requestLogging) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        requestLoggingRepository.save(requestLogging);
    }

}
