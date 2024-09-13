package com.z.module.ai.web.mapper;

import com.z.framework.common.web.vo.SelectOptionVO;
import com.z.module.ai.domain.Engine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EngineSelectMapper {

    @Mapping(source = "id", target = "value")
    @Mapping(source = "name", target = "label")
    SelectOptionVO convert(Engine bean);

    List<SelectOptionVO> convert(List<Engine> bean);

}
