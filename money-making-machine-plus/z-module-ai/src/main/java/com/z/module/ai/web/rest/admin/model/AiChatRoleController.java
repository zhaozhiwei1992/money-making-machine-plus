package com.z.module.ai.web.rest.admin.model;

import cn.hutool.core.util.ObjUtil;
import com.z.framework.common.util.object.BeanUtils;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.ai.domain.model.AiChatRoleDO;
import com.z.module.ai.service.model.AiChatRoleService;
import com.z.module.ai.web.rest.admin.model.vo.chatRole.AiChatRolePageReqVO;
import com.z.module.ai.web.rest.admin.model.vo.chatRole.AiChatRoleRespVO;
import com.z.module.ai.web.rest.admin.model.vo.chatRole.AiChatRoleSaveMyReqVO;
import com.z.module.ai.web.rest.admin.model.vo.chatRole.AiChatRoleSaveReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.z.framework.security.util.SecurityUtils.getUserId;


@Tag(name = "管理后台 - AI 聊天角色")
@RestController
@RequestMapping("/ai/chat-role")
@Validated
public class AiChatRoleController {

    @Resource
    private AiChatRoleService chatRoleService;

    @GetMapping("/my-page")
    @Operation(summary = "获得【我的】聊天角色分页")
    public PageResult<AiChatRoleRespVO> getChatRoleMyPage(Pageable pageable, @Valid AiChatRoleDO pageReqVO) {
        PageResult<AiChatRoleDO> pageResult = chatRoleService.getChatRoleMyPage(pageable, pageReqVO, getUserId());
        return BeanUtils.toBean(pageResult, AiChatRoleRespVO.class);
    }

    @GetMapping("/get-my")
    @Operation(summary = "获得【我的】聊天角色")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public AiChatRoleRespVO getChatRoleMy(@RequestParam("id") Long id) {
        AiChatRoleDO chatRole = chatRoleService.getChatRole(id);
        if (ObjUtil.notEqual(chatRole.getUserId(), getUserId())) {
            return null;
        }
        return BeanUtils.toBean(chatRole, AiChatRoleRespVO.class);
    }

    @PostMapping("/create-my")
    @Operation(summary = "创建【我的】聊天角色")
    public Long createChatRoleMy(@Valid @RequestBody AiChatRoleSaveMyReqVO createReqVO) {
        return chatRoleService.createChatRoleMy(createReqVO, getUserId());
    }

    @PutMapping("/update-my")
    @Operation(summary = "更新【我的】聊天角色")
    public Boolean updateChatRoleMy(@Valid @RequestBody AiChatRoleSaveMyReqVO updateReqVO) {
        chatRoleService.updateChatRoleMy(updateReqVO, getUserId());
        return true;
    }

    @DeleteMapping("/delete-my")
    @Operation(summary = "删除【我的】聊天角色")
    @Parameter(name = "id", description = "编号", required = true)
    public Boolean deleteChatRoleMy(@RequestParam("id") Long id) {
        chatRoleService.deleteChatRoleMy(id, getUserId());
        return true;
    }

    @GetMapping("/category-list")
    @Operation(summary = "获得聊天角色的分类列表")
    public List<String> getChatRoleCategoryList() {
        return chatRoleService.getChatRoleCategoryList();
    }

    // ========== 角色管理 ==========

    @PostMapping("/create")
    @Operation(summary = "创建聊天角色")
    public Long createChatRole(@Valid @RequestBody AiChatRoleSaveReqVO createReqVO) {
        return chatRoleService.createChatRole(createReqVO);
    }

    @PutMapping("/update")
    @Operation(summary = "更新聊天角色")
    public Boolean updateChatRole(@Valid @RequestBody AiChatRoleSaveReqVO updateReqVO) {
        chatRoleService.updateChatRole(updateReqVO);
        return true;
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除聊天角色")
    @Parameter(name = "id", description = "编号", required = true)
    public Boolean deleteChatRole(@RequestParam("id") Long id) {
        chatRoleService.deleteChatRole(id);
        return true;
    }

    @GetMapping("/get")
    @Operation(summary = "获得聊天角色")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public AiChatRoleRespVO getChatRole(@RequestParam("id") Long id) {
        AiChatRoleDO chatRole = chatRoleService.getChatRole(id);
        return BeanUtils.toBean(chatRole, AiChatRoleRespVO.class);
    }

    @GetMapping("/page")
    @Operation(summary = "获得聊天角色分页")
    public PageResult<AiChatRoleRespVO> getChatRolePage(Pageable pageable, @Valid AiChatRoleDO aiChatRoleDO) {
        PageResult<AiChatRoleDO> pageResult = chatRoleService.getChatRolePage(pageable, aiChatRoleDO);
        return BeanUtils.toBean(pageResult, AiChatRoleRespVO.class);
    }

}
