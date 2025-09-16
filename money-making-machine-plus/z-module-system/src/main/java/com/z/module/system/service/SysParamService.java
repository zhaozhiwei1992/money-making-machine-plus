package com.z.module.system.service;

import com.z.module.system.domain.SystemParam;
import com.z.module.system.repository.SysParamRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: null.java
 * @Package com.example.service
 * @Description:
 * 获取系统参数配置信息, 并缓存
 * @date 2022/3/24 下午4:45
 */
@Service
public class SysParamService {

    private static final String SYS_PARAM_CACHE = "sysParamCache";

    private final SysParamRepository sysParamRepository;

    public SysParamService(SysParamRepository sysParamRepository) {
        this.sysParamRepository = sysParamRepository;
    }

    /**
     * @data: 2022/9/27-上午11:50
     * @User: zhaozhiwei
     * @method: findByCode
      * @param code :
     * @return: com.longtu.domain.SystemParam
     * @Description: 获取缓存配置
     */
    @Cacheable(cacheNames = SYS_PARAM_CACHE)
    public SystemParam findByCode(String code) {
        return sysParamRepository.findOneByCode(code).orElse(new SystemParam());
    }
}
