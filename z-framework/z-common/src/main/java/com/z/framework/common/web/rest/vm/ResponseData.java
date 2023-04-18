package com.z.framework.common.web.rest.vm;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ResponseData<T> implements Serializable {
    private static final long serialVersionUID = 5704430076796818950L;
    private String code;
    private String msg;
    private Date timestamps;
    private T data;
    private Integer count;

    public static <T> ResponseData<T> ok(){
        final ResponseData<T> tResponseData = new ResponseData<>();
        tResponseData.setCode("200");
        tResponseData.setMsg("请求成功");
        tResponseData.setTimestamps(new Date());
        return tResponseData;
    }

    public static <T> ResponseData<T> ok(T t){
        final ResponseData<T> tResponseData = new ResponseData<>();
        tResponseData.setCode("200");
        tResponseData.setMsg("请求成功");
        tResponseData.setData(t);
        tResponseData.setCount(0);
        tResponseData.setTimestamps(new Date());
        return tResponseData;
    }

    public static <T> ResponseData<T> fail(){
        final ResponseData<T> tResponseData = new ResponseData<>();
        tResponseData.setCode("500");
        tResponseData.setMsg("请求失败");
        tResponseData.setTimestamps(new Date());
        return tResponseData;
    }

    public static <T> ResponseData<T> fail(String msg){
        final ResponseData<T> tResponseData = new ResponseData<>();
        tResponseData.setCode("500");
        tResponseData.setMsg(msg);
        tResponseData.setTimestamps(new Date());
        return tResponseData;
    }
}
