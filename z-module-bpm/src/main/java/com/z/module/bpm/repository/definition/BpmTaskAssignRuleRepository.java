package com.z.module.bpm.repository.definition;

import com.z.module.bpm.domain.definition.BpmFormDO;
import com.z.module.bpm.domain.definition.BpmTaskAssignRuleDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link BpmFormDO} entity.
 */
@Repository
public interface BpmTaskAssignRuleRepository extends JpaRepository<BpmTaskAssignRuleDO, Long> {
    void deleteAllByIdIn(List<Long> idList);

    List<BpmTaskAssignRuleDO> findAllByModelIdAndTaskDefinitionKey(String modelId, String taskDefinitionKey);

    List<BpmTaskAssignRuleDO> findAllByModelId(String modelId);

    List<BpmTaskAssignRuleDO> findAllByProcessDefinitionIdAndTaskDefinitionKey(String processDefinitionId, String taskDefinitionKey);
}
