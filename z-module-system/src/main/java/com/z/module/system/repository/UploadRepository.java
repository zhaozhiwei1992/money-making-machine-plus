package com.z.module.system.repository;

import com.z.module.system.domain.Upload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Upload} entity.
 */
@Repository
public interface UploadRepository extends JpaRepository<Upload, Long> {

    Optional<Upload> findOneByCreatedBy(String username);
}
