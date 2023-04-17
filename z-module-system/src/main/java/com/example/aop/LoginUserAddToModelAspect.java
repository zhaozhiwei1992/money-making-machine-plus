package com.example.aop;

import com.longtu.domain.User;
import com.longtu.repository.UserRepository;
import com.longtu.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.math.BigInteger;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 服务端跳转统一返回用户信息
 * Controller必须增加Model参数
 */
@Component
@Aspect
@Slf4j
public class LoginUserAddToModelAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoginUserAddToModelAspect.class);

    private final ThreadLocal<Long> beginTime = new ThreadLocal<>();

    /**
     * 切入点就是后面的表达式
     * @param joinPoint
     */
    @Before("execution(* com.longtu.web.controller..*.*(..))")
    public void before(JoinPoint joinPoint){
        // 记录请求到达时间
        beginTime.set(System.currentTimeMillis());
        logger.info(String.format("当前请求: %s, 开始时间: %s", joinPoint.toString(), beginTime.get()));
    }

    @Autowired
    private UserRepository userRepository;

    @After("execution(* com.longtu.web.controller..*.*(..))")
    public void after(JoinPoint joinPoint){
        final String format = String.format("当前请求: %s, 耗时: %s", joinPoint.toString(),
                System.currentTimeMillis() - (Objects.isNull(beginTime.get())? 0:beginTime.get()));

        final MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        final String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Stream.iterate(BigInteger.ZERO, bigInteger -> bigInteger.add(BigInteger.ONE)).limit(parameterNames.length).forEach(indexOf ->{
            final String parameterName = parameterNames[indexOf.intValue()];
            final Object argValue = args[indexOf.intValue()];
            logger.info("after: 参数名称 {}, 参数值: {} ", parameterName, argValue);
//            参数名称 model, 参数值: {loginUser=User(
            if(argValue instanceof Model){
                Model model = (Model) argValue;
                // 获取当前用户并塞入model
                final String currentLoginName = SecurityUtils.getCurrentLoginName();
                if("anonymousUser".equals(currentLoginName)){
                    model.addAttribute("loginUser", new User());
                }else{
                    final User user = userRepository.findOneByLogin(currentLoginName).orElse(new User());
                    model.addAttribute("loginUser", user);
                }
            }

        });
        log.info(format);
    }

}
