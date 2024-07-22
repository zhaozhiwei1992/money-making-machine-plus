package com.z.module.system.repository;

import com.z.module.system.domain.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link Position} entity.
 */
@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    void deleteAllByIdIn(List<Long> idList);

    List<Position> findAllByOrderByOrderNumAsc();
}
