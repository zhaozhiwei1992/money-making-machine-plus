package com.z.module.ai.repository;

import com.z.module.ai.domain.PlatformConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ${comments}
 * 
 * @author zhaozhiwei
 * @email zhaozhiweishanxi@gmail.com
 * @date 2025-09-16T06:53:12.650468005Z
 */
@Repository
public interface PlatformConfigRepository extends JpaRepository<PlatformConfig, Long> {
    void deleteAllByIdIn(List<Long> idList);
}
