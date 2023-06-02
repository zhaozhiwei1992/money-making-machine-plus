package com.z.module.bpm.web.mapper.definition;

import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.bpm.domain.definition.BpmFormDO;
import com.z.module.bpm.web.vo.definition.form.BpmFormCreateReqVO;
import com.z.module.bpm.web.vo.definition.form.BpmFormRespVO;
import com.z.module.bpm.web.vo.definition.form.BpmFormSimpleRespVO;
import com.z.module.bpm.web.vo.definition.form.BpmFormUpdateReqVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 动态表单 Convert
 *
 * @author 芋艿
 */
@Mapper(componentModel = "spring")
public interface BpmFormConvert {

    BpmFormConvert INSTANCE = Mappers.getMapper(BpmFormConvert.class);

    BpmFormDO convert(BpmFormCreateReqVO bean);

    BpmFormDO convert(BpmFormUpdateReqVO bean);

    BpmFormRespVO convert(BpmFormDO bean);

    List<BpmFormSimpleRespVO> convertList2(List<BpmFormDO> list);

    PageResult<BpmFormRespVO> convertPage(PageResult<BpmFormDO> page);

}
