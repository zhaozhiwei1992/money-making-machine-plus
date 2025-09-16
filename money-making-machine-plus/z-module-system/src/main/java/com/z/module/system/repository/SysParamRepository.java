package com.z.module.system.repository;

import com.z.module.system.domain.SystemParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the SystemParam entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SysParamRepository extends JpaRepository<SystemParam, Long> {

    Optional<SystemParam> findOneByCode(String code);

    void deleteAllByIdIn(List<Long> idList);
}
