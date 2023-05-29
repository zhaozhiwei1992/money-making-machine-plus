package com.z.module.report.util;

import org.springframework.transaction.TransactionStatus;

public class TransactionContextHolder {

    private static final ThreadLocal<TransactionStatus> transactionStatusThreadLocal = new ThreadLocal<>();

    public static void setTransactionStatus(TransactionStatus transactionStatus) {
        transactionStatusThreadLocal.set(transactionStatus);
    }

    public static TransactionStatus getTransactionStatus() {
        return transactionStatusThreadLocal.get();
    }

    public static void clearTransactionStatus() {
        transactionStatusThreadLocal.remove();
    }
}