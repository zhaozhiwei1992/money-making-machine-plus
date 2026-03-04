package com.z.module.ai.web.rest.admin.knowledge;

import com.z.framework.common.util.object.BeanUtils;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.ai.domain.knowledge.AiKnowledgeDocumentDO;
import com.z.module.ai.service.knowledge.AiKnowledgeDocumentService;
import com.z.module.ai.web.rest.admin.knowledge.vo.document.*;
import com.z.module.ai.web.rest.admin.knowledge.vo.knowledge.AiKnowledgeDocumentCreateReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "管理后台 - AI 知识库文档")
@RestController
@RequestMapping("/ai/knowledge/document")
@Validated
public class AiKnowledgeDocumentController {

    @Resource
    private AiKnowledgeDocumentService documentService;

    @GetMapping("/page")
    @Operation(summary = "获取文档分页")
    public PageResult<AiKnowledgeDocumentRespVO> getKnowledgeDocumentPage(
            Pageable pageable, @Valid AiKnowledgeDocumentDO pageReqVO) {
        PageResult<AiKnowledgeDocumentDO> pageResult = documentService.getKnowledgeDocumentPage(pageable, pageReqVO);
        return BeanUtils.toBean(pageResult, AiKnowledgeDocumentRespVO.class);
    }

    @GetMapping("/get")
    @Operation(summary = "获取文档详情")
    public AiKnowledgeDocumentRespVO getKnowledgeDocument(@RequestParam("id") Long id) {
        AiKnowledgeDocumentDO document = documentService.getKnowledgeDocument(id);
        return BeanUtils.toBean(document, AiKnowledgeDocumentRespVO.class);
    }

    @PostMapping("/create")
    @Operation(summary = "新建文档（单个）")
    public Long createKnowledgeDocument(@RequestBody @Valid AiKnowledgeDocumentCreateReqVO reqVO) {
        Long id = documentService.createKnowledgeDocument(reqVO);
        return id;
    }

    @PostMapping("/create-list")
    @Operation(summary = "新建文档（多个）")
    public List<Long> createKnowledgeDocumentList(
            @RequestBody @Valid AiKnowledgeDocumentCreateListReqVO reqVO) {
        List<Long> ids = documentService.createKnowledgeDocumentList(reqVO);
        return ids;
    }

    @PutMapping("/update")
    @Operation(summary = "更新文档")
    public Boolean updateKnowledgeDocument(@Valid @RequestBody AiKnowledgeDocumentUpdateReqVO reqVO) {
        documentService.updateKnowledgeDocument(reqVO);
        return true;
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新文档状态")
    public Boolean updateKnowledgeDocumentStatus(
            @Valid @RequestBody AiKnowledgeDocumentUpdateStatusReqVO reqVO) {
        documentService.updateKnowledgeDocumentStatus(reqVO);
        return true;
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除文档")
    public Boolean deleteKnowledgeDocument(@RequestParam("id") Long id) {
        documentService.deleteKnowledgeDocument(id);
        return true;
    }

}
