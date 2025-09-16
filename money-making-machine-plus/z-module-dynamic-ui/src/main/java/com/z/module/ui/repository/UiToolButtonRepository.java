package com.z.module.ui.repository;

import com.z.module.ui.domain.UiToolButton;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the UiToolButton entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UiToolButtonRepository extends JpaRepository<UiToolButton, Long> {
    List<UiToolButton> findByMenuIdOrderByOrderNumAsc(Long menuid);

    void deleteAllByIdIn(List<Long> idList);
}
