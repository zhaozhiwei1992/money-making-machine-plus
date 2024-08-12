package com.z.module.system.service;

import com.z.module.system.domain.RolePermission;
import com.z.module.system.repository.RolePermissionRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: null.java
 * @Package com.longtu.service
 * @Description: 保存角色权限配置信息
 * @date 2022/8/18 下午3:03
 */
@Service
public class RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;

    public RolePermissionService(RolePermissionRepository rolePermissionRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
    }

    public void saveRolePermission(List<Long> roleList, List<Long> permissionList) {
        final List<Long> roleIdList = new ArrayList<>();

        final List<RolePermission> rolePermissionList = new ArrayList<>();
        for (Long roleId : roleList) {
            // appCodeList用来做数据清理
            roleIdList.add(roleId);
            for (Long menuId : permissionList) {
                final RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(menuId);
                rolePermissionList.add(rolePermission);
            }
        }

        rolePermissionRepository.deleteAllByRoleIdIn(roleIdList);
        rolePermissionRepository.saveAll(rolePermissionList);
    }
}
