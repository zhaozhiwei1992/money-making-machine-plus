package com.z.module.system.repository;

import com.z.module.system.domain.RoleMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the RoleMenu entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RoleMenuRepository extends JpaRepository<RoleMenu, Long> {
    void deleteAllByRoleIdIn(List<Long> roleIdList);

    List<RoleMenu> findByRoleIdIn(List<Long> roleIds);
}
