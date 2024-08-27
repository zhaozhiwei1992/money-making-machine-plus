package com.z.module.ui.web.rest;

import com.z.framework.common.web.rest.errors.BadRequestAlertException;
import com.z.module.ui.domain.UiQueryForm;
import com.z.module.ui.repository.UiQueryFormRepository;
import lombok.extern.slf4j.Slf4j;
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

@RestController
@RequestMapping("/ui")
@Transactional
@Slf4j
public class UiQueryFormResource {

    private static final String ENTITY_NAME = "uiQueryForm";

    private final UiQueryFormRepository uiQueryFormRepository;

    public UiQueryFormResource(UiQueryFormRepository uiQueryFormRepository) {
        this.uiQueryFormRepository = uiQueryFormRepository;
    }

    /**
     * {@code POST  /queryforms} : Create a new uiQueryForm.
     *
     * @param uiQueryForm the uiQueryForm to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new uiQueryForm, or with status {@code 400 (Bad Request)} if the uiQueryForm has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/queryforms")
    public UiQueryForm createUiQueryForm(@RequestBody UiQueryForm uiQueryForm) throws URISyntaxException {
        log.debug("REST request to save UiQueryForm : {}", uiQueryForm);
        if (uiQueryForm.getId() != null) {
            throw new BadRequestAlertException("A new uiQueryForm cannot already have an ID", ENTITY_NAME, "idexists");
        }

        ExampleMatcher matcher = ExampleMatcher.matching();
        final UiQueryForm filterObj = new UiQueryForm();
        filterObj.setMenuId(uiQueryForm.getMenuId());
        final Example<UiQueryForm> of = Example.of(filterObj, matcher);
        final long count = uiQueryFormRepository.count(of);
        uiQueryForm.setOrderNum(Integer.parseInt(String.valueOf(count + 1)));

        UiQueryForm result = uiQueryFormRepository.save(uiQueryForm);
        return result;
    }

    /**
     * {@code GET  /queryforms} : get all the uiQueryForms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of uiQueryForms in body.
     */
    @GetMapping("/queryforms")
    public HashMap<String, Object> getAllUiQueryForms(Pageable pageable) {
        log.debug("REST request to get all UiQueryForms");
        final Page<UiQueryForm> page = uiQueryFormRepository.findAll(pageable);
        return new HashMap<String, Object>(){{
            put("list", page.getContent());
            put("total", Long.valueOf(page.getTotalElements()).intValue());
        }};
    }

    @GetMapping("/queryforms/menu/{menuid}")
    public List<UiQueryForm> getUiQueryFormByMenuId(@PathVariable Long menuid) {
        log.debug("REST request to get UiQueryForm by menu : {}", menuid);
        final List<UiQueryForm> byMenuIdOrderByOrderNumAsc = uiQueryFormRepository.findByMenuIdOrderByOrderNumAsc(menuid);
        return byMenuIdOrderByOrderNumAsc;
    }

    /**
     * {@code DELETE  /queryforms/:id} : delete the "id" uiQueryForm.
     *
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/queryforms")
    public String deleteUiQueryForm(@RequestBody List<Long> idList) {
        this.uiQueryFormRepository.deleteAllByIdIn(idList);
        return "success";
    }
}
