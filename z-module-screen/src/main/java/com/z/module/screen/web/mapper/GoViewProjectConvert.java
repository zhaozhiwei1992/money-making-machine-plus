package com.z.module.screen.web.mapper;

import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.screen.domain.GoViewProjectDO;
import com.z.module.screen.web.vo.GoViewProjectCreateReqVO;
import com.z.module.screen.web.vo.GoViewProjectRespVO;
import com.z.module.screen.web.vo.GoViewProjectUpdateReqVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GoViewProjectConvert {

    GoViewProjectConvert INSTANCE = Mappers.getMapper(GoViewProjectConvert.class);

    GoViewProjectDO convert(GoViewProjectCreateReqVO bean);

    GoViewProjectDO convert(GoViewProjectUpdateReqVO bean);

    GoViewProjectRespVO convert(GoViewProjectDO bean);

    PageResult<GoViewProjectRespVO> convertPage(PageResult<GoViewProjectDO> page);

}
