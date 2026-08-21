package com.z.module.template.repository;

import com.z.module.template.domain.TemplateItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the TemplateItem entity.
 */
@Repository
public interface TemplateItemRepository extends JpaRepository<TemplateItem, Long> {

    Optional<TemplateItem> findByCode(String code);

    void deleteAllByIdIn(List<Long> idList);
}