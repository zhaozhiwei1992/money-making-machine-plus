package com.z.module.system.repository;

import com.z.module.system.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the Menu entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findAllByOrderByOrderNumAsc();

    List<Menu> findAllByIdInOrderByOrderNumAsc(List<Long> menuIdList);

    void deleteAllByIdIn(List<Long> idList);

    List<Menu> findAllByParentIdOrderByOrderNumAsc(Long parentId);

    List<Menu> findAllByMenuTypeOrderByOrderNumAsc(Integer code);

    List<Menu> findAllByMenuTypeInOrderByOrderNumAsc(List<Integer> list);

    Optional<Menu> findOneByUrl(String requestURI);

    List<Menu> findAllByIdInAndMenuTypeInOrderByOrderNumAsc(List<Long> menuIdList, List<Integer> list);
}
