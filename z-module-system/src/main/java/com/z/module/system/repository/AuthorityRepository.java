package com.z.module.system.repository;

import com.z.module.system.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    void deleteAllByIdIn(List<Long> idList);
}
