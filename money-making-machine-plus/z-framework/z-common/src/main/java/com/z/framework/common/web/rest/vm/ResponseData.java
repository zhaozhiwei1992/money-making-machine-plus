package com.z.framework.common.web.rest.vm;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

@Data
public class ResponseData<T> implements Serializable {
    private static final long serialVersionUID = 5704430076796818950L;
    // 注意这个code是数据, 前端一般做判断是===, str === num为false
    private Integer code;
    private String msg;
    private Date timestamps;
    private T data;
    private Integer count;

    public static <T> ResponseData<T> ok(){
        final ResponseData<T> tResponseData = new ResponseData<>();
        tResponseData.setCode(200);
        tResponseData.setMsg("请求成功");
        tResponseData.setTimestamps(new Date());
        return tResponseData;
    }

    public static <T> ResponseData<T> ok(T t){
        final ResponseData<T> tResponseData = new ResponseData<>();
        tResponseData.setCode(200);
        tResponseData.setMsg("请求成功");
        tResponseData.setData(t);
        tResponseData.setTimestamps(new Date());
        return tResponseData;
    }

    public static <T> ResponseData<T> ok(T t, int count){
        final ResponseData<T> tResponseData = new ResponseData<>();
        tResponseData.setCode(200);
        tResponseData.setMsg("请求成功");
        tResponseData.setData(t);
        tResponseData.setCount(count);
        tResponseData.setTimestamps(new Date());
        return tResponseData;
    }

    public static <T> ResponseData<T> fail(){
        final ResponseData<T> tResponseData = new ResponseData<>();
        tResponseData.setCode(500);
        tResponseData.setMsg("请求失败");
        tResponseData.setTimestamps(new Date());
        return tResponseData;
    }

    public static <T> ResponseData<T> fail(String msg){
        final ResponseData<T> tResponseData = new ResponseData<>();
        tResponseData.setCode(500);
        tResponseData.setMsg(msg);
        tResponseData.setTimestamps(new Date());
        return tResponseData;
    }
}
