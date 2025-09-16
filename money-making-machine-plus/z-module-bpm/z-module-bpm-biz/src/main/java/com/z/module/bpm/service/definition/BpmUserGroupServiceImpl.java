package com.z.module.bpm.service.definition;

import cn.hutool.core.collection.CollUtil;
import com.z.framework.common.util.collection.CollectionUtils;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.bpm.domain.definition.BpmUserGroupDO;
import com.z.module.bpm.enums.CommonStatusEnum;
import com.z.module.bpm.repository.definition.BpmUserGroupRepository;
import com.z.module.bpm.web.mapper.definition.BpmUserGroupConvert;
import com.z.module.bpm.web.vo.definition.group.BpmUserGroupCreateReqVO;
import com.z.module.bpm.web.vo.definition.group.BpmUserGroupPageReqVO;
import com.z.module.bpm.web.vo.definition.group.BpmUserGroupUpdateReqVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.z.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.z.module.bpm.enums.ErrorCodeConstants.USER_GROUP_IS_DISABLE;
import static com.z.module.bpm.enums.ErrorCodeConstants.USER_GROUP_NOT_EXISTS;

/**
 * 用户组 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class BpmUserGroupServiceImpl implements BpmUserGroupService {

    @Autowired
    private BpmUserGroupRepository bpmUserGroupRepository;

    @Autowired
    private BpmUserGroupConvert bpmUserGroupConvert;

    @Override
    public Long createUserGroup(BpmUserGroupCreateReqVO createReqVO) {
        // 插入
        BpmUserGroupDO userGroup = bpmUserGroupConvert.convert(createReqVO);
        bpmUserGroupRepository.save(userGroup);
        // 返回
        return userGroup.getId();
    }

    @Override
    public void updateUserGroup(BpmUserGroupUpdateReqVO updateReqVO) {
        // 校验存在
        this.validateUserGroupExists(updateReqVO.getId());
        // 更新
        BpmUserGroupDO updateObj = bpmUserGroupConvert.convert(updateReqVO);
        bpmUserGroupRepository.save(updateObj);
    }

    @Override
    public void deleteUserGroup(Long id) {
        // 校验存在
        this.validateUserGroupExists(id);
        // 删除
        bpmUserGroupRepository.deleteById(id);
    }

    private void validateUserGroupExists(Long id) {
        if (!bpmUserGroupRepository.findById(id).isPresent()) {
            throw exception(USER_GROUP_NOT_EXISTS);
        }
    }

    @Override
    public BpmUserGroupDO getUserGroup(Long id) {
        return bpmUserGroupRepository.findById(id).orElse(null);
    }

    @Override
    public List<BpmUserGroupDO> getUserGroupList(Collection<Long> ids) {
        return bpmUserGroupRepository.findAllById(ids);
    }


    @Override
    public List<BpmUserGroupDO> getUserGroupListByStatus(Integer status) {
        return bpmUserGroupRepository.findAllByStatus(status);
    }

    @Override
    public PageResult<BpmUserGroupDO> getUserGroupPage(BpmUserGroupPageReqVO pageReqVO) {
        final PageRequest page = PageRequest.of(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<BpmUserGroupDO> pageData = bpmUserGroupRepository.findAllByNameAndStatus(page, pageReqVO.getName(), pageReqVO.getStatus());
        return new PageResult<>(pageData.getContent(), pageData.getTotalElements());
    }

    @Override
    public void validUserGroups(Set<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得用户组信息
        List<BpmUserGroupDO> userGroups = bpmUserGroupRepository.findAllById(ids);
        Map<Long, BpmUserGroupDO> userGroupMap = CollectionUtils.convertMap(userGroups, BpmUserGroupDO::getId);
        // 校验
        ids.forEach(id -> {
            BpmUserGroupDO userGroup = userGroupMap.get(id);
            if (userGroup == null) {
                throw exception(USER_GROUP_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(userGroup.getStatus())) {
                throw exception(USER_GROUP_IS_DISABLE, userGroup.getName());
            }
        });
    }

}
