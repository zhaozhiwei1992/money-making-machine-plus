package com.z.module.ai.web.rest.admin.knowledge;

import com.z.framework.common.enums.CommonStatusEnum;
import com.z.framework.common.util.object.BeanUtils;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.ai.domain.knowledge.AiKnowledgeDO;
import com.z.module.ai.service.knowledge.AiKnowledgeService;
import com.z.module.ai.web.rest.admin.knowledge.vo.knowledge.AiKnowledgePageReqVO;
import com.z.module.ai.web.rest.admin.knowledge.vo.knowledge.AiKnowledgeRespVO;
import com.z.module.ai.web.rest.admin.knowledge.vo.knowledge.AiKnowledgeSaveReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.z.framework.common.util.collection.CollectionUtils.convertList;


@Tag(name = "管理后台 - AI 知识库")
@RestController
@RequestMapping("/ai/knowledge")
@Validated
public class AiKnowledgeController {

    @Resource
    private AiKnowledgeService knowledgeService;

    @GetMapping("/page")
    @Operation(summary = "获取知识库分页")
    public PageResult<AiKnowledgeRespVO> getKnowledgePage(Pageable pageable, @Valid AiKnowledgeDO aiKnowledgeDO) {
        PageResult<AiKnowledgeDO> pageResult = knowledgeService.getKnowledgePage(pageable, aiKnowledgeDO);
        return BeanUtils.toBean(pageResult, AiKnowledgeRespVO.class);
    }

    @GetMapping("/get")
    @Operation(summary = "获得知识库")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public AiKnowledgeRespVO getKnowledge(@RequestParam("id") Long id) {
        AiKnowledgeDO knowledge = knowledgeService.getKnowledge(id);
        return BeanUtils.toBean(knowledge, AiKnowledgeRespVO.class);
    }

    @PostMapping("/create")
    @Operation(summary = "创建知识库")
    public Long createKnowledge(@RequestBody @Valid AiKnowledgeSaveReqVO createReqVO) {
        return knowledgeService.createKnowledge(createReqVO);
    }

    @PutMapping("/update")
    @Operation(summary = "更新知识库")
    public Boolean updateKnowledge(@RequestBody @Valid AiKnowledgeSaveReqVO updateReqVO) {
        knowledgeService.updateKnowledge(updateReqVO);
        return true;
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除知识库")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public Boolean deleteKnowledge(@RequestParam("id") Long id) {
        knowledgeService.deleteKnowledge(id);
        return true;
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得知识库的精简列表")
    public List<AiKnowledgeRespVO> getKnowledgeSimpleList() {
        List<AiKnowledgeDO> list = knowledgeService.getKnowledgeSimpleListByStatus(CommonStatusEnum.ENABLE.getStatus());
        return convertList(list, knowledge -> new AiKnowledgeRespVO()
                .setId(knowledge.getId()).setName(knowledge.getName()));
    }

}
