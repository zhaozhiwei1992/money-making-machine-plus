package com.example.web.rest;

import com.example.domain.Example;
import com.example.repository.ExampleRepository;
import com.example.util.SecurityUtils;
import com.example.web.rest.constants.ResponseCodeEnum;
import com.example.web.rest.vm.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "测试API")
@RestController
@RequestMapping(value = {"/api", "/api/ext"})
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ExampleResource {

    @Value("${ifmis.clientApp.name}")
    private String applicationName;

    private final ExampleRepository exampleRepository;

    public ExampleResource(ExampleRepository exampleRepository) {
        this.exampleRepository = exampleRepository;
    }

    /**
     * @data: 2022/7/14-上午9:24
     * @User: zhaozhiwei
     * @method: createExample
      * @param example :
     * @return: com.longtu.web.vm.ResponseData<com.longtu.domain.Example>
     * @Description: 数据保存
     */
    @PostMapping("/examples")
    public ResponseData<Example> createExample(@RequestBody Example example) {
        log.debug("REST request to save Example : {}", example);
        this.exampleRepository.save(example);

        final ResponseData<Example> exampleResponseData = new ResponseData<>();
        exampleResponseData.setCode(String.valueOf(HttpStatus.OK.value()));
        exampleResponseData.setData(example);
        return exampleResponseData;
    }

    /**
     * @data: 2022/7/14-上午9:24
     * @User: zhaozhiwei
     * @method: getAllExamples
      * @param pageable :
     * @return: com.longtu.web.vm.ResponseData<java.util.List<com.longtu.domain.Example>>
     * @Description: 全部数据获取
     */
    @GetMapping("/examples")
    public ResponseData<List<Example>> getAllExamples(Pageable pageable) {
        log.debug("REST request to get all Example for an admin");

        final List<Example> all = exampleRepository.findAll();

        final ResponseData<List<Example>> listResponseData = new ResponseData<>();
        listResponseData.setData(all);
        listResponseData.setCode("0");
        listResponseData.setCount(all.size());
        return listResponseData;
    }

    /**
     * @data: 2022/7/14-上午9:18
     * @User: zhaozhiwei
     * @method: deleteExample
      * @param idList : 
     * @return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Description: 批量删除示例
     */
    @PostMapping("/examples/batch/delete")
    public ResponseEntity<Void> deleteExample(@RequestParam List<Long> idList) {
        log.debug("REST request to delete Examples, ids: {}", idList);
        this.exampleRepository.deleteAllByIdIn(idList);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "测试里的hello world")
    @GetMapping("/examples/echo")
    public ResponseEntity<ResponseData<String>> echo(){
        log.info("认证通过");
        final String currentLoginName = SecurityUtils.getCurrentLoginName();
        final String tokenId = SecurityUtils.getTokenId();
        // 获取当前认证通过信息
        final ResponseData<String> responseData = new ResponseData<>();
        responseData.setCode(ResponseCodeEnum.SUCCESS.getCode());
        responseData.setData(currentLoginName);
        return ResponseEntity.ok().body(responseData);
    }
}
