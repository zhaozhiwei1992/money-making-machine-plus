package com.z.module.system.web.mapper;

import com.z.module.system.domain.Authority;
import com.z.framework.common.web.vo.SelectOptionVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleSelectMapper {

    RoleSelectMapper INSTANCE = Mappers.getMapper(RoleSelectMapper.class);

    @Mapping(source = "id", target = "value")
    @Mapping(source = "name", target = "label")
    SelectOptionVO convert(Authority bean);

    List<SelectOptionVO> convert(List<Authority> bean);

}
