package com.z.module.system.repository;

import com.z.module.system.domain.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the Area entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {

    void deleteAllByIdIn(List<Long> idList);

    List<Area> findAllByParentId(long l);

    List<Area> findAllByOrderByIdAsc();
}
