package com.z.module.system.web.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.z.module.system.domain.SystemParam;
import com.z.module.system.repository.SysParamRepository;
import com.z.framework.common.web.rest.vm.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "系统参数API")
@RestController
@RequestMapping("/api")
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class SysParamResource {

    private final SysParamRepository sysParamRepository;

    public SysParamResource(SysParamRepository taskParamRepository) {
        this.sysParamRepository = taskParamRepository;
    }

    /**
     * {@code POST  /admin/params}  : Creates a new task.
     * <p>
     * Creates a new task if the login and email are not already used, and sends an
     * mail with an activation link.
     * The task needs to be activated on creation.
     *
     * @param systemParam the task to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new task, or with status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(description = "新增系统参数")
    @PostMapping("/params")
    public ResponseEntity<ResponseData<SystemParam>> createSystemParam(@RequestBody SystemParam systemParam) throws URISyntaxException {
        log.debug("REST request to save SystemParam : {}", systemParam);

        SystemParam newSystemParam = sysParamRepository.save(systemParam);

        return ResponseData.ok(newSystemParam);
    }

    /**
     * {@code GET /admin/params} : get all params with all the details - calling this are only allowed for the administrators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all params.
     */

    @Operation(description = "获取系统参数")
    @GetMapping("/params")
    public ResponseEntity<ResponseData<HashMap<String, Object>>> getAllSystemParams(Pageable pageable, String key) {
        log.debug("REST request to get all SystemParam for an admin");

        // 根据id, 升序
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber()-1, pageable.getPageSize(), sort);

        Page<SystemParam> taskPage;
        // 搜索
        if(StrUtil.isNotEmpty(key)){
            final SystemParam task = new SystemParam();
            final List<String> cols = Collections.singletonList("name");
            //      2. 将传入属性, 填充给界面显示字段
            final Map<String, String> map = cols.stream().collect(Collectors.toMap(s -> s, key2 -> key));
            //      3. 动态构建查询条件
            BeanUtil.fillBeanWithMap(map, task, true);
            log.info("填充后对象信息 {}", task);

            //创建匹配器，即如何使用查询条件
            //构建对象
            ExampleMatcher matcher = ExampleMatcher
                    .matchingAny()
                    //改变默认字符串匹配方式：模糊查询
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                    //改变默认大小写忽略方式：忽略大小写
                    .withIgnoreCase(true)
                    //名字采用“开始匹配”的方式查询
                    .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.startsWith())
                    //忽略属性：是否关注。因为是基本类型，需要忽略掉
                    .withIgnorePaths("id");

            //创建实例
            Example<SystemParam> ex = Example.of(task, matcher);
            taskPage = sysParamRepository.findAll(ex, pageable);
        }else{
            taskPage = sysParamRepository.findAll(pageable);
        }

        return ResponseData.ok(new HashMap<String, Object>(){{
            put("list", taskPage.getContent());
            put("total", Long.valueOf(taskPage.getTotalElements()).intValue());
        }});
    }


    @Operation(description = "删除系统参数")
    @DeleteMapping("/params")
    public ResponseEntity<ResponseData<String>> deleteSystemParam(@RequestBody List<Long> idList) {
        log.debug("REST request to delete Examples, ids: {}", idList);
        this.sysParamRepository.deleteAllByIdIn(idList);
        return ResponseData.ok("success");
    }
}
