package com.z.module.ui.web.rest;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.domain.UiEditform;
import com.example.repository.UiEditformRepository;
import com.example.service.CommonEleService;
import com.example.service.dto.UiEditformDTO;
import com.example.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link com.example.domain.UiEditform}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UiEditformResource {

    private final Logger log = LoggerFactory.getLogger(UiEditformResource.class);

    private static final String ENTITY_NAME = "uiEditform";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UiEditformRepository uiEditformRepository;

    public UiEditformResource(UiEditformRepository uiEditformRepository) {
        this.uiEditformRepository = uiEditformRepository;
    }

    /**
     * {@code POST  /ui-editforms} : Create a new uiEditform.
     *
     * @param uiEditform the uiEditform to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new uiEditform, or with status {@code 400 (Bad Request)} if the uiEditform has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ui-editforms")
    public ResponseEntity<UiEditform> createUiEditform(@RequestBody UiEditform uiEditform) throws URISyntaxException {
        log.debug("REST request to save UiEditform : {}", uiEditform);
        if (uiEditform.getId() != null) {
            throw new BadRequestAlertException("A new uiEditform cannot already have an ID", ENTITY_NAME, "idexists");
        }
        //      保存时如果没有录入排序号，或者录入为0, 则程序自动计算
        ExampleMatcher matcher = ExampleMatcher.matching();
        final UiEditform filterObj = new UiEditform();
        filterObj.setMenuid(uiEditform.getMenuid());
        final Example<UiEditform> of = Example.of(filterObj, matcher);
        final long count = uiEditformRepository.count(of);
        uiEditform.setOrdernum(Integer.parseInt(String.valueOf(count + 1)));
        if (StrUtil.isEmpty(uiEditform.getPlaceholder())) {
            uiEditform.setPlaceholder("请输入" + uiEditform.getName());
        }

        UiEditform result = uiEditformRepository.save(uiEditform);
        return ResponseEntity
            .created(new URI("/api/ui-editforms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ui-editforms/:id} : Updates an existing uiEditform.
     *
     * @param id the id of the uiEditform to save.
     * @param uiEditform the uiEditform to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uiEditform,
     * or with status {@code 400 (Bad Request)} if the uiEditform is not valid,
     * or with status {@code 500 (Internal Server Error)} if the uiEditform couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ui-editforms/{id}")
    public ResponseEntity<UiEditform> updateUiEditform(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UiEditform uiEditform
    ) throws URISyntaxException {
        log.debug("REST request to update UiEditform : {}, {}", id, uiEditform);
        if (uiEditform.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uiEditform.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uiEditformRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UiEditform result = uiEditformRepository.save(uiEditform);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, uiEditform.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /ui-editforms/:id} : Partial updates given fields of an existing uiEditform, field will ignore if it is null
     *
     * @param id the id of the uiEditform to save.
     * @param uiEditform the uiEditform to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uiEditform,
     * or with status {@code 400 (Bad Request)} if the uiEditform is not valid,
     * or with status {@code 404 (Not Found)} if the uiEditform is not found,
     * or with status {@code 500 (Internal Server Error)} if the uiEditform couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ui-editforms/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UiEditform> partialUpdateUiEditform(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UiEditform uiEditform
    ) throws URISyntaxException {
        log.debug("REST request to partial update UiEditform partially : {}, {}", id, uiEditform);
        if (uiEditform.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uiEditform.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uiEditformRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UiEditform> result = uiEditformRepository
            .findById(uiEditform.getId())
            .map(existingUiEditform -> {
                if (uiEditform.getMenuid() != null) {
                    existingUiEditform.setMenuid(uiEditform.getMenuid());
                }
                if (uiEditform.getCode() != null) {
                    existingUiEditform.setCode(uiEditform.getCode());
                }
                if (uiEditform.getName() != null) {
                    existingUiEditform.setName(uiEditform.getName());
                }
                if (uiEditform.getOrdernum() != null) {
                    existingUiEditform.setOrdernum(uiEditform.getOrdernum());
                }
                if (uiEditform.getIssource() != null) {
                    existingUiEditform.setIssource(uiEditform.getIssource());
                }
                if (uiEditform.getIsedit() != null) {
                    existingUiEditform.setIsedit(uiEditform.getIsedit());
                }
                if (uiEditform.getRequirement() != null) {
                    existingUiEditform.setRequirement(uiEditform.getRequirement());
                }
                if (uiEditform.getType() != null) {
                    existingUiEditform.setType(uiEditform.getType());
                }
                if (uiEditform.getPlaceholder() != null) {
                    existingUiEditform.setPlaceholder(uiEditform.getPlaceholder());
                }
                if (uiEditform.getConfig() != null) {
                    existingUiEditform.setConfig(uiEditform.getConfig());
                }

                return existingUiEditform;
            })
            .map(uiEditformRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, uiEditform.getId().toString())
        );
    }

    /**
     * {@code GET  /ui-editforms} : get all the uiEditforms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of uiEditforms in body.
     */
    @GetMapping("/ui-editforms")
    public ResponseEntity<List<UiEditform>> getAllUiEditforms(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false) Long menuid
    ) {
        log.debug("REST request to get a page of UiComponents");

        // 根据菜单精确匹配
        ExampleMatcher matcher = ExampleMatcher.matching();
        final UiEditform uiEditform = new UiEditform();
        uiEditform.setMenuid(menuid);
        final Example<UiEditform> of = Example.of(uiEditform, matcher);

        Page<UiEditform> page = uiEditformRepository.findAll(of, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ui-editforms/:id} : get the "id" uiEditform.
     *
     * @param id the id of the uiEditform to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the uiEditform, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ui-editforms/{id}")
    public ResponseEntity<UiEditform> getUiEditform(@PathVariable Long id) {
        log.debug("REST request to get UiEditform : {}", id);
        Optional<UiEditform> uiEditform = uiEditformRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(uiEditform);
    }

    @Autowired
    private CommonEleService commonEleService;

    @GetMapping("/ui-editforms/menu/{menuid}")
    public List<UiEditformDTO> getUiEditformByMenuid(@PathVariable Long menuid) {
        log.debug("REST request to get UiEditform by menu : {}", menuid);
        final List<UiEditform> editformByMenuId = uiEditformRepository.findByMenuidOrderByOrdernumAsc(menuid);
        final List<UiEditformDTO> uiEditformDtoList = editformByMenuId
            .stream()
            .map(uiEditform -> {
                final UiEditformDTO convert = Convert.convert(UiEditformDTO.class, uiEditform);
                if (uiEditform.getIssource()) {
                    //  从config获取取数bean, 从而获取数据
                    final String configJson = uiEditform.getConfig();
                    final JSONObject jsonObject = JSONUtil.parseObj(configJson);
                    convert.setConfig(jsonObject);
                    //                    final List<Map<String, Object>> mappingList = commonEleService.findMapping(jsonObject.getStr("eleBeanName"));
                    //                    convert.setMapping(mappingList);
                    final Object mapping = jsonObject.get("mapping");
                    if (!Objects.isNull(mapping) && StrUtil.isNotEmpty(String.valueOf(mapping))) {
                        // 2. 转换为翻译信息
                        convert.setMapping(commonEleService.transToMapping(String.valueOf(mapping)));
                    }
                }

                return convert;
            })
            .collect(Collectors.toList());
        return uiEditformDtoList;
    }

    /**
     * {@code DELETE  /ui-editforms/:id} : delete the "id" uiEditform.
     *
     * @param id the id of the uiEditform to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ui-editforms/{id}")
    public ResponseEntity<Void> deleteUiEditform(@PathVariable Long id) {
        log.debug("REST request to delete UiEditform : {}", id);
        uiEditformRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
