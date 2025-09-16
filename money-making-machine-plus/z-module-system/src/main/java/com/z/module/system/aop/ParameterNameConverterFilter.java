package com.z.module.system.aop;

import cn.hutool.core.util.StrUtil;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * @Title: ParameterNameConverterFilter
 * @Package com/z/module/system/aop/ParameterNameConverterFilter.java
 * @Description: 前端请求参数转换, get请求通过 ?拼接参数, 后端通过对象接受默认不会转换, 需要自行特殊处理
 * 干脆搞成一样的, 都用驼峰, 转个p
 * //@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
 * @author zhaozhiwei
 * @date 2023/6/10 下午6:38
 * @version V1.0
 */
public class ParameterNameConverterFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ParameterNameRequestWrapper requestWrapper = new ParameterNameRequestWrapper(request);
        filterChain.doFilter(requestWrapper, response);
    }
}

class ParameterNameRequestWrapper extends HttpServletRequestWrapper {
    public ParameterNameRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String[] getParameterValues(String name) {
        // 转为下划线参数
        String originalName = name;
        if(!Arrays.asList("pageSize", "pageIndex").contains(name)){
            originalName = StrUtil.toUnderlineCase(name);;
        }
        return super.getParameterValues(originalName);
    }
}