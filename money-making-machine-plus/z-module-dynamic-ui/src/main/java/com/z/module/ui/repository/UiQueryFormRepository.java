package com.z.module.ui.repository;

import com.z.module.ui.domain.UiQueryForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the UiQueryform entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UiQueryFormRepository extends JpaRepository<UiQueryForm, Long> {
    List<UiQueryForm> findByMenuIdOrderByOrderNumAsc(Long menuid);

    void deleteAllByIdIn(List<Long> idList);
}
