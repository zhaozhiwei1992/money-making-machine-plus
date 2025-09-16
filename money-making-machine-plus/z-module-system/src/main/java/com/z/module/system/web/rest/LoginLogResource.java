package com.z.module.system.web.rest;

import com.z.module.system.domain.LoginLog;
import com.z.module.system.repository.LoginLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Tag(name = "登录日志API")
@RestController
@RequestMapping("/system")
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class LoginLogResource {

    private final LoginLogRepository requestLogRepository;

    public LoginLogResource(LoginLogRepository loginLoggingRepository) {
        this.requestLogRepository = loginLoggingRepository;
    }

    /**
     * {@code GET /admin/loginLoggings} : get all loginLoggings with all the details - calling this are only allowed
     * for the administrators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all loginLoggings.
     */
    @Operation(description = "获取登录日志信息")
    @GetMapping("/login/logs")
    @PreAuthorize("hasAuthority('log:login:view')")
    public HashMap<String, Object> getAllLoginLoggings(Pageable pageable,
                                                                       LoginLog loginLog) {
        log.debug("REST login to get all LoginLogging for an admin");

        // 根据id, 升序
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), sort);

        Page<LoginLog> loginLoggingPage;
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
        Example<LoginLog> ex = Example.of(loginLog, matcher);
        loginLoggingPage = requestLogRepository.findAll(ex, pageable);

        return new HashMap<String, Object>() {{
            put("list", loginLoggingPage.getContent());
            put("total", Long.valueOf(loginLoggingPage.getTotalElements()).intValue());
        }};
    }

}
