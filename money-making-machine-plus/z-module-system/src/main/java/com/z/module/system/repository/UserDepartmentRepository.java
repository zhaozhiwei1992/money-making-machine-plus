package com.z.module.system.repository;

import com.z.module.system.domain.UserDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link UserDepartment} entity.
 */
@Repository
public interface UserDepartmentRepository extends JpaRepository<UserDepartment, Long> {
    void deleteAllByIdIn(List<Long> idList);

    List<UserDepartment> findAllByUserIdIn(List<Long> collect);
}
