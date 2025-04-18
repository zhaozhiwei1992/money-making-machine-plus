package com.z.framework.security.config;

import cn.hutool.core.collection.CollUtil;
import com.z.framework.security.aop.AbstractPermissionFilterTemplate;
import com.z.framework.security.aop.JWTAuthenticationFilter;
import com.z.framework.security.service.TokenProviderService;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.util.*;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: SpringSecurityAutoConfiguration
 * @Package com/z/framework/security/config/SpringSecurityAutoConfiguration.java
 * @Description:
 * @EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
 * prePostEnabled = true 开启权限注解, 从而可以使用preAuthorize注解精细控制权限
 * 规则: org.springframework.security.access.expression.SecurityExpressionRoot
 * springboot3.x 不再使用WebSecurityConfigurerAdapter
 * @date 2024/8/13 上午11:59
 */
@AutoConfiguration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@ComponentScan(value = {"com.z.framework.security"})
@EnableConfigurationProperties(CustomSecurityProperties.class)
public class SpringSecurityAutoConfiguration {

    private final CustomSecurityProperties customSecurityProperties;

    /**
     * 需要放行的URL
     */
    public static String[] AUTH_WHITELIST = {
//            静态资源白名单
            "/index.html",
            "/assets/**",
            // other public endpoints of your API may be appended to this array
    };

    private final Environment env;

    public SpringSecurityAutoConfiguration(CustomSecurityProperties customSecurityProperties, Environment env) {
        this.customSecurityProperties = customSecurityProperties;
        this.env = env;

        // 白名单赋值, 业务扩展
        final List<String> authWhiteList = this.customSecurityProperties.getAuthWhiteList();
        authWhiteList.addAll(Arrays.asList(AUTH_WHITELIST));

        // 如果项目整体增加了前缀，则权限也要调整, 如: /mmmp/**
        String property = env.getProperty("spring.mvc.static-path-pattern");
        if(StringUtils.hasText(property)){
            String prefix = property.replace("/**", "");
            List<String> list = authWhiteList.stream().map(s -> prefix + s).toList();
            authWhiteList.addAll(list);
        }

        AUTH_WHITELIST = authWhiteList.stream().distinct().toArray(String[]::new);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenProviderService tokenProviderService;

    //    @Autowired
    public AbstractPermissionFilterTemplate abstractPermissionFilterTemplate;

    /**
     * 配置这个bean会在做AuthorizationServerConfigurer配置的时候使用
     *
     * @return
     * @throws Exception
     */
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//
//        return authenticationManagerBuilder.userDetailsService(userDetailsService)
//                .passwordEncoder(passwordEncoder()).and().build();
//    }

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 配置请求拦截, 3.x推荐方式
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        layui 的嵌套页面异常Refused to display in a frame because it set ‘X-Frame-Options‘ to ‘DENY‘
        //2.x
//        http.headers().frameOptions().disable();
        // 3.x写法
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        // cors开启跨域支持
        http.cors(Customizer.withDefaults())
                //由于使用的是JWT，我们这里不需要csrf
                .csrf(AbstractHttpConfigurer::disable)
                //基于token，所以不需要session
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() // 以前版本写法
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeRequests(auth -> auth.antMatchers(AUTH_WHITELIST).permitAll()) // 以前版本写法
                //可以匿名访问的链接
//                .dispatcherTypeMatchers(AUTH_WHITELIST).permitAll()
                //其他所有请求需要身份认证
//                .anyRequest().authenticated()
                .authorizeHttpRequests(auth -> auth
                        // 静态资源，可匿名访问
                        .requestMatchers(HttpMethod.GET, "/*.html", "/*.css", "/*.js").permitAll()
                        // 白名单， 可匿名访问
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        // WebFlux 异步请求，无需认证，目的：SSE 场景
                        .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                        // 兜底部分 都要认证
                        .anyRequest().authenticated())
                // Can't configure anyRequest after itself   5.2版本以后只能有一个anyRequest,坑
//                .anyRequest().access("@rbacServiceImpl.hasPermission(request, authentication)")
                .addFilterBefore(new JWTAuthenticationFilter(tokenProviderService, userDetailsService), UsernamePasswordAuthenticationFilter.class);
//        退出逻辑, 理想情况下退出逻辑走这里， 但是现在抛出事件前东西都被清空了，玩个蛋
        http.logout(logout -> logout
                // 必须与前端调用路径一致
                .logoutUrl("/api/loginOut")
                // 兼容有状态场景
                .invalidateHttpSession(true)
                // 若使用Cookie需清理
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler((request, response, authentication) -> {
                    // 从请求属性中获取保存的Authentication
                    if (authentication != null) {
                        LogoutSuccessEvent event = new LogoutSuccessEvent(authentication);
                        applicationContext.publishEvent(event);
                    }
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":200,\"msg\":\"退出成功\"}");
                })
        );


        // 打开下述注释可以自定义动态权限控制
//                .addFilterAfter(abstractPermissionFilterTemplate, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}