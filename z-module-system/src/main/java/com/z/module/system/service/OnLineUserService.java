package com.z.module.system.service;

import com.z.module.system.web.vo.OnLineUserVO;
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

    private List<OnLineUserVO> onlineUserList = new ArrayList<>();

    public void add(OnLineUserVO onLineUserVO) {
        onlineUserList.removeIf(next -> onLineUserVO.getUserName().equals(next.getUserName()));
        onlineUserList.add(onLineUserVO);
    }

    public void delete(String loginName) {
        onlineUserList.removeIf(next -> loginName.equals(next.getUserName()));
    }

    public List<OnLineUserVO> findAll() {
        return onlineUserList;
    }
}
