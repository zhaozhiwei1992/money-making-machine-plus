package com.z.module.system.repository;

import com.z.module.system.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link Department} entity.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    void deleteAllByIdIn(List<Long> idList);

    List<Department> findAllByOrderByOrderNumAsc();
}
