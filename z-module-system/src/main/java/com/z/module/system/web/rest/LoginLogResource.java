package com.z.module.system.web.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.module.system.domain.LoginLog;
import com.z.module.system.repository.LoginLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "登录日志API")
@RestController
@RequestMapping("/api")
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class LoginLogResource {

    private final LoginLogRepository requestLogRepository;

    public LoginLogResource(LoginLogRepository loginLoggingRepository) {
        this.requestLogRepository = loginLoggingRepository;
    }

    /**
     * {@code GET /admin/loginLoggings} : get all loginLoggings with all the details - calling this are only allowed for the administrators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all loginLoggings.
     */
    @Operation(description = "获取登录日志信息")
    @GetMapping("/login/logs")
    public ResponseEntity<ResponseData<HashMap<String, Object>>> getAllLoginLoggings(Pageable pageable, String key) {
        log.debug("REST login to get all LoginLogging for an admin");

        // 根据id, 升序
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber()-1, pageable.getPageSize(), sort);

        Page<LoginLog> loginLoggingPage;
        // 搜索
        if(StrUtil.isNotEmpty(key)){
            final LoginLog loginLogging = new LoginLog();
            final List<String> cols = Arrays.asList("loginName", "os");
            //      2. 将传入属性, 填充给界面显示字段
            final Map<String, String> map = cols.stream().collect(Collectors.toMap(s -> s, key2 -> key));
            //      3. 动态构建查询条件
            BeanUtil.fillBeanWithMap(map, loginLogging, true);
            log.info("填充后对象信息 {}", loginLogging);

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
            Example<LoginLog> ex = Example.of(loginLogging, matcher);
            loginLoggingPage = requestLogRepository.findAll(ex, pageable);
        }else{
            loginLoggingPage = requestLogRepository.findAll(pageable);
        }

        return ResponseData.ok(new HashMap<String, Object>(){{
            put("list", loginLoggingPage.getContent());
            put("total", Long.valueOf(loginLoggingPage.getTotalElements()).intValue());
        }});
    }

}
