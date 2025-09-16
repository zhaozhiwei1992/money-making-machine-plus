package com.z.module.report.aop;

import com.z.module.report.util.TransactionContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @Title: TransactionalInterceptor
 * @Package com/z/module/report/aop/TransactionalInterceptor.java
 * @Description: 解决积木报表没有事务控制的问题, 天坑
 * @author zhao
 * @date 2023/5/29 下午5:44
 * @version V1.0
 */
@Component
public class TransactionalInterceptor implements HandlerInterceptor {

    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate transactionTemplate;

    public TransactionalInterceptor(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 在preHandle方法中开启事务
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(definition);
        TransactionContextHolder.setTransactionStatus(status);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 在afterCompletion方法中结束事务
        TransactionStatus status = TransactionContextHolder.getTransactionStatus();
        if (status != null) {
            if (ex != null) {
                transactionManager.rollback(status);
            } else {
                transactionManager.commit(status);
            }
            TransactionContextHolder.clearTransactionStatus();
        }
    }
}
