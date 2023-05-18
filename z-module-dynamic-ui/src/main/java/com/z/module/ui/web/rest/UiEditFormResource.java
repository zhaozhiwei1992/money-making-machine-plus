package com.z.module.ui.web.rest;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.z.framework.common.web.rest.errors.BadRequestAlertException;
import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.framework.common.web.util.ResponseUtil;
import com.z.module.system.service.CommonEleService;
import com.z.module.ui.domain.UiEditForm;
import com.z.module.ui.repository.UiEditFormRepository;
import com.z.module.ui.web.vo.UiEditFormVO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Transactional
@Slf4j
public class UiEditFormResource {

    private static final String ENTITY_NAME = "uiEditform";

    private final UiEditFormRepository uiEditFormRepository;

    public UiEditFormResource(UiEditFormRepository uiEditFormRepository) {
        this.uiEditFormRepository = uiEditFormRepository;
    }

    /**
     * {@code POST  /ui-editforms} : Create a new uiEditform.
     *
     * @param uiEditform the uiEditform to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new uiEditform, or with
     * status {@code 400 (Bad Request)} if the uiEditform has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ui-editforms")
    public ResponseEntity<ResponseData<UiEditForm>> createUiEditForm(@RequestBody UiEditForm uiEditform) throws URISyntaxException {
        log.debug("REST request to save UiEditForm : {}", uiEditform);
        if (uiEditform.getId() != null) {
            throw new BadRequestAlertException("A new uiEditform cannot already have an ID", ENTITY_NAME, "idexists");
        }
        //      保存时如果没有录入排序号，或者录入为0, 则程序自动计算
        ExampleMatcher matcher = ExampleMatcher.matching();
        final UiEditForm filterObj = new UiEditForm();
        filterObj.setMenuId(uiEditform.getMenuId());
        final Example<UiEditForm> of = Example.of(filterObj, matcher);
        final long count = uiEditFormRepository.count(of);
        uiEditform.setOrderNum(Integer.parseInt(String.valueOf(count + 1)));
        if (StrUtil.isEmpty(uiEditform.getPlaceholder())) {
            uiEditform.setPlaceholder("请输入" + uiEditform.getName());
        }

        UiEditForm result = uiEditFormRepository.save(uiEditform);
        return ResponseData.ok(result);
    }

    /**
     * {@code GET  /ui-editforms} : get all the uiEditforms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of uiEditforms in body.
     */
    @GetMapping("/ui-editforms")
    public ResponseEntity<ResponseData<HashMap<String, Object>>> getAllUiEditForms(Pageable pageable) {
        log.debug("REST request to get a page of UiComponents");

        Page<UiEditForm> page = uiEditFormRepository.findAll(pageable);

        return ResponseData.ok(new HashMap<String, Object>(){{
            put("list", page.getContent());
            put("total", Long.valueOf(page.getTotalElements()).intValue());
        }});
    }

    /**
     * {@code GET  /ui-editforms/:id} : get the "id" uiEditform.
     *
     * @param id the id of the uiEditform to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the uiEditform, or with status
     * {@code 404 (Not Found)}.
     */
    @GetMapping("/ui-editforms/{id}")
    public ResponseEntity<UiEditForm> getUiEditForm(@PathVariable Long id) {
        log.debug("REST request to get UiEditForm : {}", id);
        Optional<UiEditForm> uiEditForm = uiEditFormRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(uiEditForm);
    }

    @Autowired
    private CommonEleService commonEleService;

    @GetMapping("/ui-editforms/menu/{menuid}")
    public ResponseEntity<ResponseData<Object>> getUiEditFormByMenuId(@PathVariable Long menuid) {
        log.debug("REST request to get UiEditForm by menu : {}", menuid);
        final List<UiEditForm> editformByMenuId = uiEditFormRepository.findByMenuIdOrderByOrderNumAsc(menuid);
        final List<UiEditFormVO> uiEditFormDtoList = editformByMenuId
                .stream()
                .map(uiEditform -> {
                    final UiEditFormVO convert = Convert.convert(UiEditFormVO.class, uiEditform);
                    if (uiEditform.getIsSource()) {
                        //  从config获取取数bean, 从而获取数据
                        final String configJson = uiEditform.getConfig();
                        final JSONObject jsonObject = JSONUtil.parseObj(configJson);
                        convert.setConfig(jsonObject);
                        final Object mapping = jsonObject.get("mapping");
                        if (!Objects.isNull(mapping) && StrUtil.isNotEmpty(String.valueOf(mapping))) {
                            // 2. 转换为翻译信息
                            convert.setMapping(commonEleService.transToMapping(String.valueOf(mapping)));
                        }
                    }

                    return convert;
                })
                .collect(Collectors.toList());
        return ResponseData.ok(uiEditFormDtoList);
    }

    @Operation(description = "删除编辑区配置")
    @DeleteMapping("/ui-editforms/{id}")
    public ResponseEntity<ResponseData<String>> deleteUiEditForm(@RequestBody List<Long> idList) {
        log.debug("REST request to delete Examples, ids: {}", idList);
        this.uiEditFormRepository.deleteAllByIdIn(idList);
        return ResponseData.ok("success");
    }
}
