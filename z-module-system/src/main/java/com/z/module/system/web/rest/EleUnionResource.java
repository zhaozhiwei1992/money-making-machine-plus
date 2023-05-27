package com.z.module.system.web.rest;

import com.z.framework.common.web.rest.errors.BadRequestAlertException;
import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.module.system.domain.EleUnion;
import com.z.module.system.repository.EleUnionRepository;
import com.z.module.system.service.CommonEleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link com.example.domain.EleUnion}.
 * 基础信息, 数据源信息
 */
@RestController
@Transactional
public class EleUnionResource {

    private final Logger log = LoggerFactory.getLogger(EleUnionResource.class);

    private static final String ENTITY_NAME = "eleUnion";

    private final EleUnionRepository eleUnionRepository;

    private final CommonEleService commonEleService;

    public EleUnionResource(EleUnionRepository eleUnionRepository, CommonEleService commonEleService) {
        this.eleUnionRepository = eleUnionRepository;
        this.commonEleService = commonEleService;
    }

    /**
     * {@code POST  /ele-unions} : Create a new eleUnion.
     *
     * @param eleUnion the eleUnion to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eleUnion, or with
     * status {@code 400 (Bad Request)} if the eleUnion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ele-unions")
    public ResponseEntity<ResponseData<EleUnion>> createEleUnion(@RequestBody EleUnion eleUnion) throws URISyntaxException {
        log.debug("REST request to save EleUnion : {}", eleUnion);
        if (eleUnion.getId() != null) {
            throw new BadRequestAlertException("A new eleUnion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EleUnion result = eleUnionRepository.save(eleUnion);
        return ResponseData.ok(result);
    }

    /**
     * {@code GET  /ele-unions} : get all the eleUnions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of eleUnions in body.
     */
    @GetMapping("/ele-unions")
    public ResponseEntity<ResponseData<List<EleUnion>>> getAllEleUnions() {
        log.debug("REST request to get all EleUnions");
        final List<EleUnion> all = eleUnionRepository.findAll();
        return ResponseData.ok(all);
    }

    /**
     * @data: 2022/6/20-下午10:32
     * @User: zhaozhiwei
     * @method: getEleUnionsLeftTree
     * @return: java.util.List<com.example.domain.EleUnion>
     * @Description: 获取基础信息左侧要素列表
     */
    @GetMapping("/ele-unions/left-tree")
    public ResponseEntity<ResponseData<Object>> getEleUnionsLeftTree() {
        log.debug("REST request to get all getEleUnionsLeftTree");

        //1. 获取所有ele_的信息
        final List<Map<String, Object>> allEleCategory = commonEleService.findAllEleCategory();
        //2. 构建成树信息
        for (Map<String, Object> map : allEleCategory) {
            map.put("id", map.get("eleCatCode"));
            map.put("label", map.get("eleCatCode") + "-" + map.get("eleCatName"));
        }
        return ResponseData.ok(allEleCategory);
    }

    /**
     * @param eleUnionList :
     * @data: 2022/6/21-下午9:32
     * @User: zhaozhiwei
     * @method: saveOrUpdate
     * @return: org.springframework.http.ResponseEntity<com.example.domain.EleUnion>
     * @Description: 保存提交的数据
     */
    @PostMapping("/ele-unions/save/update")
    public ResponseEntity<ResponseData<List<EleUnion>>> saveOrUpdate(@RequestBody List<EleUnion> eleUnionList) throws URISyntaxException {
        log.debug("REST request to save EleUnionList : {}", eleUnionList);
        //1. 清理掉已经删除的数据, id不在范围内的就是
        final String eleCatCode = eleUnionList.get(0).getEleCatCode();
        final List<EleUnion> byEleCatCode = eleUnionRepository.findByEleCatCode(eleCatCode);
        final List<Long> idList = eleUnionList.stream().map(m -> m.getId()).collect(Collectors.toList());
        final List<Long> deleteIds = new ArrayList<>();
        for (EleUnion eleUnion : byEleCatCode) {
            if (!idList.contains(eleUnion.getId())) {
                deleteIds.add(eleUnion.getId());
            }
        }
        eleUnionRepository.deleteAllById(deleteIds);
        eleUnionRepository.saveAll(eleUnionList);
        return ResponseData.ok(eleUnionList);
    }

    /**
     * @data: 2022/6/20-下午11:01
     * @User: zhaozhiwei
     * @method: getElementInfoByNodeId
     * @return: java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @Description: 根据选中的左侧节点id查询基础信息
     */
    @GetMapping("/ele-unions/element-info/{id}")
    public ResponseEntity<ResponseData<List<EleUnion>>> getElementInfoByNodeId(@PathVariable String id) {
        log.debug("REST request to get all getElementInfoByNodeId");
        final List<EleUnion> elementInfoByEleCatCode = commonEleService.findElementInfoByEleCatCode(id);
        return ResponseData.ok(elementInfoByEleCatCode);
    }

    /**
     * {@code GET  /ele-unions/:id} : get the "id" eleUnion.
     *
     * @param id the id of the eleUnion to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eleUnion, or with status
     * {@code 404 (Not Found)}.
     */
    @GetMapping("/ele-unions/{id}")
    public ResponseEntity<ResponseData<Optional<EleUnion>>> getEleUnion(@PathVariable Long id) {
        log.debug("REST request to get EleUnion : {}", id);
        Optional<EleUnion> eleUnion = eleUnionRepository.findById(id);
        if (eleUnion.isPresent()) {
            return ResponseData.ok(eleUnion);
        } else {
            throw new RuntimeException("找不到该要素信息, id: " + id);
        }
    }

    /**
     * {@code DELETE  /ele-unions/:id} : delete the "id" eleUnion.
     *
     * @param id the id of the eleUnion to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ele-unions/{id}")
    public ResponseEntity<ResponseData<Object>> deleteEleUnion(@PathVariable Long id) {
        log.debug("REST request to delete EleUnion : {}", id);
        eleUnionRepository.deleteById(id);
        return ResponseData.ok();
    }
}
