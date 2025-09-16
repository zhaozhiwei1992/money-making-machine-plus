package com.z.module.system.service;

import com.z.module.system.web.vo.OnLineUserVO;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: null.java
 * @Package com.example.service
 * @Description:
 * 临时先通过服务map存储登录用户信息
 *
 * 用户量很大时可以调整为缓存存储
 * @date 2022/5/25 下午2:38
 */
@Service
public class OnLineUserService {

    private final CacheManager cacheManager;

    private Cache onlineUserCache;

    public OnLineUserService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        onlineUserCache = cacheManager.getCache("onlineUserCache");
    }

    public void add(OnLineUserVO onLineUserVO) {
        List<OnLineUserVO> onLineUserList ;
        if(Objects.isNull(onlineUserCache.get("onLineUserList"))){
            onLineUserList = new ArrayList<>();
        }else{
            onLineUserList = (List<OnLineUserVO>) onlineUserCache.get("onLineUserList").get();
        }
        onLineUserList.removeIf(next -> onLineUserVO.getUserName().equals(next.getUserName()));
        onLineUserList.add(onLineUserVO);
        onlineUserCache.put("onLineUserList", onLineUserList);
    }

    public void delete(String loginName) {
        List<OnLineUserVO> onLineUserList ;
        if(Objects.isNull(onlineUserCache.get("onLineUserList"))){
            onLineUserList = new ArrayList<>();
        }else{
            onLineUserList = (List<OnLineUserVO>) onlineUserCache.get("onLineUserList").get();
        }
        onLineUserList.removeIf(next -> loginName.equals(next.getUserName()));
        onlineUserCache.put("onLineUserList", onLineUserList);
    }

    public List<OnLineUserVO> findAll() {
        List<OnLineUserVO> onLineUserList ;
        if(Objects.isNull(onlineUserCache.get("onLineUserList"))){
            onLineUserList = new ArrayList<>();
        }else{
            onLineUserList = (List<OnLineUserVO>) onlineUserCache.get("onLineUserList").get();
        }
        return onLineUserList;
    }
}
