package com.z.module.screen.web.rest;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import com.z.module.screen.service.GoViewDataService;
import com.z.module.screen.web.vo.GoViewDataGetBySqlReqVO;
import com.z.module.screen.web.vo.GoViewDataRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

@Tag(name = "GoView 数据", description = "提供 SQL、HTTP 等数据查询的能力")
@RestController
@RequestMapping("/goview/data")
@Validated
public class GoViewDataResource {

    @Resource
    private GoViewDataService goViewDataService;

    @RequestMapping("/get-by-sql")
    @Operation(summary = "使用 SQL 查询数据")
    public GoViewDataRespVO getDataBySQL(@Valid @RequestBody GoViewDataGetBySqlReqVO reqVO) {
        return goViewDataService.getDataBySQL(reqVO.getSql());
    }

    /**
     * @data: 2023/5/30-下午3:18
     * @User: zhaozhiwei
     * @method: getDataByHttp
      * @param params :
 * @param body :  params、body 按照需要去接收，这里仅仅是示例
     * @return: org.springframework.http.ResponseEntity<com.z.framework.common.web.rest.vm.ResponseData<com.z.module.screen.web.vo.GoViewDataRespVO>>
     * @Description: 描述
     */
    @RequestMapping("/get-by-http")
    @Operation(summary = "使用 HTTP 查询数据", description = "这个只是示例接口，实际应该每个查询，都要写一个接口")
    public GoViewDataRespVO getDataByHttp(
            @RequestParam(required = false) Map<String, String> params,
            @RequestBody(required = false) String body) {
        GoViewDataRespVO respVO = new GoViewDataRespVO();
        // 1. 数据维度
        respVO.setDimensions(Arrays.asList("日期", "PV", "UV")); // PV 是每天访问次数；UV 是每天访问人数
        // 2. 明细数据列表
        // 目前通过随机的方式生成。一般来说，这里你可以写逻辑来实现数据的返回
        respVO.setSource(new LinkedList<>());
        for (int i = 1; i <= 12; i++) {
            String date = "2021-" + (i < 10 ? "0" + i : i);
            Integer pv = RandomUtil.randomInt(1000, 10000);
            Integer uv = RandomUtil.randomInt(100, 1000);
            respVO.getSource().add(MapUtil.<String, Object>builder().put("日期", date)
                    .put("PV", pv).put("UV", uv).build());
        }
        return respVO;
    }

}
