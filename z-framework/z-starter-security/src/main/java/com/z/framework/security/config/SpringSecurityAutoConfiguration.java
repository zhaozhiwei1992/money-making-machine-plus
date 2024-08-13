package com.z.framework.security.config;

import com.z.framework.security.aop.JWTAuthenticationFilter;
import com.z.framework.security.aop.AbstractPermissionFilterTemplate;
import com.z.framework.security.service.TokenProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: SpringSecurityAutoConfiguration
 * @Package com/z/framework/security/config/SpringSecurityAutoConfiguration.java
 * @Description:
 * @EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
 * prePostEnabled = true 开启权限注解, 从而可以使用preAuthorize注解精细控制权限
 * 规则: org.springframework.security.access.expression.SecurityExpressionRoot
 * @date 2024/8/13 上午11:59
 */
@AutoConfiguration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@ComponentScan(value = {"com.z.framework.security"})
@EnableConfigurationProperties(CustomSecurityProperties.class)
public class SpringSecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

    private final CustomSecurityProperties customSecurityProperties;

    /**
     * 需要放行的URL
     */
    public static String[] AUTH_WHITELIST = {
//            静态资源白名单
            "/index.html",
            "/assets/**",
            "/favicon.ico",
            "/logo.png",
            "/static/**",
            "/images/**",
            "/css/**",
            "/js/**",
            "/layui/**",
            "/bootstrap-5.0.2-dist/**",
            "/font-awesome-4.7.0/**",
            "/druid/**",
            "/lay-config.js",
            "/echarts/**",
//            url 白名单
            "/login",
            "/api/login",
            "/api/mobile/login/**",
            "/actuator/**",
//           验证码
            "/captcha/numCode",
            "/api/captcha/numCode",
            "/dashboard/analysis"
            // 临时测试
//            "/",
//            "/index",
//            "/api/**",
//            "/user/**"
            // other public endpoints of your API may be appended to this array
    };

    public SpringSecurityAutoConfiguration(CustomSecurityProperties customSecurityProperties) {
        this.customSecurityProperties = customSecurityProperties;

        // 白名单赋值, 业务扩展
        final List<String> authWhiteList = this.customSecurityProperties.getAuthWhiteList();
        authWhiteList.addAll(Arrays.asList(AUTH_WHITELIST));
        AUTH_WHITELIST = authWhiteList.stream().distinct().collect(Collectors.toList()).toArray(new String[0]);
    }

    /**
     * 配置这个bean会在做AuthorizationServerConfigurer配置的时候使用
     *
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Autowired
    private TokenProviderService tokenProviderService;

    //    @Autowired
    public AbstractPermissionFilterTemplate abstractPermissionFilterTemplate;

    /**
     * 配置请求拦截
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        layui 的嵌套页面异常Refused to display in a frame because it set ‘X-Frame-Options‘ to ‘DENY‘
        http.headers().frameOptions().disable();

        http.cors().and()
                //由于使用的是JWT，我们这里不需要csrf
                .csrf().disable()
                //基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                //可以匿名访问的链接
                .antMatchers(AUTH_WHITELIST).permitAll()
                //其他所有请求需要身份认证
                .anyRequest().authenticated()
                // Can't configure anyRequest after itself   5.2版本以后只能有一个anyRequest,坑
//                .anyRequest().access("@rbacServiceImpl.hasPermission(request, authentication)")
                .and()
                .addFilterBefore(new JWTAuthenticationFilter(tokenProviderService, authenticationManager(), userDetailsService), UsernamePasswordAuthenticationFilter.class);

        // 打开下述注释可以自定义动态权限控制
//                .addFilterAfter(abstractPermissionFilterTemplate, UsernamePasswordAuthenticationFilter.class);
    }
}