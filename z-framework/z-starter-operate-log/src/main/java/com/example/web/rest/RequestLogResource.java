package com.example.web.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.example.domain.RequestLog;
import com.example.repository.RequestLogRepository;
import com.example.web.rest.vm.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "请求日志信息API")
@RestController
@RequestMapping("/api")
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class RequestLogResource {

    @Value("${ifmis.clientApp.name}")
    private String applicationName;

    private final RequestLogRepository requestLoggingRepository;

    public RequestLogResource(RequestLogRepository requestLoggingRepository) {
        this.requestLoggingRepository = requestLoggingRepository;
    }

    /**
     * {@code GET /admin/requestLoggings} : get all requestLoggings with all the details - calling this are only allowed for the administrators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all requestLoggings.
     */
    @Operation(description = "所有请求日志信息")
    @GetMapping("/request/logs")
    public ResponseData<List<RequestLog>> getAllRequestLoggings(Pageable pageable, String key) {
        log.debug("REST request to get all RequestLogging for an admin");

        // 根据id, 升序
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber()-1, pageable.getPageSize(), sort);

        Page<RequestLog> requestLoggingPage;
        // 搜索
        if(StrUtil.isNotEmpty(key)){
            final RequestLog requestLogging = new RequestLog();
            final List<String> cols = Arrays.asList("loginName", "requestName");
            //      2. 将传入属性, 填充给界面显示字段
            final Map<String, String> map = cols.stream().collect(Collectors.toMap(s -> s, key2 -> key));
            //      3. 动态构建查询条件
            BeanUtil.fillBeanWithMap(map, requestLogging, true);
            log.info("填充后对象信息 {}", requestLogging);

            //创建匹配器，即如何使用查询条件
            //构建对象
            ExampleMatcher matcher = ExampleMatcher
                    .matchingAny()
                    //改变默认字符串匹配方式：模糊查询
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                    //改变默认大小写忽略方式：忽略大小写
                    .withIgnoreCase(true)
                    //忽略属性：是否关注。因为是基本类型，需要忽略掉
                    .withIgnorePaths("id");

            //创建实例
            Example<RequestLog> ex = Example.of(requestLogging, matcher);
            requestLoggingPage = requestLoggingRepository.findAll(ex, pageable);
        }else{
            requestLoggingPage = requestLoggingRepository.findAll(pageable);
        }

        final ResponseData<List<RequestLog>> listResponseData = new ResponseData<>();
        listResponseData.setData(requestLoggingPage.getContent());
        listResponseData.setCode("0");
        listResponseData.setCount(Long.valueOf(requestLoggingPage.getTotalElements()).intValue());
        return listResponseData;
    }

}
