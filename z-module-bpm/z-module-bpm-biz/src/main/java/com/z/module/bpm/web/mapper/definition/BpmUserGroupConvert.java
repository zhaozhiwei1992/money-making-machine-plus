package com.z.module.bpm.web.mapper.definition;

import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.bpm.domain.definition.BpmUserGroupDO;
import com.z.module.bpm.web.vo.definition.group.BpmUserGroupCreateReqVO;
import com.z.module.bpm.web.vo.definition.group.BpmUserGroupRespVO;
import com.z.module.bpm.web.vo.definition.group.BpmUserGroupUpdateReqVO;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户组 Convert
 *
 * @author 芋道源码
 */
@Mapper(componentModel = "spring")
public interface BpmUserGroupConvert {

    BpmUserGroupConvert INSTANCE = Mappers.getMapper(BpmUserGroupConvert.class);

    BpmUserGroupDO convert(BpmUserGroupCreateReqVO bean);

    BpmUserGroupDO convert(BpmUserGroupUpdateReqVO bean);

    BpmUserGroupRespVO convert(BpmUserGroupDO bean);

    List<BpmUserGroupRespVO> convertList(List<BpmUserGroupDO> list);

    PageResult<BpmUserGroupRespVO> convertPage(PageResult<BpmUserGroupDO> page);

    @Named("convertList2")
    List<BpmUserGroupRespVO> convertList2(List<BpmUserGroupDO> list);

}
