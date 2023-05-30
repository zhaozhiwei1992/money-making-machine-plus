package com.z.module.screen.service;

import com.z.framework.common.web.rest.vm.PageResult;
import com.z.framework.security.util.SecurityUtils;
import com.z.module.screen.domain.GoViewProjectDO;
import com.z.module.screen.repository.GoViewProjectRepository;
import com.z.module.screen.web.mapper.GoViewProjectConvert;
import com.z.module.screen.web.vo.GoViewProjectCreateReqVO;
import com.z.module.screen.web.vo.GoViewProjectUpdateReqVO;
import com.z.module.screen.web.vo.PageParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

/**
 * GoView 项目 Service 实现
 *
 * @author zhaozhiwei
 * @author 芋道源码
 */
@Service
@Validated
public class GoViewProjectService {

    private final GoViewProjectRepository goViewProjectRepository;

    private final GoViewProjectConvert goViewProjectConvert;

    public GoViewProjectService(GoViewProjectRepository goViewProjectRepository, GoViewProjectConvert goViewProjectConvert) {
        this.goViewProjectRepository = goViewProjectRepository;
        this.goViewProjectConvert = goViewProjectConvert;
    }


    public Long createProject(GoViewProjectCreateReqVO createReqVO) {
        // 插入
        GoViewProjectDO goViewProject = goViewProjectConvert.convert(createReqVO)
                .setStatus(1);
        goViewProjectRepository.save(goViewProject);
        // 返回
        return goViewProject.getId();
    }

    
    public void updateProject(GoViewProjectUpdateReqVO updateReqVO) {
        // 校验存在
        validateProjectExists(updateReqVO.getId());
        // 更新
        GoViewProjectDO updateObj = goViewProjectConvert.convert(updateReqVO);
        goViewProjectRepository.save(updateObj);
    }

    
    public void deleteProject(Long id) {
        // 校验存在
        validateProjectExists(id);
        // 删除
        goViewProjectRepository.deleteById(id);
    }

    private void validateProjectExists(Long id) {
        if (!goViewProjectRepository.findById(id).isPresent()) {
            throw new RuntimeException("找不到改项目");
        }
    }

    
    public GoViewProjectDO getProject(Long id) {
        final Optional<GoViewProjectDO> byId = goViewProjectRepository.findById(id);
        return byId.orElse(null);
    }

    /**
     * @data: 2023/5/30-下午3:12
     * @User: zhaozhiwei
     * @method: getMyProjectPage
      * @param pageReqVO :
     * @return: com.z.framework.common.web.rest.vm.PageResult<com.z.module.screen.domain.GoViewProjectDO>
     * @Description: 获取自己创建的项目
     */
    public PageResult<GoViewProjectDO> getMyProjectPage(PageParam pageReqVO) {
        String loginName = SecurityUtils.getCurrentLoginName();
        final PageRequest page = PageRequest.of(pageReqVO.getPage(), pageReqVO.getLimit());
        Page<GoViewProjectDO> pageObj = goViewProjectRepository.findAllByCreatedBy(page, loginName);
        return new PageResult<>(pageObj.getContent(), pageObj.getTotalElements());
    }
}
