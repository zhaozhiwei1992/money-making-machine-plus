package com.z.module.bpm.repository.definition;

import com.z.module.bpm.domain.definition.BpmFormDO;
import com.z.module.bpm.domain.definition.BpmProcessDefinitionExtDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link BpmFormDO} entity.
 */
@Repository
public interface BpmProcessDefinitionExtRepository extends JpaRepository<BpmProcessDefinitionExtDO, Long> {
    void deleteAllByIdIn(List<Long> idList);

    Optional<BpmProcessDefinitionExtDO> findOneByProcessDefinitionId(String id);

    List<BpmProcessDefinitionExtDO> findAllByProcessDefinitionIdIn(List<String> strings);
}
