package com.z.module.system.web.mapper;

import com.z.module.system.domain.Position;
import com.z.framework.common.web.vo.SelectOptionVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PositionSelectMapper {

    PositionSelectMapper INSTANCE = Mappers.getMapper(PositionSelectMapper.class);

    @Mapping(source = "id", target = "value")
    @Mapping(source = "name", target = "label")
    SelectOptionVO convert(Position bean);

    List<SelectOptionVO> convert(List<Position> bean);

}
