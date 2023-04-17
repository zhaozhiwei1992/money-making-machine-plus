package com.example.repository;

import com.example.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link Example} entity.
 */
@Repository
public interface ExampleRepository extends JpaRepository<Example, Long> {
    void deleteAllByIdIn(List<Long> idList);
}
