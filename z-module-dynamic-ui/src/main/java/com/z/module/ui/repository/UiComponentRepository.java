package com.z.module.ui.repository;

import com.z.module.ui.domain.UiComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the UiComponent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UiComponentRepository extends JpaRepository<UiComponent, Long> {
    List<UiComponent> findByMenuIdOrderByOrderNumAsc(Long menuid);

    Optional<UiComponent> findByMenuIdAndComponent(Long menuid, String uitable);

    void deleteAllByIdIn(List<Long> idList);
}
