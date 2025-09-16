package com.z.module.bpm.web.rest.definition;

import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.bpm.domain.definition.BpmUserGroupDO;
import com.z.module.bpm.enums.CommonStatusEnum;
import com.z.module.bpm.service.definition.BpmUserGroupService;
import com.z.module.bpm.web.mapper.definition.BpmUserGroupConvert;
import com.z.module.bpm.web.vo.definition.group.BpmUserGroupCreateReqVO;
import com.z.module.bpm.web.vo.definition.group.BpmUserGroupPageReqVO;
import com.z.module.bpm.web.vo.definition.group.BpmUserGroupRespVO;
import com.z.module.bpm.web.vo.definition.group.BpmUserGroupUpdateReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "用户组")
@RestController
@RequestMapping("/bpm/user-group")
@Validated
public class BpmUserGroupResource {

    @Resource
    private BpmUserGroupService userGroupService;

    @PostMapping("/create")
    @Operation(summary = "创建用户组")

    public Long createUserGroup(@Valid @RequestBody BpmUserGroupCreateReqVO createReqVO) {
        return userGroupService.createUserGroup(createReqVO);
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户组")

    public boolean updateUserGroup(@Valid @RequestBody BpmUserGroupUpdateReqVO updateReqVO) {
        userGroupService.updateUserGroup(updateReqVO);
        return true;
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户组")
    @Parameter(name = "id", description = "编号", required = true)

    public boolean deleteUserGroup(@RequestParam("id") Long id) {
        userGroupService.deleteUserGroup(id);
        return true;
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户组")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")

    public BpmUserGroupRespVO getUserGroup(@RequestParam("id") Long id) {
        BpmUserGroupDO userGroup = userGroupService.getUserGroup(id);
        return BpmUserGroupConvert.INSTANCE.convert(userGroup);
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户组分页")

    public PageResult<BpmUserGroupRespVO> getUserGroupPage(@Valid BpmUserGroupPageReqVO pageVO) {
        PageResult<BpmUserGroupDO> pageResult = userGroupService.getUserGroupPage(pageVO);
        return BpmUserGroupConvert.INSTANCE.convertPage(pageResult);
    }

    @GetMapping("/list-all-simple")
    @Operation(summary = "获取用户组精简信息列表", description = "只包含被开启的用户组，主要用于前端的下拉选项")
    public List<BpmUserGroupRespVO> getSimpleUserGroups() {
        // 获用户门列表，只要开启状态的
        List<BpmUserGroupDO> list = userGroupService.getUserGroupListByStatus(CommonStatusEnum.ENABLE.getStatus());
        // 排序后，返回给前端
        return BpmUserGroupConvert.INSTANCE.convertList2(list);
    }

}
