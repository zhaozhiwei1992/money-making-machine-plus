package com.z.module.ui.repository;

import com.z.module.ui.domain.UiTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the UiTable entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UiTableRepository extends JpaRepository<UiTable, Long> {
    List<UiTable> findByMenuIdOrderByOrderNumAsc(Long menuid);

    void deleteAllByIdIn(List<Long> idList);
}
