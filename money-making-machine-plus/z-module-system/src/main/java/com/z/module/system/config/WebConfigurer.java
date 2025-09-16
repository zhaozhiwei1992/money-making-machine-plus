package com.z.module.system.config;

import com.z.framework.common.config.CommonProperties;
import com.z.framework.operatelog.config.RequestLogAutoConfiguration;
import com.z.framework.common.exception.handler.CustomResponseErrorHandler;
import com.z.module.system.aop.ParameterNameConverterFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.ServletContext;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
public class WebConfigurer implements ServletContextInitializer, WebServerFactoryCustomizer<WebServerFactory>,
        WebMvcConfigurer {

    private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

    private final Environment env;

    private final CommonProperties commonProperties;

    public WebConfigurer(Environment env, CommonProperties commonProperties) {
        this.env = env;
        this.commonProperties = commonProperties;
    }

    @Override
    public void onStartup(ServletContext servletContext) {
        if (env.getActiveProfiles().length != 0) {
            log.info("Web application configuration, using profiles: {}", (Object[]) env.getActiveProfiles());
        }

        log.info("Web application fully configured");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 确保Jackson转换器优先于String转换器
        // 注意 这玩意儿会导致有些不需要"号的情况前端解析有问题，比如base64的验证码图片, 除非前端统一处理json, 慎用
        // 伪代码展示Jackson行为,
        //String original = "hello";
        //String jsonEncoded = objectMapper.writeValueAsString(original);
        // 输出: ""hello""（外层引号是JSON规范，内层是字符串内容）
//        converters.addFirst(new MappingJackson2HttpMessageConverter());
    }

    /**
     * Customize the Servlet engine: Mime types, the document root, the cache.
     */
    @Override
    public void customize(WebServerFactory server) {
        // When running in an IDE or with ./mvnw spring-boot:run, set location of the static web assets.
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = commonProperties.getCors();
        log.debug("Registering CORS filter: {}", config);
        source.registerCorsConfiguration("/api/**", config);
        source.registerCorsConfiguration("/management/**", config);
        source.registerCorsConfiguration("/v2/api-docs", config);
        source.registerCorsConfiguration("/swagger-ui/**", config);
        return new CorsFilter(source);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

    }

    /**
     * @param registry :
     * @data: 2023/5/21-下午3:21
     * @User: zhaozhiwei
     * @method: addResourceHandlers
     * @return: void
     * @Description: 如果有静态资源打到jar包里找不到的需要这里搞搞
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter =
                new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_OCTET_STREAM));
        restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
        restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        return restTemplate;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 增加固定前缀, 所有RestController增加/api, 可以分别自定义注解, 或者分模块
        // 有可能会有依赖内部使用了RestController注解导致错误, 自定义注解靠谱点
//        configurer.addPathPrefix("/api", c -> c.isAnnotationPresent(RestController.class));
        // 通过包名统一增加前缀, 更科学点, 或者每个模块自行处理
        // 如果设置了静态资源前缀, 则接口前缀也要增加
        String property = env.getProperty("spring.mvc.static-path-pattern");
        String prefix = commonProperties.RESOURCE_PRE;
        if(StringUtils.hasText(property)){
            prefix = property.replace("/**", "") + prefix;
        }
        configurer.addPathPrefix(prefix,
                // 所有在web.rest包下, 并且使用RestController注解的都要增加 /api
                c -> c.isAnnotationPresent(RestController.class)
                        && c.getPackage().getName().contains("web.rest")
                        // 报表的请求依赖包内部封装, 跳过不进行地址转换(不增加api)
                        && !c.getPackage().getName().contains("module.report")
        );
    }

    /**
     * @data: 2023/6/10-下午6:28
     * @User: zhaozhiwei
     * @method: getParameterNameConverterFilterRegistrationBean

     * @return: org.springframework.boot.web.servlet.FilterRegistrationBean<com.z.module.system.aop.ParameterNameConverterFilter>
     * @Description: 请求参数转换过滤器, 处理下划线参数如login_name, 参数接收不到问题
     */
//    @Bean
//    public FilterRegistrationBean<ParameterNameConverterFilter> getParameterNameConverterFilterRegistrationBean() {
//        FilterRegistrationBean<ParameterNameConverterFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new ParameterNameConverterFilter());
//        registrationBean.addUrlPatterns("/api/*");
//        registrationBean.setName("parameterNameConverterFilter");
//        registrationBean.setOrder(0);
//        return registrationBean;
//    }
}
