package com.z.module.system.repository;

import com.z.module.system.domain.UserPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link UserPosition} entity.
 * 用户岗位信息
 */
@Repository
public interface UserPositionRepository extends JpaRepository<UserPosition, Long> {
    void deleteAllByIdIn(List<Long> idList);

    List<UserPosition> findAllByUserIdIn(List<Long> collect);
}
