package com.z.module.ui.web.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.z.framework.common.web.rest.errors.BadRequestAlertException;
import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.module.system.service.CommonEleService;
import com.z.module.ui.domain.UiTable;
import com.z.module.ui.repository.UiComponentRepository;
import com.z.module.ui.repository.UiTableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ui")
@Transactional
public class UiTableResource {

    private final Logger log = LoggerFactory.getLogger(UiTableResource.class);

    private static final String ENTITY_NAME = "uiTable";

    private final UiTableRepository uiTableRepository;

    public UiTableResource(
            UiTableRepository uiTableRepository,
            UiComponentRepository uiComponentRepository,
            CommonEleService commonEleService
    ) {
        this.uiTableRepository = uiTableRepository;
        this.uiComponentRepository = uiComponentRepository;
        this.commonEleService = commonEleService;
    }

    /**
     * {@code POST  /tables} : Create a new uiTable.
     *
     * @param uiTable the uiTable to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new uiTable, or with
     * status {@code 400 (Bad Request)} if the uiTable has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tables")
    public UiTable createUiTable(@RequestBody UiTable uiTable) throws URISyntaxException {
        log.debug("REST request to save UiTable : {}", uiTable);
        if (uiTable.getId() != null) {
            throw new BadRequestAlertException("A new uiTable cannot already have an ID", ENTITY_NAME, "idexists");
        }

        ExampleMatcher matcher = ExampleMatcher.matching();
        final UiTable filterObj = new UiTable();
        filterObj.setMenuId(uiTable.getMenuId());
        final Example<UiTable> of = Example.of(filterObj, matcher);
        final long count = uiTableRepository.count(of);
        uiTable.setOrderNum(Integer.parseInt(String.valueOf(count + 1)));

        return uiTableRepository.save(uiTable);
    }

    /**
     * {@code GET  /tables} : get all the uiTables.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of uiTables in body.
     */
    @GetMapping("/tables")
    public HashMap<String, Object> getAllUiTables(Pageable pageable) {
        Page<UiTable> page = uiTableRepository.findAll(pageable);
        return new HashMap<String, Object>() {{
            put("list", page.getContent());
            put("total", Long.valueOf(page.getTotalElements()).intValue());
        }};
    }

    private final UiComponentRepository uiComponentRepository;

    private final CommonEleService commonEleService;

    @GetMapping("/tables/menu/{menuid}")
    public List<Map<String, Object>> getUiTableByMenuId(@PathVariable Long menuid) {
        log.debug("REST request to get UiTable by menu : {}", menuid);
        List<UiTable> uiTableCols = uiTableRepository.findByMenuIdOrderByOrderNumAsc(menuid);

        // TODO 多表头处理

        // 转换为map, 填充翻译值集信息
        // 根据是否存在数据源配置来填充mapping信息, 对于select需要id, label, 对于cascader, 则要构建为树型
        // 所以直接不管是啥都按树来即可
        // config中获取mapping配置, 为ele中的eleCatCode编码
        // 2. 转换为翻译信息
        return uiTableCols
                .stream()
                .map(bean -> {
                    final Map<String, Object> map = BeanUtil.beanToMap(bean);
                    // 根据是否存在数据源配置来填充mapping信息, 对于select需要id, label, 对于cascader, 则要构建为树型
                    // 所以直接不管是啥都按树来即可
                    // config中获取mapping配置, 为ele中的eleCatCode编码
                    final String config = bean.getConfig();
                    final JSONObject jsonObject = JSONUtil.parseObj(config);
                    final Object mapping = jsonObject.get("mapping");
                    if (!Objects.isNull(mapping) && StrUtil.isNotEmpty(String.valueOf(mapping))) {
                        // 2. 转换为翻译信息
                        map.put("mapping", commonEleService.transToMapping(String.valueOf(mapping)));
                    }
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * {@code DELETE  /tables/:id} : delete the "id" uiTable.
     *
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tables")
    public String deleteUiTable(@RequestBody List<Long> idList) {
        this.uiTableRepository.deleteAllByIdIn(idList);
        return "success";
    }
}
