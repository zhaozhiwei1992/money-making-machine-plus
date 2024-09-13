package com.z.module.ai.repository;

import com.z.module.ai.domain.Engine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link Engine} entity.
 */
@Repository
public interface EngineRepository extends JpaRepository<Engine, Long> {
    void deleteAllByIdIn(List<Long> idList);
}
