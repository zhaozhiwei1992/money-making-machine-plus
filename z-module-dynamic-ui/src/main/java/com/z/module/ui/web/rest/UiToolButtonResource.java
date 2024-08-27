package com.z.module.ui.web.rest;

import com.z.module.ui.domain.UiToolButton;
import com.z.module.ui.repository.UiToolButtonRepository;
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
public class UiToolButtonResource {

    private final UiToolButtonRepository uiToolButtonRepository;

    public UiToolButtonResource(UiToolButtonRepository uiToolButtonRepository) {
        this.uiToolButtonRepository = uiToolButtonRepository;
    }

    /**
     * {@code POST  /tool-buttons} : Create a new uiToolButton.
     *
     * @param uiToolButton the uiToolButton to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new uiToolButton, or
     * with status {@code 400 (Bad Request)} if the uiToolButton has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tool-buttons")
    public UiToolButton createUiToolButton(@RequestBody UiToolButton uiToolButton) throws URISyntaxException {
        log.debug("REST request to save UiToolButton : {}", uiToolButton);
        ExampleMatcher matcher = ExampleMatcher.matching();
        final UiToolButton filterObj = new UiToolButton();
        filterObj.setMenuId(uiToolButton.getMenuId());
        final Example<UiToolButton> of = Example.of(filterObj, matcher);
        final long count = uiToolButtonRepository.count(of);
        uiToolButton.setOrderNum(Integer.parseInt(String.valueOf(count + 1)));

        return uiToolButtonRepository.save(uiToolButton);
    }

    /**
     * {@code GET  /tool-buttons} : get all the uiToolButtons.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of uiToolButtons in body.
     */

    @GetMapping("/tool-buttons")
    public HashMap<String, Object> getAllUiToolButtons(Pageable pageable) {
        log.debug("REST request to get a page of UiComponents");

        Page<UiToolButton> page = uiToolButtonRepository.findAll(pageable);
        return new HashMap<String, Object>() {{
            put("list", page.getContent());
            put("total", Long.valueOf(page.getTotalElements()).intValue());
        }};
    }

    @GetMapping("/tool-buttons/menu/{menuId}")
    public List<UiToolButton> getUiToolButtonByMenuId(@PathVariable Long menuId) {
        return uiToolButtonRepository.findByMenuIdOrderByOrderNumAsc(menuId);
    }

    /**
     * {@code DELETE  /tool-buttons/:id} : delete the "id" uiToolButton.
     *
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tool-buttons")
    public String deleteUiToolButton(@RequestBody List<Long> idList) {
        this.uiToolButtonRepository.deleteAllByIdIn(idList);
        return "success";
    }
}
