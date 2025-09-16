package com.z.module.ui.repository;

import com.z.module.ui.domain.UiTab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the UiTab entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UiTabRepository extends JpaRepository<UiTab, Long> {
    List<UiTab> findByMenuId(Long menuid);

    void deleteAllByIdIn(List<Long> idList);
}
