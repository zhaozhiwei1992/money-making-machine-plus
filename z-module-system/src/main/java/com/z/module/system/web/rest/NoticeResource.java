package com.z.module.system.web.rest;

import com.z.module.system.domain.Notice;
import com.z.module.system.repository.NoticeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

@Tag(name = "通知公告API")
@RestController
@RequestMapping("/system")
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class NoticeResource {

    private final NoticeRepository noticeRepository;

    public NoticeResource(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    /**
     * {@code POST  /api/notices}  : Creates a new role.
     * <p>
     * Creates a new role if the login and email are not already used, and sends an
     * mail with an activation link.
     * The role needs to be activated on creation.
     *
     * @param notice the role to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new role, or with
     * status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(description = "新增通知公告")
    @PostMapping("/notices")
    public Notice createNotice(@RequestBody Notice notice) throws URISyntaxException {
        log.debug("REST request to save Notice : {}", notice);

        Notice newNotice = noticeRepository.save(notice);

        return newNotice;
    }

    /**
     * {@code GET /api/notices} : get all notices with all the details - calling this are only allowed for the
     * administrators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all notices.
     */

    @Operation(description = "获取通知公告")
    @GetMapping("/notices")
    public HashMap<String, Object> getAllNotices(Pageable pageable, Notice role) {
        log.debug("REST request to get all Notice for an admin");

//        final List<Notice> all = roleRepository.findAll();
        // 根据id, 升序
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), sort);

        Page<Notice> noticePage;
        //创建匹配器，即如何使用查询条件
        //构建对象
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                //改变默认字符串匹配方式：模糊查询
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                //改变默认大小写忽略方式：忽略大小写
                .withIgnoreCase(true)
                //名字采用“开始匹配”的方式查询
                .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.startsWith())
                //忽略属性：是否关注。因为是基本类型，需要忽略掉
                .withIgnorePaths("id", "createdDate", "lastModifiedDate");

        //创建实例
        Example<Notice> ex = Example.of(role, matcher);
        noticePage = noticeRepository.findAll(ex, pageable);

        return new HashMap<String, Object>() {{
            put("list", noticePage.getContent());
            put("total", Long.valueOf(noticePage.getTotalElements()).intValue());
        }};
    }

    @Operation(description = "删除通知公告")
    @DeleteMapping("/notices")
    public String deleteNotice(@RequestBody List<Long> idList) {
        log.debug("REST request to delete Examples, ids: {}", idList);
        this.noticeRepository.deleteAllByIdIn(idList);
        return "success";
    }
}
