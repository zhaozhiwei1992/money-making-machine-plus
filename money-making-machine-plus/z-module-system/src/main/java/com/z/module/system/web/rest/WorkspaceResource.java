package com.z.module.system.web.rest;

import com.z.module.system.web.vo.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title: WorkspaceResource
 * @Package com/z/module/system/web/rest/WorkspaceResource.java
 * @Description: 工作台信息加载
 * @author zhaozhiwei
 * @date 2024/10/12 17:01
 * @version V1.0
 */

@Tag(name = "工作台API")
@RestController
@RequestMapping(value = "/system/workplace")
@Slf4j
public class WorkspaceResource {

    @GetMapping("total")
    public WorkplaceTotalVO workplaceTotalVO(){
        WorkplaceTotalVO workplaceTotalVO = new WorkplaceTotalVO();
        workplaceTotalVO.setProject(999);
        workplaceTotalVO.setAccess(888);
        workplaceTotalVO.setTodo(111);
        return workplaceTotalVO;
    }

    /**
     * @Description: 项目数
     * @author: zhaozhiwei
     * @data: 2024/10/12-17:27

     * @return: java.util.List<com.z.module.system.web.vo.ProjectVO>
    */

    @GetMapping("project")
    public List<ProjectVO> projectVO(){
        ProjectVO projectVO = new ProjectVO();
        projectVO.setName("赚钱工具-plus");
        projectVO.setIcon("icon");
        projectVO.setMessage("一个牛皮的后端基础项目，完整的用户权限管理及基础设施");
        projectVO.setPersonal("是");
        projectVO.setTime(new Date());
        return List.of(projectVO);
    }

    /**
     * @Description: 动态
     * @author: zhaozhiwei
     * @data: 2024/10/12-17:27

     * @return: java.util.List<com.z.module.system.web.vo.DynamicVO>
    */

    @GetMapping("dynamic")
    public List<DynamicVO> dynamicVO(){
        List<String> list = Arrays.asList("基础项目建设", "用户权限管理", "个人信息维护", "期待....");
        return list.stream().map(s -> {
            DynamicVO dynamicVO = new DynamicVO();
//        dynamicVO.setKeys(List.of(new String[]{"2024-10-12", "2024-10-13", "2024-10-14", "2024-10-15", "2024-10-16", "2024-10-17", "2024-10-18"}));
            dynamicVO.setTime(new Date());
            dynamicVO.setMsg(s);
            return dynamicVO;
        }).collect(Collectors.toList());
    }

    /**
     * @Description: 团队
     * @author: zhaozhiwei
     * @data: 2024/10/12-17:27

     * @return: java.util.List<com.z.module.system.web.vo.TeamVO>
    */

    @GetMapping("team")
    public List<TeamVO> teamVO(){
        TeamVO teamVO = new TeamVO();
        teamVO.setName("架构组");
        teamVO.setIcon("icon");
        return List.of(teamVO);
    }

    /**
     * @Description: 指数
     * @author: zhaozhiwei
     * @data: 2024/10/12-17:28

     * @return: java.util.List<com.z.module.system.web.vo.RadarDataVO>
    */

    @GetMapping("radar")
    public List<RadarDataVO> radarDataVO(){
        RadarDataVO radarDataVO = new RadarDataVO();
        radarDataVO.setTeam(1);
        radarDataVO.setName("x");
        return List.of(radarDataVO);
    }

}
