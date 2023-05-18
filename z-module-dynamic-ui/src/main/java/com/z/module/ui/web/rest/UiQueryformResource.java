package com.z.module.ui.web.rest;

import com.example.domain.UiQueryform;
import com.example.repository.UiQueryformRepository;
import com.example.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link com.example.domain.UiQueryform}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UiQueryformResource {

    private final Logger log = LoggerFactory.getLogger(UiQueryformResource.class);

    private static final String ENTITY_NAME = "uiQueryform";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UiQueryformRepository uiQueryformRepository;

    public UiQueryformResource(UiQueryformRepository uiQueryformRepository) {
        this.uiQueryformRepository = uiQueryformRepository;
    }

    /**
     * {@code POST  /ui-queryforms} : Create a new uiQueryform.
     *
     * @param uiQueryform the uiQueryform to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new uiQueryform, or with status {@code 400 (Bad Request)} if the uiQueryform has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ui-queryforms")
    public ResponseEntity<UiQueryform> createUiQueryform(@RequestBody UiQueryform uiQueryform) throws URISyntaxException {
        log.debug("REST request to save UiQueryform : {}", uiQueryform);
        if (uiQueryform.getId() != null) {
            throw new BadRequestAlertException("A new uiQueryform cannot already have an ID", ENTITY_NAME, "idexists");
        }

        ExampleMatcher matcher = ExampleMatcher.matching();
        final UiQueryform filterObj = new UiQueryform();
        filterObj.setMenuid(uiQueryform.getMenuid());
        final Example<UiQueryform> of = Example.of(filterObj, matcher);
        final long count = uiQueryformRepository.count(of);
        uiQueryform.setOrdernum(Integer.parseInt(String.valueOf(count + 1)));

        UiQueryform result = uiQueryformRepository.save(uiQueryform);
        return ResponseEntity
            .created(new URI("/api/ui-queryforms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ui-queryforms/:id} : Updates an existing uiQueryform.
     *
     * @param id the id of the uiQueryform to save.
     * @param uiQueryform the uiQueryform to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uiQueryform,
     * or with status {@code 400 (Bad Request)} if the uiQueryform is not valid,
     * or with status {@code 500 (Internal Server Error)} if the uiQueryform couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ui-queryforms/{id}")
    public ResponseEntity<UiQueryform> updateUiQueryform(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UiQueryform uiQueryform
    ) throws URISyntaxException {
        log.debug("REST request to update UiQueryform : {}, {}", id, uiQueryform);
        if (uiQueryform.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uiQueryform.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uiQueryformRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UiQueryform result = uiQueryformRepository.save(uiQueryform);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, uiQueryform.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /ui-queryforms/:id} : Partial updates given fields of an existing uiQueryform, field will ignore if it is null
     *
     * @param id the id of the uiQueryform to save.
     * @param uiQueryform the uiQueryform to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uiQueryform,
     * or with status {@code 400 (Bad Request)} if the uiQueryform is not valid,
     * or with status {@code 404 (Not Found)} if the uiQueryform is not found,
     * or with status {@code 500 (Internal Server Error)} if the uiQueryform couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ui-queryforms/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UiQueryform> partialUpdateUiQueryform(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UiQueryform uiQueryform
    ) throws URISyntaxException {
        log.debug("REST request to partial update UiQueryform partially : {}, {}", id, uiQueryform);
        if (uiQueryform.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uiQueryform.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uiQueryformRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UiQueryform> result = uiQueryformRepository
            .findById(uiQueryform.getId())
            .map(existingUiQueryform -> {
                if (uiQueryform.getMenuid() != null) {
                    existingUiQueryform.setMenuid(uiQueryform.getMenuid());
                }
                if (uiQueryform.getCode() != null) {
                    existingUiQueryform.setCode(uiQueryform.getCode());
                }
                if (uiQueryform.getName() != null) {
                    existingUiQueryform.setName(uiQueryform.getName());
                }
                if (uiQueryform.getOrdernum() != null) {
                    existingUiQueryform.setOrdernum(uiQueryform.getOrdernum());
                }
                if (uiQueryform.getIssource() != null) {
                    existingUiQueryform.setIssource(uiQueryform.getIssource());
                }
                if (uiQueryform.getRequirement() != null) {
                    existingUiQueryform.setRequirement(uiQueryform.getRequirement());
                }
                if (uiQueryform.getType() != null) {
                    existingUiQueryform.setType(uiQueryform.getType());
                }
                if (uiQueryform.getPlaceholder() != null) {
                    existingUiQueryform.setPlaceholder(uiQueryform.getPlaceholder());
                }
                if (uiQueryform.getConfig() != null) {
                    existingUiQueryform.setConfig(uiQueryform.getConfig());
                }

                return existingUiQueryform;
            })
            .map(uiQueryformRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, uiQueryform.getId().toString())
        );
    }

    /**
     * {@code GET  /ui-queryforms} : get all the uiQueryforms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of uiQueryforms in body.
     */
    @GetMapping("/ui-queryforms")
    public List<UiQueryform> getAllUiQueryforms() {
        log.debug("REST request to get all UiQueryforms");
        return uiQueryformRepository.findAll();
    }

    /**
     * {@code GET  /ui-queryforms/:id} : get the "id" uiQueryform.
     *
     * @param id the id of the uiQueryform to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the uiQueryform, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ui-queryforms/{id}")
    public ResponseEntity<UiQueryform> getUiQueryform(@PathVariable Long id) {
        log.debug("REST request to get UiQueryform : {}", id);
        Optional<UiQueryform> uiQueryform = uiQueryformRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(uiQueryform);
    }

    @GetMapping("/ui-queryforms/menu/{menuid}")
    public List<UiQueryform> getUiQueryformByMenuid(@PathVariable Long menuid) {
        log.debug("REST request to get UiQueryform by menu : {}", menuid);
        return uiQueryformRepository.findByMenuidOrderByOrdernumAsc(menuid);
    }

    /**
     * {@code DELETE  /ui-queryforms/:id} : delete the "id" uiQueryform.
     *
     * @param id the id of the uiQueryform to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ui-queryforms/{id}")
    public ResponseEntity<Void> deleteUiQueryform(@PathVariable Long id) {
        log.debug("REST request to delete UiQueryform : {}", id);
        uiQueryformRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
