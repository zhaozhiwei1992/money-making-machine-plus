package com.z.module.system.service;

import com.z.module.system.domain.RoleMenu;
import com.z.module.system.repository.RoleMenuRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: null.java
 * @Package com.longtu.service
 * @Description: 保存角色菜单配置信息
 * @date 2022/8/18 下午3:03
 */
@Service
public class RoleMenuService {

    private final RoleMenuRepository roleMenuRepository;

    public RoleMenuService(RoleMenuRepository roleMenuRepository) {
        this.roleMenuRepository = roleMenuRepository;
    }

    @CacheEvict(allEntries = true, value = {"loginMenuCache"})
    public void saveRoleMenu(List<Long> roleList, List<Long> menuList) {
        final List<Long> roleIdList = new ArrayList<>();

        final List<RoleMenu> roleMenuList = new ArrayList<>();
        for (Long roleId : roleList) {
            // appCodeList用来做数据清理
            roleIdList.add(roleId);
            for (Long menuId : menuList) {
                final RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenuList.add(roleMenu);
            }
        }

        roleMenuRepository.deleteAllByRoleIdIn(roleIdList);
        roleMenuRepository.saveAll(roleMenuList);
    }
}
