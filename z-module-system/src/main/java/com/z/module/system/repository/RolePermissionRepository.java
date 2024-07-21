package com.z.module.system.repository;

import com.z.module.system.domain.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link RolePermission} entity.
 */
@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
}
