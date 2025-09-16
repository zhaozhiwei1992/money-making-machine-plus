package com.z.module.system.repository;

import com.z.module.system.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link Notice} entity.
 */
@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    void deleteAllByIdIn(List<Long> idList);
}
