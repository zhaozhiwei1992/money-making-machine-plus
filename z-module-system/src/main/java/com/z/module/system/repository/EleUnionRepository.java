package com.z.module.system.repository;

import com.z.module.system.domain.EleUnion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the EleUnion entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EleUnionRepository extends JpaRepository<EleUnion, Long> {
    List<EleUnion> findByEleCatCode(String eleCatCode);
}
