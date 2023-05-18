package com.z.module.ui.web.rest;

import com.example.domain.UiTab;
import com.example.repository.UiTabRepository;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link com.example.domain.UiTab}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UiTabResource {

    private final Logger log = LoggerFactory.getLogger(UiTabResource.class);

    private static final String ENTITY_NAME = "uiTab";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UiTabRepository uiTabRepository;

    public UiTabResource(UiTabRepository uiTabRepository) {
        this.uiTabRepository = uiTabRepository;
    }

    /**
     * {@code POST  /ui-tabs} : Create a new uiTab.
     *
     * @param uiTab the uiTab to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new uiTab, or with status {@code 400 (Bad Request)} if the uiTab has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ui-tabs")
    public ResponseEntity<UiTab> createUiTab(@RequestBody UiTab uiTab) throws URISyntaxException {
        log.debug("REST request to save UiTab : {}", uiTab);
        if (uiTab.getId() != null) {
            throw new BadRequestAlertException("A new uiTab cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ExampleMatcher matcher = ExampleMatcher.matching();
        final UiTab filterObj = new UiTab();
        filterObj.setMenuid(uiTab.getMenuid());
        final Example<UiTab> of = Example.of(filterObj, matcher);
        final long count = uiTabRepository.count(of);
        uiTab.setOrdernum(Integer.parseInt(String.valueOf(count + 1)));

        UiTab result = uiTabRepository.save(uiTab);
        return ResponseEntity
            .created(new URI("/api/ui-tabs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ui-tabs/:id} : Updates an existing uiTab.
     *
     * @param id the id of the uiTab to save.
     * @param uiTab the uiTab to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uiTab,
     * or with status {@code 400 (Bad Request)} if the uiTab is not valid,
     * or with status {@code 500 (Internal Server Error)} if the uiTab couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ui-tabs/{id}")
    public ResponseEntity<UiTab> updateUiTab(@PathVariable(value = "id", required = false) final Long id, @RequestBody UiTab uiTab)
        throws URISyntaxException {
        log.debug("REST request to update UiTab : {}, {}", id, uiTab);
        if (uiTab.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uiTab.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uiTabRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UiTab result = uiTabRepository.save(uiTab);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, uiTab.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /ui-tabs/:id} : Partial updates given fields of an existing uiTab, field will ignore if it is null
     *
     * @param id the id of the uiTab to save.
     * @param uiTab the uiTab to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uiTab,
     * or with status {@code 400 (Bad Request)} if the uiTab is not valid,
     * or with status {@code 404 (Not Found)} if the uiTab is not found,
     * or with status {@code 500 (Internal Server Error)} if the uiTab couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ui-tabs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UiTab> partialUpdateUiTab(@PathVariable(value = "id", required = false) final Long id, @RequestBody UiTab uiTab)
        throws URISyntaxException {
        log.debug("REST request to partial update UiTab partially : {}, {}", id, uiTab);
        if (uiTab.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uiTab.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uiTabRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UiTab> result = uiTabRepository
            .findById(uiTab.getId())
            .map(existingUiTab -> {
                if (uiTab.getMenuid() != null) {
                    existingUiTab.setMenuid(uiTab.getMenuid());
                }
                if (uiTab.getCode() != null) {
                    existingUiTab.setCode(uiTab.getCode());
                }
                if (uiTab.getName() != null) {
                    existingUiTab.setName(uiTab.getName());
                }
                if (uiTab.getOrdernum() != null) {
                    existingUiTab.setOrdernum(uiTab.getOrdernum());
                }
                if (uiTab.getConfig() != null) {
                    existingUiTab.setConfig(uiTab.getConfig());
                }

                return existingUiTab;
            })
            .map(uiTabRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, uiTab.getId().toString())
        );
    }

    /**
     * {@code GET  /ui-tabs} : get all the uiTabs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of uiTabs in body.
     */
    @GetMapping("/ui-tabs")
    public ResponseEntity<List<UiTab>> getAllUiTabs(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false) Long menuid
    ) {
        log.debug("REST request to get a page of UiTabs");

        // 根据菜单精确匹配
        ExampleMatcher matcher = ExampleMatcher.matching();
        final UiTab uiEditform = new UiTab();
        uiEditform.setMenuid(menuid);
        final Example<UiTab> of = Example.of(uiEditform, matcher);

        Page<UiTab> page = uiTabRepository.findAll(of, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ui-tabs/:id} : get the "id" uiTab.
     *
     * @param id the id of the uiTab to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the uiTab, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ui-tabs/{id}")
    public ResponseEntity<UiTab> getUiTab(@PathVariable Long id) {
        log.debug("REST request to get UiTab : {}", id);
        Optional<UiTab> uiTab = uiTabRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(uiTab);
    }

    @GetMapping("/ui-tabs/menu/{menuid}")
    public List<UiTab> getUiTabByMenuid(@PathVariable Long menuid) {
        log.debug("REST request to get UiToolButton by menu : {}", menuid);
        return uiTabRepository.findByMenuid(menuid);
    }

    /**
     * {@code DELETE  /ui-tabs/:id} : delete the "id" uiTab.
     *
     * @param id the id of the uiTab to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ui-tabs/{id}")
    public ResponseEntity<Void> deleteUiTab(@PathVariable Long id) {
        log.debug("REST request to delete UiTab : {}", id);
        uiTabRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
