package com.example.web.mapper;

import com.longtu.domain.TaskParam;
import com.longtu.web.vm.TaskParamDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BooleanStringFormat.class})
public interface TaskParamMapper {

    @Mapping(source = "taskParam.enable", target = "enable")
    TaskParamDTO taskParamToTaskParamDto(TaskParam taskParam);

    List<TaskParamDTO> taskParamsToTaskParamDtos(List<TaskParam> taskParams);
}

