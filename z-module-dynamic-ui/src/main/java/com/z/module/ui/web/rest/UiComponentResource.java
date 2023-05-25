package com.z.module.ui.web.rest;

import com.z.framework.common.web.rest.errors.BadRequestAlertException;
import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.module.ui.domain.UiComponent;
import com.z.module.ui.repository.UiComponentRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
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

@RestController
@Transactional
@Slf4j
public class UiComponentResource {

    private static final String ENTITY_NAME = "uiComponent";

    private final UiComponentRepository uiComponentRepository;

    public UiComponentResource(UiComponentRepository uiComponentRepository) {
        this.uiComponentRepository = uiComponentRepository;
    }

    /**
     * {@code POST  /ui-components} : Create a new uiComponent.
     *
     * @param uiComponent the uiComponent to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new uiComponent, or
     * with status {@code 400 (Bad Request)} if the uiComponent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ui-components")
    public ResponseEntity<ResponseData<UiComponent>> createUiComponent(@RequestBody UiComponent uiComponent) throws URISyntaxException {
        log.debug("REST request to save UiComponent : {}", uiComponent);
        UiComponent result = uiComponentRepository.save(uiComponent);
        return ResponseData.ok(result);
    }

    /**
     * {@code PUT  /ui-components/:id} : Updates an existing uiComponent.
     *
     * @param id          the id of the uiComponent to save.
     * @param uiComponent the uiComponent to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uiComponent,
     * or with status {@code 400 (Bad Request)} if the uiComponent is not valid,
     * or with status {@code 500 (Internal Server Error)} if the uiComponent couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ui-components/{id}")
    public ResponseEntity<ResponseData<UiComponent>> updateUiComponent(
            @PathVariable(value = "id", required = false) final Long id,
            @RequestBody UiComponent uiComponent
    ) throws URISyntaxException {
        log.debug("REST request to update UiComponent : {}, {}", id, uiComponent);
        if (uiComponent.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uiComponent.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uiComponentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UiComponent result = uiComponentRepository.save(uiComponent);
        return ResponseData.ok(result);
    }

    /**
     * {@code GET  /ui-components} : get all the uiComponents.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of uiComponents in body.
     */
    @GetMapping("/ui-components")
    public ResponseEntity<ResponseData<HashMap<String, Object>>> getUiComponents(Pageable pageable) {
        log.debug("REST request to get a page of UiComponents");

        Page<UiComponent> page = uiComponentRepository.findAll(pageable);
        return ResponseData.ok(new HashMap<String, Object>() {{
            put("list", page.getContent());
            put("total", Long.valueOf(page.getTotalElements()).intValue());
        }});
    }

    /**
     * {@code GET  /ui-components/:id} : get the "id" uiComponent.
     *
     * @param id the id of the uiComponent to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the uiComponent, or with status
     * {@code 404 (Not Found)}.
     */
    @GetMapping("/ui-components/{id}")
    public ResponseEntity<ResponseData<UiComponent>> getUiComponent(@PathVariable Long id) {
        log.debug("REST request to get UiComponent : {}", id);
        Optional<UiComponent> uiComponent = uiComponentRepository.findById(id);
        return ResponseData.ok(uiComponent.get());
    }

    @GetMapping("/ui-components/menu/{menuid}")
    public ResponseEntity<ResponseData<List<UiComponent>>> getUiComponentByMenuId(@PathVariable Long menuid) {
        log.debug("REST request to get UiComponent by menuid : {}", menuid);
        final List<UiComponent> byMenuIdOrderByOrderNumAsc =
                uiComponentRepository.findByMenuIdOrderByOrderNumAsc(menuid);
        return ResponseData.ok(byMenuIdOrderByOrderNumAsc);
    }

    @Operation(description = "删除编辑区配置")
    @DeleteMapping("/ui-components/{id}")
    public ResponseEntity<ResponseData<String>> deleteUiComponent(@RequestBody List<Long> idList) {
        log.debug("REST request to delete Examples, ids: {}", idList);
        this.uiComponentRepository.deleteAllByIdIn(idList);
        return ResponseData.ok("success");
    }
}
