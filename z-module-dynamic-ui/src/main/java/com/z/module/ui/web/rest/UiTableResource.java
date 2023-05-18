package com.z.module.ui.web.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.domain.SysCollectCol;
import com.example.domain.UiComponent;
import com.example.domain.UiTable;
import com.example.repository.SysCollectColRepository;
import com.example.repository.UiComponentRepository;
import com.example.repository.UiTableRepository;
import com.example.service.CommonEleService;
import com.example.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link com.example.domain.UiTable}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UiTableResource {

    private final Logger log = LoggerFactory.getLogger(UiTableResource.class);

    private static final String ENTITY_NAME = "uiTable";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UiTableRepository uiTableRepository;

    public UiTableResource(
        UiTableRepository uiTableRepository,
        UiComponentRepository uiComponentRepository,
        SysCollectColRepository sysCollectColRepository,
        CommonEleService commonEleService
    ) {
        this.uiTableRepository = uiTableRepository;
        this.uiComponentRepository = uiComponentRepository;
        this.sysCollectColRepository = sysCollectColRepository;
        this.commonEleService = commonEleService;
    }

    /**
     * {@code POST  /ui-tables} : Create a new uiTable.
     *
     * @param uiTable the uiTable to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new uiTable, or with
     * status {@code 400 (Bad Request)} if the uiTable has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ui-tables")
    public ResponseEntity<UiTable> createUiTable(@RequestBody UiTable uiTable) throws URISyntaxException {
        log.debug("REST request to save UiTable : {}", uiTable);
        if (uiTable.getId() != null) {
            throw new BadRequestAlertException("A new uiTable cannot already have an ID", ENTITY_NAME, "idexists");
        }

        ExampleMatcher matcher = ExampleMatcher.matching();
        final UiTable filterObj = new UiTable();
        filterObj.setMenuid(uiTable.getMenuid());
        final Example<UiTable> of = Example.of(filterObj, matcher);
        final long count = uiTableRepository.count(of);
        uiTable.setOrdernum(Integer.parseInt(String.valueOf(count + 1)));

        UiTable result = uiTableRepository.save(uiTable);
        return ResponseEntity
            .created(new URI("/api/ui-tables/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ui-tables/:id} : Updates an existing uiTable.
     *
     * @param id      the id of the uiTable to save.
     * @param uiTable the uiTable to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uiTable,
     * or with status {@code 400 (Bad Request)} if the uiTable is not valid,
     * or with status {@code 500 (Internal Server Error)} if the uiTable couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ui-tables/{id}")
    public ResponseEntity<UiTable> updateUiTable(@PathVariable(value = "id", required = false) final Long id, @RequestBody UiTable uiTable)
        throws URISyntaxException {
        log.debug("REST request to update UiTable : {}, {}", id, uiTable);
        if (uiTable.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uiTable.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uiTableRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UiTable result = uiTableRepository.save(uiTable);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, uiTable.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /ui-tables/:id} : Partial updates given fields of an existing uiTable, field will ignore if it
     * is null
     *
     * @param id      the id of the uiTable to save.
     * @param uiTable the uiTable to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uiTable,
     * or with status {@code 400 (Bad Request)} if the uiTable is not valid,
     * or with status {@code 404 (Not Found)} if the uiTable is not found,
     * or with status {@code 500 (Internal Server Error)} if the uiTable couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ui-tables/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UiTable> partialUpdateUiTable(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UiTable uiTable
    ) throws URISyntaxException {
        log.debug("REST request to partial update UiTable partially : {}, {}", id, uiTable);
        if (uiTable.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uiTable.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uiTableRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UiTable> result = uiTableRepository
            .findById(uiTable.getId())
            .map(existingUiTable -> {
                if (uiTable.getMenuid() != null) {
                    existingUiTable.setMenuid(uiTable.getMenuid());
                }
                if (uiTable.getCode() != null) {
                    existingUiTable.setCode(uiTable.getCode());
                }
                if (uiTable.getName() != null) {
                    existingUiTable.setName(uiTable.getName());
                }
                if (uiTable.getOrdernum() != null) {
                    existingUiTable.setOrdernum(uiTable.getOrdernum());
                }
                if (uiTable.getIssource() != null) {
                    existingUiTable.setIssource(uiTable.getIssource());
                }
                if (uiTable.getIsedit() != null) {
                    existingUiTable.setIsedit(uiTable.getIsedit());
                }
                if (uiTable.getRequirement() != null) {
                    existingUiTable.setRequirement(uiTable.getRequirement());
                }
                if (uiTable.getType() != null) {
                    existingUiTable.setType(uiTable.getType());
                }
                if (uiTable.getConfig() != null) {
                    existingUiTable.setConfig(uiTable.getConfig());
                }

                return existingUiTable;
            })
            .map(uiTableRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, uiTable.getId().toString())
        );
    }

    /**
     * {@code GET  /ui-tables} : get all the uiTables.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of uiTables in body.
     */
    @GetMapping("/ui-tables")
    public ResponseEntity<List<UiTable>> getAllUiTables(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false) Long menuid
    ) {
        log.debug("REST request to get a page of UiComponents");

        // 根据菜单精确匹配
        ExampleMatcher matcher = ExampleMatcher.matching();
        final UiTable uiTable = new UiTable();
        uiTable.setMenuid(menuid);
        final Example<UiTable> of = Example.of(uiTable, matcher);

        Page<UiTable> page = uiTableRepository.findAll(of, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ui-tables/:id} : get the "id" uiTable.
     *
     * @param id the id of the uiTable to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the uiTable, or with status
     * {@code 404 (Not Found)}.
     */
    @GetMapping("/ui-tables/{id}")
    public ResponseEntity<UiTable> getUiTable(@PathVariable Long id) {
        log.debug("REST request to get UiTable : {}", id);
        Optional<UiTable> uiTable = uiTableRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(uiTable);
    }

    private final UiComponentRepository uiComponentRepository;

    private final SysCollectColRepository sysCollectColRepository;

    private final CommonEleService commonEleService;

    @GetMapping("/ui-tables/menu/{menuid}")
    public List<Map<String, Object>> getUiTableByMenuid(@PathVariable Long menuid) {
        log.debug("REST request to get UiTable by menu : {}", menuid);
        List<UiTable> uiTableCols = uiTableRepository.findByMenuidOrderByOrdernumAsc(menuid);
        // 如果没有列配置信息, 则获取组件配置信息,判断是否为采集表
        if (Objects.isNull(uiTableCols) || uiTableCols.size() < 1) {
            //1. 获取组件配置中的tab_id
            Optional<UiComponent> tableComponent = uiComponentRepository.findByMenuidAndComponentid(menuid, "uitable");
            // 2. 通过采集表配置构建成UiTableList信息
            if (tableComponent.isPresent()) {
                final JSONObject jsonObject = JSONUtil.parseObj(tableComponent.get().getConfig());
                final String tabId = jsonObject.getStr("tab_id");
                final List<SysCollectCol> collectCols = sysCollectColRepository.findAllByTabIdOrderByOrderNum(Long.parseLong(tabId));
                uiTableCols = new ArrayList<>();
                for (SysCollectCol collectCol : collectCols) {
                    final UiTable uiTable = new UiTable();
                    uiTable.setCode(collectCol.getColEname());
                    uiTable.setName(collectCol.getColCname());
                    uiTable.setIssource(false);
                    uiTable.setIsedit(collectCol.getIsEdit());
                    uiTable.setRequirement(collectCol.getRequirement());
                    uiTable.setType(collectCol.getType());
                    final Map<String, String> config = new HashMap<>();
                    if (!Objects.isNull(collectCol.getSource()) && StrUtil.isNotEmpty(collectCol.getSource())) {
                        config.put("mapping", collectCol.getSource());
                        uiTable.setConfig(JSONUtil.toJsonStr(config));
                    }
                    uiTableCols.add(uiTable);
                }
            }
        }

        // 转换为map, 填充翻译值集信息
        final List<Map<String, Object>> collect = uiTableCols
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
        return collect;
    }

    /**
     * {@code DELETE  /ui-tables/:id} : delete the "id" uiTable.
     *
     * @param id the id of the uiTable to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ui-tables/{id}")
    public ResponseEntity<Void> deleteUiTable(@PathVariable Long id) {
        log.debug("REST request to delete UiTable : {}", id);
        uiTableRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
