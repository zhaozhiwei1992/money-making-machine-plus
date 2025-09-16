package com.z.module.system.repository;

import com.z.module.system.domain.UserOpenId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link UserOpenId} entity.
 * 用户login和openId关系
 */
@Repository
public interface UserOpenIdRepository extends JpaRepository<UserOpenId, Long> {
    Optional<UserOpenId> findOneByOpenId(String openId);

    void deleteByOpenId(String openId);

    void deleteByLogin(String login);
}
