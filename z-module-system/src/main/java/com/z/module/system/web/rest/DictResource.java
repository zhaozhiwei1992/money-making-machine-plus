package com.z.module.system.web.rest;

import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.module.system.service.CommonEleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "数据字典API")
@RestController
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class DictResource {

    private final CommonEleService commonEleService;

    public DictResource(CommonEleService commonEleService) {
        this.commonEleService = commonEleService;
    }

    @Operation(description = "获取数据字典")
    @GetMapping("/dict/list")
    public ResponseEntity<ResponseData<Map<String, List<Map<String, Object>>>>> getAllDictList() {
//        final List<Map<String, Object>> allEleCategory = commonEleService.findAllEleCategory();
        final Map<String, List<Map<String, Object>>> result = new HashMap<>();
        final List<Map<String, Object>> maps = new ArrayList<>();
        {
            final HashMap<String, Object> map = new HashMap<>();
            map.put("value", 0);
            map.put("lable", "hh");
            maps.add(map);
        }
        result.put("importance", maps);
        return ResponseData.ok(result);
    }

    @Operation(description = "获取数据字典")
    @GetMapping("/dict/one")
    public ResponseEntity<ResponseData<List<Map<String, Object>>>> getOneDict() {
//        final List<Map<String, Object>> allEleCategory = commonEleService.findAllEleCategory();
        final List<Map<String, Object>> maps = new ArrayList<>();
        {
            final HashMap<String, Object> map = new HashMap<>();
            map.put("value", 0);
            map.put("lable", "hh");
            maps.add(map);
        }
        return ResponseData.ok(maps);
    }

}
