package com.example.web.mapper;

import org.springframework.stereotype.Component;

/**
 * @Title: BooleanStringFormat
 * @Package com/longtu/web/mapper/BooleanStringFormat.java
 * @Description: 是否转换
 * @author zhaozhiwei
 * @date 2022/8/17 上午11:20
 * @version V1.0
 */
@Component
public class BooleanStringFormat {

    public String toString(Boolean disabled){
        if (disabled) {
            return "是";
        }
        return "否";
    }

    public Boolean toBoolean(String disable){
        return "是".equals(disable);
    }
}