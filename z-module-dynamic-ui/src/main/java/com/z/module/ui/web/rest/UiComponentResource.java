package com.z.module.ui.web.rest;

import com.z.framework.common.web.rest.errors.BadRequestAlertException;
import com.z.framework.common.web.util.HeaderUtil;
import com.z.framework.common.web.util.PaginationUtil;
import com.z.framework.common.web.util.ResponseUtil;
import com.z.module.ui.domain.UiComponent;
import com.z.module.ui.repository.UiComponentRepository;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link com.example.domain.UiComponent}.
 */
@RestController
@RequestMapping("/api")
@Transactional
@Slf4j
public class UiComponentResource {

    private static final String ENTITY_NAME = "uiComponent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

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
    public ResponseEntity<UiComponent> createUiComponent(@RequestBody UiComponent uiComponent) throws URISyntaxException {
        log.debug("REST request to save UiComponent : {}", uiComponent);
        if (uiComponent.getId() != null) {
            throw new BadRequestAlertException("A new uiComponent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UiComponent result = uiComponentRepository.save(uiComponent);
        return ResponseEntity
            .created(new URI("/api/ui-components/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
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
    public ResponseEntity<UiComponent> updateUiComponent(
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
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, uiComponent.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /ui-components/:id} : Partial updates given fields of an existing uiComponent, field will ignore
     * if it is null
     *
     * @param id          the id of the uiComponent to save.
     * @param uiComponent the uiComponent to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uiComponent,
     * or with status {@code 400 (Bad Request)} if the uiComponent is not valid,
     * or with status {@code 404 (Not Found)} if the uiComponent is not found,
     * or with status {@code 500 (Internal Server Error)} if the uiComponent couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ui-components/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UiComponent> partialUpdateUiComponent(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UiComponent uiComponent
    ) throws URISyntaxException {
        log.debug("REST request to partial update UiComponent partially : {}, {}", id, uiComponent);
        if (uiComponent.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uiComponent.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uiComponentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UiComponent> result = uiComponentRepository
            .findById(uiComponent.getId())
            .map(existingUiComponent -> {
                if (uiComponent.getMenuid() != null) {
                    existingUiComponent.setMenuid(uiComponent.getMenuid());
                }
                if (uiComponent.getOrdernum() != null) {
                    existingUiComponent.setOrdernum(uiComponent.getOrdernum());
                }
                if (uiComponent.getComponentid() != null) {
                    existingUiComponent.setComponentid(uiComponent.getComponentid());
                }
                if (uiComponent.getConfig() != null) {
                    existingUiComponent.setConfig(uiComponent.getConfig());
                }

                return existingUiComponent;
            })
            .map(uiComponentRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, uiComponent.getId().toString())
        );
    }

    /**
     * {@code GET  /ui-components} : get all the uiComponents.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of uiComponents in body.
     */
    //    @GetMapping("/ui-components")
    //    public List<UiComponent> getAllUiComponents() {
    //        log.debug("REST request to get all UiComponents");
    //        return uiComponentRepository.findAll();
    //    }

    /**
     * {@code GET  /ui-components} : get all the uiComponents.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of uiComponents in body.
     */
    @GetMapping("/ui-components")
    public ResponseEntity<List<UiComponent>> getAllUiComponents(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false) Long menuid
    ) {
        log.debug("REST request to get a page of UiComponents");

        // 菜单精确匹配
        ExampleMatcher matcher = ExampleMatcher.matching();
        final UiComponent uiComponent = new UiComponent();
        uiComponent.setMenuid(menuid);
        final Example<UiComponent> of = Example.of(uiComponent, matcher);

        Page<UiComponent> page = uiComponentRepository.findAll(of, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ui-components/:id} : get the "id" uiComponent.
     *
     * @param id the id of the uiComponent to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the uiComponent, or with status
     * {@code 404 (Not Found)}.
     */
    @GetMapping("/ui-components/{id}")
    public ResponseEntity<UiComponent> getUiComponent(@PathVariable Long id) {
        log.debug("REST request to get UiComponent : {}", id);
        Optional<UiComponent> uiComponent = uiComponentRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(uiComponent);
    }

    @GetMapping("/ui-components/menu/{menuid}")
    public List<UiComponent> getUiComponentByMenuId(@PathVariable Long menuid) {
        log.debug("REST request to get UiComponent by menuid : {}", menuid);
        return uiComponentRepository.findByMenuidOrderByOrdernumAsc(menuid);
    }

    /**
     * {@code DELETE  /ui-components/:id} : delete the "id" uiComponent.
     *
     * @param id the id of the uiComponent to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ui-components/{id}")
    public ResponseEntity<Void> deleteUiComponent(@PathVariable Long id) {
        log.debug("REST request to delete UiComponent : {}", id);
        uiComponentRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
