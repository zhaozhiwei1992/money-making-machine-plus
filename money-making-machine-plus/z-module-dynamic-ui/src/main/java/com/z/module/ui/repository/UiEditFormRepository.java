package com.z.module.ui.repository;

import com.z.module.ui.domain.UiEditForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the UiEditform entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UiEditFormRepository extends JpaRepository<UiEditForm, Long> {
    List<UiEditForm> findByMenuIdOrderByOrderNumAsc(Long menuid);

    void deleteAllByIdIn(List<Long> idList);
}
