package com.z.module.ai.repository;

import com.z.module.ai.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link History} entity.
 */
@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    void deleteAllByIdIn(List<Long> idList);
}
