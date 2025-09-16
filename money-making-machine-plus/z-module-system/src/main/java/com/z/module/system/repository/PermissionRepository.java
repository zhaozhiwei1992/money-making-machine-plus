package com.z.module.system.repository;

import com.z.module.system.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link Permission} entity.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    void deleteAllByIdIn(List<Long> idList);
}
