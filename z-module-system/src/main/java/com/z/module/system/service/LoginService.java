package com.z.module.system.service;

import com.z.framework.security.util.SecurityUtils;
import com.z.module.system.domain.UserOpenId;
import com.z.module.system.repository.UserOpenIdRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: null.java
 * @Package com.example.service
 * @Description:
 * 一些登录的特殊处理, 如记录白名单
 * @date 2022/3/24 下午4:45
 */
@Service
public class LoginService {


    private final CacheManager cacheManager;

    public LoginService(CacheManager cacheManager, UserOpenIdRepository userOpenIdRepository) {
        this.cacheManager = cacheManager;
        this.userOpenIdRepository = userOpenIdRepository;
    }

    public void addTokenWriteList(String token) {
        final Cache tokenWriteListCache = cacheManager.getCache("tokenWriteListCache");
        List<String> tokenWriteList;
        if(Objects.isNull(tokenWriteListCache.get("tokenWriteList"))){
            tokenWriteList = new ArrayList<>();
        }else{
            tokenWriteList = (List<String>) tokenWriteListCache.get("tokenWriteList").get();
        }
        tokenWriteList.add(token);
        tokenWriteListCache.put("tokenWriteList", tokenWriteList);
    }

    public void removeTokenWriteList() {
        // 退出时同时从白名单下线登录用户
        final Cache tokenBlackCache = cacheManager.getCache("tokenWriteListCache");
        List<String> tokenWriteList;
        if(!Objects.isNull(tokenBlackCache.get("tokenWriteList"))){
            tokenWriteList = (List<String>) tokenBlackCache.get("tokenWriteList").get();
            tokenWriteList.remove(SecurityUtils.getTokenId());
            tokenBlackCache.put("tokenWriteList", tokenWriteList);
        }
    }

    private final UserOpenIdRepository userOpenIdRepository;

    /**
     * @data: 2024/8/20-上午10:34
     * @User: zhaozhiwei
     * @method: saveOpenidUserLogin
      * @param openid :
 * @param login :
     * @return: void
     * @Description: 保存openid和用户登录信息
     */
    @Transactional
    public void saveOpenidUserLogin(String openid, String login) {
        // 1. 根据用户和openId分别删除数据
        userOpenIdRepository.deleteByLogin(login);
        userOpenIdRepository.deleteByOpenId(openid);
        // 2. 保存新数据
        final UserOpenId userOpenId = new UserOpenId();
        userOpenId.setLogin(login);
        userOpenId.setOpenId(openid);
        userOpenIdRepository.save(userOpenId);
    }
}
