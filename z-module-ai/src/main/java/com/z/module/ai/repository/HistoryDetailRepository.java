package com.z.module.ai.repository;

import com.z.module.ai.domain.HistoryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link HistoryDetail} entity.
 */
@Repository
public interface HistoryDetailRepository extends JpaRepository<HistoryDetail, Long> {
    void deleteAllByIdIn(List<Long> idList);

    List<HistoryDetail> findAllByHistoryIdOrderByIdAsc(Long historyId);
}
