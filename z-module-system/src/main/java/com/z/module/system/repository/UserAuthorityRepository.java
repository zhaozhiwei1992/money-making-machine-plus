package com.z.module.system.repository;

import com.z.module.system.domain.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link UserAuthority} entity.
 */
@Repository
public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {
    void deleteAllByIdIn(List<Long> idList);

    List<UserAuthority> findAllByUserId(Long id);

    List<UserAuthority> findAllByUserIdIn(List<Long> collect);
}
