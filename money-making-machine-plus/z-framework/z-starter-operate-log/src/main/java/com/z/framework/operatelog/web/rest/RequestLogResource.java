package com.z.framework.operatelog.web.rest;

import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.framework.operatelog.domain.RequestLog;
import com.z.framework.operatelog.repository.RequestLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Tag(name = "请求日志信息API")
@RestController
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class RequestLogResource {

    @Value("${z.app.name}")
    private String applicationName;

    private final RequestLogRepository requestLoggingRepository;

    public RequestLogResource(RequestLogRepository requestLoggingRepository) {
        this.requestLoggingRepository = requestLoggingRepository;
    }

    /**
     * {@code GET /admin/requestLoggings} : get all requestLoggings with all the details - calling this are only
     * allowed for the administrators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all requestLoggings.
     */
    @Operation(description = "所有请求日志信息")
    @GetMapping("/request/logs")
    public HashMap<String, Object> getAllRequestLog(Pageable pageable,
                                                                                  RequestLog requestLog) {
        log.debug("REST request to get all RequestLogging for an admin");

        // 根据id, 升序
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), sort);

        Page<RequestLog> requestLoggingPage;
        // 搜索
        //创建匹配器，即如何使用查询条件
        //构建对象
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                //改变默认字符串匹配方式：模糊查询
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                //改变默认大小写忽略方式：忽略大小写
                .withIgnoreCase(true)
                .withIgnoreNullValues()
                //忽略属性：是否关注。因为是基本类型，需要忽略掉
                .withIgnorePaths("id", "createdDate", "lastModifiedDate");

        //创建实例
        Example<RequestLog> ex = Example.of(requestLog, matcher);
        requestLoggingPage = requestLoggingRepository.findAll(ex, pageable);
        return new HashMap<>() {{
            put("list", requestLoggingPage.getContent());
            put("total", Long.valueOf(requestLoggingPage.getTotalElements()).intValue());
        }};
    }

}
