package com.z.module.system.web.mapper;

import com.z.module.system.domain.EleUnion;
import com.z.module.system.web.vo.SelectOptionVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EleUnionSelectMapper {

    @Mapping(source = "eleCode", target = "value")
    @Mapping(source = "eleName", target = "label")
    @Mapping(source = "eleCode", target = "id")
    SelectOptionVO convert(EleUnion bean);

    List<SelectOptionVO> convert(List<EleUnion> bean);
}
