package com.z.module.system.repository;

import com.z.module.system.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link Example} entity.
 */
@Repository
public interface ExampleRepository extends JpaRepository<Example, Long> {
    void deleteAllByIdIn(List<Long> idList);
}
