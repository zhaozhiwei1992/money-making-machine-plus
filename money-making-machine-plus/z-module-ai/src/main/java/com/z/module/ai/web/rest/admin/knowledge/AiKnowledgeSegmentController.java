package com.z.module.ai.web.rest.admin.knowledge;

import cn.hutool.core.collection.CollUtil;
import com.z.framework.common.util.collection.MapUtils;
import com.z.framework.common.util.object.BeanUtils;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.ai.domain.knowledge.AiKnowledgeDocumentDO;
import com.z.module.ai.domain.knowledge.AiKnowledgeSegmentDO;
import com.z.module.ai.service.knowledge.AiKnowledgeDocumentService;
import com.z.module.ai.service.knowledge.AiKnowledgeSegmentService;
import com.z.module.ai.service.knowledge.bo.AiKnowledgeSegmentSearchReqBO;
import com.z.module.ai.service.knowledge.bo.AiKnowledgeSegmentSearchRespBO;
import com.z.module.ai.web.rest.admin.knowledge.vo.segment.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.z.framework.common.util.collection.CollectionUtils.convertSet;


@Tag(name = "管理后台 - AI 知识库段落")
@RestController
@RequestMapping("/ai/knowledge/segment")
@Validated
public class AiKnowledgeSegmentController {

    @Resource
    private AiKnowledgeSegmentService segmentService;
    @Resource
    private AiKnowledgeDocumentService documentService;

    @GetMapping("/get")
    @Operation(summary = "获取段落详情")
    @Parameter(name = "id", description = "段落编号", required = true, example = "1024")
    public AiKnowledgeSegmentRespVO getKnowledgeSegment(@RequestParam("id") Long id) {
        AiKnowledgeSegmentDO segment = segmentService.getKnowledgeSegment(id);
        return BeanUtils.toBean(segment, AiKnowledgeSegmentRespVO.class);
    }

    @GetMapping("/page")
    @Operation(summary = "获取段落分页")
    public PageResult<AiKnowledgeSegmentRespVO> getKnowledgeSegmentPage(
            Pageable pageable, @Valid AiKnowledgeSegmentDO aiKnowledgeSegmentDO) {
        PageResult<AiKnowledgeSegmentDO> pageResult = segmentService.getKnowledgeSegmentPage(pageable, aiKnowledgeSegmentDO);
        return BeanUtils.toBean(pageResult, AiKnowledgeSegmentRespVO.class);
    }

    @PostMapping("/create")
    @Operation(summary = "创建段落")
    public Long createKnowledgeSegment(@Valid @RequestBody AiKnowledgeSegmentSaveReqVO createReqVO) {
        return segmentService.createKnowledgeSegment(createReqVO);
    }

    @PutMapping("/update")
    @Operation(summary = "更新段落内容")
    public Boolean updateKnowledgeSegment(@Valid @RequestBody AiKnowledgeSegmentSaveReqVO reqVO) {
        segmentService.updateKnowledgeSegment(reqVO);
        return true;
    }

    @PutMapping("/update-status")
    @Operation(summary = "启禁用段落内容")
    public Boolean updateKnowledgeSegmentStatus(
            @Valid @RequestBody AiKnowledgeSegmentUpdateStatusReqVO reqVO) {
        segmentService.updateKnowledgeSegmentStatus(reqVO);
        return true;
    }

    @GetMapping("/split")
    @Operation(summary = "切片内容")
    @Parameters({
            @Parameter(name = "url", description = "文档 URL", required = true),
            @Parameter(name = "segmentMaxTokens", description = "分段的最大 Token 数", required = true)
    })
    public List<AiKnowledgeSegmentRespVO> splitContent(
            @RequestParam("url") String url,
            @RequestParam(value = "segmentMaxTokens") Integer segmentMaxTokens) {
        List<AiKnowledgeSegmentDO> segments = segmentService.splitContent(url, segmentMaxTokens);
        return BeanUtils.toBean(segments, AiKnowledgeSegmentRespVO.class);
    }

    @GetMapping("/get-process-list")
    @Operation(summary = "获取文档处理列表")
    @Parameter(name = "documentIds", description = "文档编号列表", required = true, example = "1,2,3")
    public List<AiKnowledgeSegmentProcessRespVO> getKnowledgeSegmentProcessList(
            @RequestParam("documentIds") List<Long> documentIds) {
        List<AiKnowledgeSegmentProcessRespVO> list = segmentService.getKnowledgeSegmentProcessList(documentIds);
        return list;
    }

    @GetMapping("/search")
    @Operation(summary = "搜索段落内容")
    public List<AiKnowledgeSegmentSearchRespVO> searchKnowledgeSegment(
            @Valid AiKnowledgeSegmentSearchReqVO reqVO) {
        // 1. 搜索段落
        List<AiKnowledgeSegmentSearchRespBO> segments = segmentService
                .searchKnowledgeSegment(BeanUtils.toBean(reqVO, AiKnowledgeSegmentSearchReqBO.class));
        if (CollUtil.isEmpty(segments)) {
            return Collections.emptyList();
        }

        // 2. 拼接 VO
//        Map<Long, AiKnowledgeDocumentDO> documentMap = documentService.getKnowledgeDocumentMap(convertSet(
//                segments, AiKnowledgeSegmentSearchRespBO::getDocumentId));
//        return BeanUtils.toBean(segments, AiKnowledgeSegmentSearchRespVO.class,
//                segment -> MapUtils.findAndThen(documentMap, segment.getDocumentId(),
//                        document -> segment.setDocumentName(document.getName())));
        return null;
    }

}
