package com.z.module.system.service;

import com.z.module.system.domain.Menu;
import com.z.module.system.repository.MenuRepository;
import com.z.module.system.repository.RoleMenuRepository;
import com.z.module.system.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    private final UserRepository userRepository;

    private final RoleMenuRepository roleMenuRepository;

    private final MenuRepository menuRepository;

    public MenuService(UserRepository userRepository, RoleMenuRepository roleMenuRepository,
                       MenuRepository menuRepository) {
        this.userRepository = userRepository;
        this.roleMenuRepository = roleMenuRepository;
        this.menuRepository = menuRepository;
    }

    /**
     * @data: 2022/7/29-上午10:03
     * @User: zhaozhiwei
     * @Description: 根据登录用户做菜单树服务缓存
     */
    private static final String LOGIN_MENU_CACHE = "loginMenuCache";

    @Cacheable(cacheNames = LOGIN_MENU_CACHE)
    public List<Menu> findAllMenusByLogin(String currentLoginName) {
        List<Menu> allMenusOrderByOrdernumAsc;
        allMenusOrderByOrdernumAsc = menuRepository.findAllByOrderByOrderNumAsc();
        return allMenusOrderByOrdernumAsc;
    }
}
