package com.z.module.bpm.service;

import cn.hutool.core.lang.tree.Tree;
import com.z.framework.common.service.MenuRouterExtService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title: BpmMenuRouterExtServiceImpl
 * @Package com/z/module/bpm/service/BpmMenuRouterExtServiceImpl.java
 * @Description: bpm模块扩展路由, 不需要在界面上配置, 写到前端文件也行, 就是有点2
 * @author zhaozhiwei
 * @date 2023/5/28 下午7:17
 * @version V1.0
 */
@Service
public class BpmMenuRouterExtServiceImpl implements MenuRouterExtService {
    @Override
    public List<Tree<Long>> customTreeNodeList() {

        List<Tree<Long>> treeNodes = new ArrayList<>();
        {
            // 表单新增页面
            final Tree<Long> tree = new Tree<>();
            tree.setId(999L);
            tree.setParentId(0L);
            tree.setName("BpmFormEditor");
            tree.putExtra("component", "views/bpm/Form/Editor/Index");
            tree.putExtra("path", "/formEditor");
            final Map<String, Object> metaMap = new HashMap<>();
            metaMap.put("menu_id", tree.getId());
            metaMap.put("title", tree.getName());
            metaMap.put("hidden", true);
            tree.putExtra("meta", metaMap);
            treeNodes.add(tree);
        }
        {
            // 流程定义修改页面
            final Tree<Long> tree = new Tree<>();
            tree.setId(998L);
            tree.setParentId(0L);
            tree.setName("BpmModelEditor");
            tree.putExtra("component", "views/bpm/Model/Editor/Index");
            tree.putExtra("path", "/modelEditor");
            final Map<String, Object> metaMap = new HashMap<>();
            metaMap.put("menu_id", tree.getId());
            metaMap.put("title", tree.getName());
            metaMap.put("hidden", true);
            tree.putExtra("meta", metaMap);
            treeNodes.add(tree);
        }
        {
            final Tree<Long> tree = new Tree<>();
            tree.setId(997L);
            tree.setParentId(0L);
            tree.setName("BpmTaskAssignRuleList");
            tree.putExtra("component", "views/bpm/TaskAssignRule/Index");
            tree.putExtra("path", "/taskAssignRuleList");
            final Map<String, Object> metaMap = new HashMap<>();
            metaMap.put("menu_id", tree.getId());
            metaMap.put("title", tree.getName());
            metaMap.put("hidden", true);
            tree.putExtra("meta", metaMap);
            treeNodes.add(tree);
        }
        return treeNodes;
    }
}
