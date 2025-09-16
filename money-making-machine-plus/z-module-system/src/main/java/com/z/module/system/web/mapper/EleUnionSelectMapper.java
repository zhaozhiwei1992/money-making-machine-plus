package com.z.module.system.web.mapper;

import com.z.module.system.domain.EleUnion;
import com.z.module.system.web.vo.EleSelectOptionVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EleUnionSelectMapper {

    @Mapping(source = "eleCode", target = "value")
    @Mapping(source = "eleName", target = "label")
    @Mapping(source = "eleCode", target = "id")
    EleSelectOptionVO convert(EleUnion bean);

    List<EleSelectOptionVO> convert(List<EleUnion> bean);
}
