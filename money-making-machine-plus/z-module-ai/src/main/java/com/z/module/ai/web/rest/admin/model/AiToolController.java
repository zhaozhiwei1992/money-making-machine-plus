package com.z.module.ai.web.rest.admin.model;

import com.z.framework.common.enums.CommonStatusEnum;
import com.z.framework.common.util.object.BeanUtils;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.ai.domain.model.AiToolDO;
import com.z.module.ai.service.model.AiToolService;
import com.z.module.ai.web.rest.admin.model.vo.tool.AiToolPageReqVO;
import com.z.module.ai.web.rest.admin.model.vo.tool.AiToolRespVO;
import com.z.module.ai.web.rest.admin.model.vo.tool.AiToolSaveReqVO;
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

@Tag(name = "管理后台 - AI 工具")
@RestController
@RequestMapping("/ai/tool")
@Validated
public class AiToolController {

    @Resource
    private AiToolService toolService;

    @PostMapping("/create")
    @Operation(summary = "创建工具")
    public Long createTool(@Valid @RequestBody AiToolSaveReqVO createReqVO) {
        return toolService.createTool(createReqVO);
    }

    @PutMapping("/update")
    @Operation(summary = "更新工具")
    public Boolean updateTool(@Valid @RequestBody AiToolSaveReqVO updateReqVO) {
        toolService.updateTool(updateReqVO);
        return true;
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除工具")
    @Parameter(name = "id", description = "编号", required = true)
    public Boolean deleteTool(@RequestParam("id") Long id) {
        toolService.deleteTool(id);
        return true;
    }

    @GetMapping("/get")
    @Operation(summary = "获得工具")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public AiToolRespVO getTool(@RequestParam("id") Long id) {
        AiToolDO tool = toolService.getTool(id);
        return BeanUtils.toBean(tool, AiToolRespVO.class);
    }

    @GetMapping("/page")
    @Operation(summary = "获得工具分页")
    public PageResult<AiToolRespVO> getToolPage(Pageable pageable, @Valid AiToolDO aiToolDO) {
        PageResult<AiToolDO> pageResult = toolService.getToolPage(pageable, aiToolDO);
        return BeanUtils.toBean(pageResult, AiToolRespVO.class);
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得工具列表")
    public List<AiToolRespVO> getToolSimpleList() {
        List<AiToolDO> list = toolService.getToolListByStatus(CommonStatusEnum.ENABLE.getStatus());
        return convertList(list, tool -> new AiToolRespVO()
                .setId(tool.getId()).setName(tool.getName()));
    }

}
