package com.z.module.system.web.rest;

import com.z.module.system.domain.EleUnion;
import com.z.module.system.service.CommonEleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "数据字典API")
@RestController
@RequestMapping("/system")
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class DictResource {

    private final CommonEleService commonEleService;

    public DictResource(CommonEleService commonEleService) {
        this.commonEleService = commonEleService;
    }

    @Operation(description = "获取数据字典")
    @GetMapping("/dict/list")
    public Map<String, List<Map<String, Object>>> getAllDictList() {
        final List<EleUnion> all = commonEleService.findAll();
        Map<String, List<Map<String, Object>>> resultMap = all.stream()
                .collect(Collectors.groupingBy(EleUnion::getEleCatCode, Collectors.mapping(eleUnion -> {
                    Map<String, Object> map = new HashMap<>();
                    // 这里可以根据实际需要将 EleUnion 转换为 Map
                    // 例如：map.put("fieldName", eleUnion.getFieldName());
                    map.put("value", eleUnion.getEleCode());
                    map.put("label", eleUnion.getEleName());
                    return map;
                }, Collectors.toList())));
        return resultMap;
    }

    @Operation(description = "获取数据字典")
    @GetMapping("/dict/one")
    public List<Map<String, Object>> getOneDict() {
//        final List<Map<String, Object>> allEleCategory = commonEleService.findAllEleCategory();
        final List<Map<String, Object>> maps = new ArrayList<>();
        {
            final HashMap<String, Object> map = new HashMap<>();
            map.put("value", 0);
            map.put("lable", "hh");
            maps.add(map);
        }
        return maps;
    }

}
