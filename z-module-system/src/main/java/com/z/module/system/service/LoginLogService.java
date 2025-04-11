package com.z.module.system.service;

import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.z.module.system.domain.LoginLog;
import com.z.module.system.repository.LoginLogRepository;
import com.z.module.system.web.vo.LoginVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: null.java
 * @Package com.longtu.service
 * @Description: 写入login日志服务类
 * @date 2022/7/19 下午5:51
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LoginLogService{

    private final LoginLogRepository loginLogRepository;

    public LoginLogService(LoginLogRepository loginLogRepository) {
        this.loginLogRepository = loginLogRepository;
    }

    public LoginLog save(LoginVO loginVM, HttpServletRequest request){
        final LoginLog loginLog = new LoginLog();
        loginLog.setClientIp(JakartaServletUtil.getClientIP(request));
        final UserAgent userAgentParse = UserAgentUtil.parse(request.getHeader("User-Agent"));
        loginLog.setOs(userAgentParse.getOs().toString());
        final String username = loginVM.getUsername();
        loginLog.setLoginName(username);
        loginLog.setBrowser(userAgentParse.getBrowser().toString());

        return loginLogRepository.save(loginLog);
    }

    public LoginLog genLogInfo(LoginVO loginVM, HttpServletRequest request){
        final LoginLog loginLog = new LoginLog();
        loginLog.setClientIp(JakartaServletUtil.getClientIP(request));
        final UserAgent userAgentParse = UserAgentUtil.parse(request.getHeader("User-Agent"));
        loginLog.setOs(userAgentParse.getOs().toString());
        final String username = loginVM.getUsername();
        loginLog.setLoginName(username);
        loginLog.setBrowser(userAgentParse.getBrowser().toString());
        return loginLog;
    }
}
