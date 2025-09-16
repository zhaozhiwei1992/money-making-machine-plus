package com.z.module.ai.web.rest;

import com.z.module.ai.domain.PlatformConfig;
import com.z.module.ai.repository.PlatformConfigRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * 
 *
 * @author zhaozhiwei
 * @email zhaozhiweishanxi@gmail.com
 * @date 2025-09-16T06:54:51.516586245Z
 */
@Tag(name = "API")
@RestController
@RequestMapping("/api")
@Slf4j
public class PlatformConfigResource {

    @Autowired
    private PlatformConfigRepository platformConfigRepository;

    @PostMapping("/ai/platformconfig")
    @Operation(description = "新增")
    public PlatformConfig createPlatformConfig(@RequestBody PlatformConfig example) {
        log.debug("REST request to save PlatformConfig : {}", example);

        return this.platformConfigRepository.save(example);
    }

    /**
     * @Description: 所有数据获取
     */
    @GetMapping("/ai/platformconfig/all")
    @Operation(description = "查询所有")
    public List<PlatformConfig> getAllPlatformConfigs() {
        log.debug("REST request to get all PlatformConfig");
        return platformConfigRepository.findAll();
    }

    /**
     * @Description: 分页数据获取
     */
    @GetMapping("/ai/platformconfig")
    @Operation(description = "分页查询")
    public HashMap<String, Object> getAllPlatformConfigs(Pageable pageable, PlatformConfig platformConfig) {
        log.debug("REST request to get all PlatformConfig");
        // 默认根据id, 升序
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber()-1, pageable.getPageSize(), sort);

        Page<PlatformConfig> platformConfigPage;
        //创建匹配器，即如何使用查询条件
        //构建对象
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                //改变默认字符串匹配方式：模糊查询
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                //改变默认大小写忽略方式：忽略大小写
                .withIgnoreCase(true)
                //名字采用“开始匹配”的方式查询
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.startsWith())
                //忽略属性：是否关注。因为是基本类型，需要忽略掉
                .withIgnorePaths("id", "createdDate", "lastModifiedDate");

        //创建实例
        Example<PlatformConfig> ex = Example.of(platformConfig, matcher);
        platformConfigPage = platformConfigRepository.findAll(ex, pageable);

        return new HashMap<String, Object>(){{
            put("list", platformConfigPage.getContent());
            put("total", Long.valueOf(platformConfigPage.getTotalElements()).intValue());
        }};
    }

    /**
     * @Description: 批量删除
     */
    @DeleteMapping("/ai/platformconfig")
    @Operation(description = "批量删除")
    public String deletePlatformConfig(@RequestBody List<Long> idList) {
        log.debug("REST request to delete PlatformConfigs, ids: {}", idList);
        this.platformConfigRepository.deleteAllByIdIn(idList);
        return "success";
    }
}