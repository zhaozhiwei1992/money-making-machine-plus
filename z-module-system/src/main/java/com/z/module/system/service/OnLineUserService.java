package com.z.module.system.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private List<Map<String, Object>> onlineUserList = new ArrayList<>();

    public void add(Map<String, Object> map) {
        onlineUserList.removeIf(next -> map.get("userName").equals(next.get("userName")));
        onlineUserList.add(map);
    }

    public void delete(String loginName) {
        onlineUserList.removeIf(next -> loginName.equals(next.get("userName")));
    }

    public List<Map<String, Object>> findAll() {
        return onlineUserList;
    }
}
