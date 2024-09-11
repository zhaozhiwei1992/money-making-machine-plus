package com.z.framework.security.config;

import com.z.framework.security.aop.AbstractPermissionFilterTemplate;
import com.z.framework.security.aop.JWTAuthenticationFilter;
import com.z.framework.security.service.TokenProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

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
                .authorizeHttpRequests(auth -> auth.requestMatchers(AUTH_WHITELIST).permitAll().anyRequest().authenticated())
                // Can't configure anyRequest after itself   5.2版本以后只能有一个anyRequest,坑
//                .anyRequest().access("@rbacServiceImpl.hasPermission(request, authentication)")
                .addFilterBefore(new JWTAuthenticationFilter(tokenProviderService, userDetailsService), UsernamePasswordAuthenticationFilter.class);

        // 打开下述注释可以自定义动态权限控制
//                .addFilterAfter(abstractPermissionFilterTemplate, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}