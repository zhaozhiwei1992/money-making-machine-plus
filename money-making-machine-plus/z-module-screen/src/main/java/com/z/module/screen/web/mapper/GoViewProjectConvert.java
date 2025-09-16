package com.z.module.screen.web.mapper;

import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.screen.domain.GoViewProjectDO;
import com.z.module.screen.web.vo.GoViewProjectCreateReqVO;
import com.z.module.screen.web.vo.GoViewProjectRespVO;
import com.z.module.screen.web.vo.GoViewProjectUpdateReqVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GoViewProjectConvert {

    GoViewProjectConvert INSTANCE = Mappers.getMapper(GoViewProjectConvert.class);

    @Mapping(source = "projectName", target = "name")
    GoViewProjectDO convert(GoViewProjectCreateReqVO bean);

    @Mapping(source = "projectName", target = "name")
    @Mapping(source = "indexImage", target = "picUrl")
    @Mapping(source = "remarks", target = "remark")
    @Mapping(source = "state", target = "status")
    GoViewProjectDO convert(GoViewProjectUpdateReqVO bean);

    @Mapping(source = "name", target = "projectName")
    @Mapping(source = "picUrl", target = "indexImage")
    @Mapping(source = "remark", target = "remarks")
    @Mapping(source = "status", target = "state")
    @Mapping(source = "createdBy", target = "createUserId")
    GoViewProjectRespVO convert(GoViewProjectDO bean);

    List<GoViewProjectRespVO> convert(List<GoViewProjectDO> bean);

    PageResult<GoViewProjectRespVO> convertPage(PageResult<GoViewProjectDO> page);

}
