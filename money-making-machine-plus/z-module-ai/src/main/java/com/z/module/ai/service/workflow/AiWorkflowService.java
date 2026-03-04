package com.z.module.ai.service.workflow;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.framework.common.util.object.BeanUtils;
import com.z.module.ai.web.rest.admin.workflow.vo.AiWorkflowPageReqVO;
import com.z.module.ai.web.rest.admin.workflow.vo.AiWorkflowSaveReqVO;
import com.z.module.ai.web.rest.admin.workflow.vo.AiWorkflowTestReqVO;
import com.z.module.ai.domain.workflow.AiWorkflowDO;
import com.z.module.ai.repository.AiWorkflowRepository;
import com.z.module.ai.service.model.AiModelService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dev.tinyflow.core.Tinyflow;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.z.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.z.module.ai.enums.ErrorCodeConstants.WORKFLOW_CODE_EXISTS;
import static com.z.module.ai.enums.ErrorCodeConstants.WORKFLOW_NOT_EXISTS;

/**
 * AI 工作流 Service 实现类
 *
 * @author lesan
 */
@Service
@Slf4j
public class AiWorkflowService {

    @Resource
    private AiWorkflowRepository workflowRepository;

    @Resource
    private AiModelService apiModelService;

    public Long createWorkflow(AiWorkflowSaveReqVO createReqVO) {
        // 1. 参数校验
        validateCodeUnique(null, createReqVO.getCode());

        // 2. 插入工作流配置
        AiWorkflowDO workflow = BeanUtils.toBean(createReqVO, AiWorkflowDO.class);
        workflowRepository.save(workflow);
        return workflow.getId();
    }

    public void updateWorkflow(AiWorkflowSaveReqVO updateReqVO) {
        // 1. 参数校验
        validateWorkflowExists(updateReqVO.getId());
        validateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 2. 更新工作流配置
        AiWorkflowDO workflow = BeanUtils.toBean(updateReqVO, AiWorkflowDO.class);
        workflowRepository.save(workflow);
    }

    public void deleteWorkflow(Long id) {
        // 1. 校验存在
        validateWorkflowExists(id);

        // 2. 删除工作流配置
        workflowRepository.deleteById(id);
    }

    private AiWorkflowDO validateWorkflowExists(Long id) {
        if (ObjUtil.isNull(id)) {
            throw exception(WORKFLOW_NOT_EXISTS);
        }
        AiWorkflowDO workflow = workflowRepository.findById(id).orElse(null);
        if (ObjUtil.isNull(workflow)) {
            throw exception(WORKFLOW_NOT_EXISTS);
        }
        return workflow;
    }

    private void validateCodeUnique(Long id, String code) {
        if (StrUtil.isBlank(code)) {
            return;
        }
        AiWorkflowDO workflow = workflowRepository.findByCode(code);
        if (ObjUtil.isNull(workflow)) {
            return;
        }
        if (ObjUtil.isNull(id)) {
            throw exception(WORKFLOW_CODE_EXISTS);
        }
        if (ObjUtil.notEqual(workflow.getId(), id)) {
            throw exception(WORKFLOW_CODE_EXISTS);
        }
    }

    public AiWorkflowDO getWorkflow(Long id) {
        return workflowRepository.findById(id).orElse(null);
    }

    public PageResult<AiWorkflowDO> getWorkflowPage(Pageable pageable, AiWorkflowDO query) {
        log.debug("REST request to get all AiWorkflow for page {}", pageable);

        // 根据id降序
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<AiWorkflowDO> page;
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true)
                .withIgnorePaths("id", "createdDate", "lastModifiedDate");

        Example<AiWorkflowDO> ex = Example.of(query, matcher);
        page = workflowRepository.findAll(ex, pageable);

        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    public Object testWorkflow(AiWorkflowTestReqVO testReqVO) {
        // 加载 graph
        String graph = testReqVO.getGraph() != null ? testReqVO.getGraph()
                : validateWorkflowExists(testReqVO.getId()).getGraph();

        // 构建 TinyFlow 执行链
        Tinyflow tinyflow = parseFlowParam(graph);

        // 执行
        Map<String, Object> variables = testReqVO.getParams();
        return tinyflow.toChain().executeForResult(variables);
    }

    private Tinyflow parseFlowParam(String graph) {
        // TODO @lesan：可以使用 jackson 哇？
        JSONObject json = JSONObject.parseObject(graph);
        JSONArray nodeArr = json.getJSONArray("nodes");
        Tinyflow tinyflow = new Tinyflow(json.toJSONString());
        for (int i = 0; i < nodeArr.size(); i++) {
            JSONObject node = nodeArr.getJSONObject(i);
            switch (node.getString("type")) {
                case "llmNode":
                    JSONObject data = node.getJSONObject("data");
                    apiModelService.getLLmProvider4Tinyflow(tinyflow, data.getLong("llmId"));
                    break;
                case "internalNode":
                    break;
                default:
                    break;
            }
        }
        return tinyflow;
    }

}
