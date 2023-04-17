package com.example.repository;

import com.example.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the Menu entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findAllByOrderByOrdernumAsc();

    List<Menu> findAllByIdInOrderByOrdernumAsc(List<Long> menuIdList);

    void deleteAllByIdIn(List<Long> idList);

    List<Menu> findAllByParentIdOrderByOrdernumAsc(Long parentId);
}
