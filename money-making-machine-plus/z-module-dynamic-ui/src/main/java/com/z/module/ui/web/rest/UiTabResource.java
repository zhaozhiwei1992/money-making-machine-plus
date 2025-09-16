package com.z.module.ui.web.rest;

import com.z.framework.common.web.rest.errors.BadRequestAlertException;
import com.z.module.ui.domain.UiTab;
import com.z.module.ui.repository.UiTabRepository;
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
@Transactional
@RequestMapping("/ui")
@Slf4j
public class UiTabResource {

    private static final String ENTITY_NAME = "uiTab";

    private final UiTabRepository uiTabRepository;

    public UiTabResource(UiTabRepository uiTabRepository) {
        this.uiTabRepository = uiTabRepository;
    }

    /**
     * {@code POST  /tabs} : Create a new uiTab.
     *
     * @param uiTab the uiTab to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new uiTab, or with
     * status {@code 400 (Bad Request)} if the uiTab has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tabs")
    public UiTab createUiTab(@RequestBody UiTab uiTab) throws URISyntaxException {
        log.debug("REST request to save UiTab : {}", uiTab);
        if (uiTab.getId() != null) {
            throw new BadRequestAlertException("A new uiTab cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ExampleMatcher matcher = ExampleMatcher.matching();
        final UiTab filterObj = new UiTab();
        filterObj.setMenuId(uiTab.getMenuId());
        final Example<UiTab> of = Example.of(filterObj, matcher);
        final long count = uiTabRepository.count(of);
        uiTab.setOrderNum(Integer.parseInt(String.valueOf(count + 1)));

        return uiTabRepository.save(uiTab);
    }

    /**
     * {@code GET  /tabs} : get all the uiTabs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of uiTabs in body.
     */
    @GetMapping("/tabs")
    public HashMap<String, Object> getAllUiTabs(Pageable pageable) {
        log.debug("REST request to get a page of UiTabs");

        Page<UiTab> page = uiTabRepository.findAll(pageable);
        return new HashMap<String, Object>() {{
            put("list", page.getContent());
            put("total", Long.valueOf(page.getTotalElements()).intValue());
        }};
    }

    @GetMapping("/tabs/menu/{menuid}")
    public Object getUiTabByMenuId(@PathVariable Long menuid) {
        log.debug("REST request to get UiToolButton by menu : {}", menuid);
        final List<UiTab> byMenuId = uiTabRepository.findByMenuId(menuid);
        return byMenuId;
    }

    /**
     * {@code DELETE  /tabs/:id} : delete the "id" uiTab.
     *
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tabs/{id}")
    public String deleteUiTab(@RequestBody List<Long> idList) {
        this.uiTabRepository.deleteAllByIdIn(idList);
        return "success";
    }
}
