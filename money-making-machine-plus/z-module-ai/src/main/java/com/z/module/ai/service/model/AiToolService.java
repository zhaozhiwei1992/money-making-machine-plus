package com.z.module.ai.service.model;

import com.z.framework.common.util.object.BeanUtils;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.ai.domain.model.AiToolDO;
import com.z.module.ai.repository.AiToolRepository;
import com.z.module.ai.web.rest.admin.model.vo.tool.AiToolSaveReqVO;
import groovy.util.logging.Slf4j;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

import static com.z.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.z.module.ai.enums.ErrorCodeConstants.TOOL_NAME_NOT_EXISTS;
import static com.z.module.ai.enums.ErrorCodeConstants.TOOL_NOT_EXISTS;

/**
 * AI 工具 Service 实现类
 *
 * @author 芋道源码
 */
@lombok.extern.slf4j.Slf4j
@Service
@Validated
@Slf4j
public class AiToolService {

    @Resource
    private AiToolRepository toolRepository;

    @Resource
    private ToolCallbackResolver toolCallbackResolver;

    /**
     * 创建工具
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    public Long createTool(@Valid AiToolSaveReqVO createReqVO) {
        // 校验名称是否存在
        validateToolNameExists(createReqVO.getName());

        // 插入
        AiToolDO tool = BeanUtils.toBean(createReqVO, AiToolDO.class);
        toolRepository.save(tool);
        return tool.getId();
    }

    /**
     * 更新工具
     *
     * @param updateReqVO 更新信息
     */
    public void updateTool(@Valid AiToolSaveReqVO updateReqVO) {
        // 1.1 校验存在
        validateToolExists(updateReqVO.getId());
        // 1.2 校验名称是否存在
        validateToolNameExists(updateReqVO.getName());

        // 2. 更新
        AiToolDO updateObj = BeanUtils.toBean(updateReqVO, AiToolDO.class);
        toolRepository.save(updateObj);
    }

    /**
     * 删除工具
     *
     * @param id 编号
     */
    public void deleteTool(Long id) {
        // 校验存在
        validateToolExists(id);
        // 删除
        toolRepository.deleteById(id);
    }

    /**
     * 校验工具是否存在
     *
     * @param id 编号
     */
    public void validateToolExists(Long id) {
        if (toolRepository.findById(id).orElse(null) == null) {
            throw exception(TOOL_NOT_EXISTS);
        }
    }

    private void validateToolNameExists(String name) {
        ToolCallback toolCallback = toolCallbackResolver.resolve(name);
        if (toolCallback == null) {
            throw exception(TOOL_NAME_NOT_EXISTS, name);
        }
    }

    /**
     * 获得工具
     *
     * @param id 编号
     * @return 工具
     */
    public AiToolDO getTool(Long id) {
        return toolRepository.findById(id).orElse(null);
    }

    /**
     * 获得工具列表
     *
     * @param ids 编号列表
     * @return 工具列表
     */
    public List<AiToolDO> getToolList(Collection<Long> ids) {
        return toolRepository.findAllByIdIn(ids);
    }

    /**
     * 获得工具分页
     *
     * @param pageable 分页查询
     * @param query 查询条件
     * @return 工具分页
     */
    public PageResult<AiToolDO> getToolPage(Pageable pageable, AiToolDO query) {
        log.debug("REST request to get all AiTool for page {}", pageable);

        // 根据id降序
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<AiToolDO> page;
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true)
                .withIgnorePaths("id", "createdDate", "lastModifiedDate");

        Example<AiToolDO> ex = Example.of(query, matcher);
        page = toolRepository.findAll(ex, pageable);

        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    /**
     * 获得工具列表
     *
     * @param status 状态
     * @return 工具列表
     */
    public List<AiToolDO> getToolListByStatus(Integer status) {
        return toolRepository.findByStatus(status);
    }

}
