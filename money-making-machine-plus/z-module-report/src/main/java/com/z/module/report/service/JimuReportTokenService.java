package com.z.module.report.service;

import com.z.framework.security.service.TokenProviderService;
import org.jeecg.modules.jmreport.api.JmReportTokenServiceI;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Title: JimuReportTokenService
 * @Package com/z/module/report/service/JimuReportTokenService.java
 * @Description: 对积木报表进行授权认证
 * https://help.jeecg.com/jimureport/config/token.html
 * @author zhao
 * @date 2023/5/20 下午9:58
 * @version V1.0
 */
@Component
public class JimuReportTokenService implements JmReportTokenServiceI {

    private final TokenProviderService tokenProviderService;

    public JimuReportTokenService(TokenProviderService tokenProviderService) {
        this.tokenProviderService = tokenProviderService;
    }

    @Override
    public String getUsername(String s) {
        return tokenProviderService.validateToken(s);
    }

    @Override
    public Boolean verifyToken(String s) {
        final String userName = tokenProviderService.validateToken(s);
        return StringUtils.hasText(userName);
    }
}