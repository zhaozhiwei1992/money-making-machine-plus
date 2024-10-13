package com.z.module.system.web.rest;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.z.framework.common.config.MenuTypeEnum;
import com.z.framework.common.service.MenuRouterExtService;
import com.z.framework.common.util.GenericTreeBuilderUtil;
import com.z.framework.security.util.SecurityUtils;
import com.z.module.system.domain.Menu;
import com.z.module.system.domain.RoleMenu;
import com.z.module.system.domain.User;
import com.z.module.system.domain.UserAuthority;
import com.z.module.system.repository.MenuRepository;
import com.z.module.system.repository.RoleMenuRepository;
import com.z.module.system.repository.UserAuthorityRepository;
import com.z.module.system.repository.UserRepository;
import com.z.module.system.service.MenuService;
import com.z.module.system.web.mapper.MenuSelectMapper;
import com.z.module.system.web.vo.MenuVO;
import com.z.framework.common.web.vo.SelectOptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: null.java
 * @Package com.longtu.web.resource
 * @Description: TODO
 * @date 2022/7/13 上午11:31
 */
@Tag(name = "菜单维护API")
@RestController
@RequestMapping("/system")
@Transactional(rollbackFor = Exception.class)
public class MenuResource {

    private final Logger log = LoggerFactory.getLogger(MenuResource.class);

    private final MenuRepository menuRepository;

    private final UserAuthorityRepository userAuthorityRepository;

    private final UserRepository userRepository;

    private final RoleMenuRepository roleMenuRepository;

    public MenuResource(MenuRepository menuRepository, UserAuthorityRepository userAuthorityRepository, UserRepository userRepository, RoleMenuRepository roleMenuRepository, MenuService menuService, MenuSelectMapper menuSelectMapper) {
        this.menuRepository = menuRepository;
        this.userAuthorityRepository = userAuthorityRepository;
        this.userRepository = userRepository;
        this.roleMenuRepository = roleMenuRepository;
        this.menuService = menuService;
        this.menuSelectMapper = menuSelectMapper;
    }

    /**
     * {@code POST  /menus} : Create a new menu.
     *
     * @param menu the menu to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new menu, or with
     * status {@code 400 (Bad Request)} if the menu has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(description = "新增菜单")
    @PostMapping("/menus")
    @PreAuthorize("hasAuthority('system:menu:add')")
    public Menu createMenu(@RequestBody Menu menu) throws URISyntaxException {

        ExampleMatcher matcher = ExampleMatcher.matching();
        final Menu filterObj = new Menu();
        if (Objects.isNull(menu.getId())) {
            menu.setEnabled(true);
            final Example<Menu> of = Example.of(filterObj, matcher);
            final long count = menuRepository.count(of);
            menu.setOrderNum(Integer.parseInt(String.valueOf(count + 1)));
        }

        if (Objects.isNull(menu.getParentId())) {
            menu.setParentId(0L);
        }

        Menu result = menuRepository.save(menu);
        return result;
    }

    /**
     * {@code GET  /menus} : get all the menus.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of menus in body.
     */
    @Operation(description = "获取所有菜单")
    @GetMapping("/menus")
    @PreAuthorize("hasAuthority('system:menu:view')")
    public HashMap<String, Object> getAllMenus(
            Pageable pageable, Menu menu) {
        log.debug("REST request to get a page of UiComponents");

        //创建匹配器，即如何使用查询条件
        //构建对象
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                //改变默认字符串匹配方式：模糊查询
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                //改变默认大小写忽略方式：忽略大小写
                .withIgnoreCase(true)
                .withIgnoreNullValues()
                //名字采用“开始匹配”的方式查询
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.startsWith())
                //忽略属性：是否关注。因为是基本类型，需要忽略掉
                .withIgnorePaths("id", "createdDate", "lastModifiedDate");

        //创建实例
        Example<Menu> ex = Example.of(menu, matcher);
        final List<Menu> allMenusOrderByOrdernumAsc = menuRepository.findAll(ex);
        final List<MenuVO> collect = allMenusOrderByOrdernumAsc.stream().map(m -> {
            final MenuVO menuDTO = new MenuVO();
            BeanUtils.copyProperties(m, menuDTO);
            return menuDTO;
        }).collect(Collectors.toList());
        final GenericTreeBuilderUtil<MenuVO> menuDTOGenericTreeBuilderUtil = new GenericTreeBuilderUtil<>(MenuVO.class);
        final List<MenuVO> menuDTOS = menuDTOGenericTreeBuilderUtil.buildTree(collect);

        //菜单属性转换成 下划线 再给前端, mapstruct? 直接采用Jackson的JsonNaming注解搞了先
        return new HashMap<String, Object>() {{
            put("list", menuDTOS);
            put("total", 0);
        }};

    }

    private final MenuService menuService;

    /**
     * @data: 2022/7/28-上午11:35
     * @User: zhaozhiwei
     * @method: getMenusTree
     * @return: java.util.List<cn.hutool.core.lang.tree.Tree < java.lang.Long>>
     * @Description: 树形菜单, 给layui展现, 或者配置页面挂接菜单使用
     * "title": "营业厅",
     * "icon": "fa fa-desktop ",
     * // 是否默认展开
     * "spread": false,
     * "href": " ",
     * "children": [　　//二级菜单 children
     * {
     * "title": "指标",
     * "icon": "fa fa-flag-checkered",
     * "href": "${pageContext.request.contextPath}/static/modules/main/setting/userList.jsp",
     * "spread": false
     * },
     */
    @Operation(description = "获取菜单树")
    @GetMapping("/menus/tree")
    @PreAuthorize("hasAuthority('system:menu:view')")
    public List<Tree<Long>> getMenusTree() {
        log.debug("REST request to get Menus Tree");

        List<Menu> allMenusOrderByOrdernumAsc;
        final String currentLoginName = SecurityUtils.getCurrentLoginName();
        allMenusOrderByOrdernumAsc = menuService.findAllMenusByLogin(currentLoginName);

        //树形结构一些特殊配置
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        // 自定义属性名 都要默认值的
        treeNodeConfig.setWeightKey("orderNum");
        treeNodeConfig.setDeep(3);

        //转换器
        List<Tree<Long>> treeNodes = TreeUtil.build(
                allMenusOrderByOrdernumAsc,
                0L,
                treeNodeConfig,
                (menuObj, tree) -> {
                    tree.setId(menuObj.getId());
                    tree.setParentId(menuObj.getParentId());
                    // tree.setWeight(treeNode.getWeight());
                    tree.setName(menuObj.getName());
                    // 属性扩展, 只显示界面展示需要的属性即可
                    if ("#".equals(menuObj.getUrl())) {
                        tree.putExtra("icon", menuObj.getIconCls());
                    }
                    tree.putExtra("href", menuObj.getUrl());
                    tree.putExtra("index", menuObj.getUrl());
                    tree.putExtra("spread", false);
                    tree.putExtra("title", menuObj.getName());
                    // 防止index相同导致el-menu全部展开
                    if (menuObj.getParentId() == 0) {
                        tree.putExtra("index", menuObj.getId());
                    }
                    tree.putExtra("ordernum", menuObj.getOrderNum());
                }
        );

        //      children默认给空, 防止前端解析报错
        for (Tree<Long> treeNode : treeNodes) {
            if (Objects.isNull(treeNode.getChildren())) {
                treeNode.setChildren(Collections.emptyList());
            }
        }

        log.info("左侧树构建: {}", treeNodes);
        return treeNodes;
    }

    /**
     * @data: 2022/5/8-下午7:44
     * @User: zhaozhiwei
     * @method: getMenusRoute
     * @return: java.util.List<cn.hutool.core.lang.tree.Tree < java.lang.Long>>
     * @Description: 根据菜单动态产生路由, 菜单表自己加的菜单就不用手动加路由了
     * <p>
     * declare interface AppCustomRouteRecordRaw extends Omit<RouteRecordRaw, 'meta'> {
     * name: string
     * meta: RouteMeta
     * component: string
     * path: string
     * redirect: string
     * children?: AppCustomRouteRecordRaw[]
     * }
     * 路由数据结构, 参考前端架子里的mock/role/index.ts  adminList
     * {
     * path: '/example',
     * component: '#',
     * redirect: '/example/example-dialog',
     * name: 'Example',
     * meta: {
     * title: 'router.example',
     * icon: 'ep:management',
     * alwaysShow: true
     * },
     * children: [
     * {
     * path: 'example-dialog',
     * component: 'views/Example/Dialog/ExampleDialog',
     * name: 'ExampleDialog',
     * meta: {
     * title: 'router.exampleDialog'
     * }
     * },
     * ...
     */
    @GetMapping("/menus/route")
    @PreAuthorize("hasAuthority('system:menu:view')")
    public List<Tree<Long>> getMenusRoute() {
        log.debug("REST request to get Menus Tree");

        final String currentLoginName = SecurityUtils.getCurrentLoginName();
        List<Menu> allMenusOrderByOrdernumAsc;
        if("admin".equals(currentLoginName)){
                    allMenusOrderByOrdernumAsc = menuRepository.findAllByMenuTypeInOrderByOrderNumAsc(Arrays.asList(MenuTypeEnum.DICT.getCode(),
                            MenuTypeEnum.MENU.getCode()));
        }else{
            // 获取权限下的菜单
            final Optional<User> oneByLogin = userRepository.findOneByLogin(currentLoginName);
            final User user = oneByLogin.get();
            final List<UserAuthority> allByUserId = userAuthorityRepository.findAllByUserId(user.getId());
            final List<RoleMenu> roleMenuList = roleMenuRepository.findByRoleIdIn(allByUserId.stream().map(UserAuthority::getRoleId).collect(Collectors.toList()));
            allMenusOrderByOrdernumAsc = menuRepository.findAllByIdInAndMenuTypeInOrderByOrderNumAsc(roleMenuList.stream().map(RoleMenu::getMenuId).collect(Collectors.toList()), Arrays.asList(MenuTypeEnum.DICT.getCode(),
                    MenuTypeEnum.MENU.getCode()));
        }

        //树形结构一些特殊配置
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        // 自定义属性名 都要默认值的
        treeNodeConfig.setWeightKey("orderNum");
        treeNodeConfig.setDeep(3);

        //转换器
        List<Tree<Long>> treeNodes = TreeUtil.build(
                allMenusOrderByOrdernumAsc,
                0L,
                treeNodeConfig,
                (menuObj, tree) -> {
                    tree.setId(menuObj.getId());
                    tree.setParentId(menuObj.getParentId());
                    // 路由的name千万别重复,不然坑爹
                    // fixed: report/views.vue使用iframe跳转不同的报表页面, 组建是相同的直接路由跳转异常, 用id更保险
//                    tree.setName(menuObj.getComponent().equals("#")? menuObj.getUrl() : menuObj.getComponent());
                    tree.setName(menuObj.getId().toString());
                    // 属性扩展, 只显示界面展示需要的属性即可
                    tree.putExtra("path", menuObj.getUrl());
                    tree.putExtra("component", menuObj.getComponent());
                    final Map<String, Object> metaMap = new HashMap<>();
                    metaMap.put("menuId", menuObj.getId());
                    metaMap.put("title", menuObj.getName());
                    metaMap.put("icon", menuObj.getIconCls());
                    tree.putExtra("meta", metaMap);
                }
        );

        //      children默认给空, 防止前端解析报错
        for (Tree<Long> treeNode : treeNodes) {
            if (Objects.isNull(treeNode.getChildren())) {
                treeNode.setChildren(Collections.emptyList());
            }
        }

        // 各个模块自行扩展静态路由, 通过接口
        final Map<String, MenuRouterExtService> extMenuRouter = SpringUtil.getBeansOfType(MenuRouterExtService.class);
        for (Map.Entry<String, MenuRouterExtService> extServiceEntry : extMenuRouter.entrySet()) {
            final MenuRouterExtService menuRouterBean = extServiceEntry.getValue();
            treeNodes.addAll(menuRouterBean.customTreeNodeList());
        }

        return treeNodes;
    }

    /**
     * {@code GET  /menus/:id} : get the "id" menu.
     *
     * @param id the id of the menu to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the menu, or with status {@code
     * 404 (Not Found)}.
     */
    @GetMapping("/menus/{id}")
    @PreAuthorize("hasAuthority('system:menu:view')")
    public Menu getMenu(@PathVariable Long id) {
        log.debug("REST request to get Menu : {}", id);
        Optional<Menu> menu = menuRepository.findById(id);
        return menu.get();
    }

    @Operation(description = "删除菜单")
    @DeleteMapping("/menus")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    public String deleteMenu(@RequestBody List<Long> idList) {
        log.debug("REST request to delete Examples, ids: {}", idList);
        this.menuRepository.deleteAllByIdIn(idList);
        return "success";
    }

    @Operation(description = "获取所有一级菜单")
    @GetMapping("/menus/root")
    @PreAuthorize("hasAuthority('system:menu:view')")
    public List<Menu> getAllRootMenus() {
        log.debug("REST request to get a page of Menus");

        List<Menu> menuList = menuRepository.findAllByParentIdOrderByOrderNumAsc(0L);

        return menuList;
    }

    private final MenuSelectMapper menuSelectMapper;

    @Operation(description = "获取菜单树select")
    @GetMapping("/menus/select")
    @PreAuthorize("hasAuthority('system:menu:view')")
    public List<SelectOptionVO> getPositionsSelect() {
        log.debug("REST request to get Menu Select");

        final List<Menu> menuList = menuRepository.findAll();
        final List<SelectOptionVO> convert = menuSelectMapper.convert(menuList);
        final GenericTreeBuilderUtil<SelectOptionVO> genericTreeBuilderUtil = new GenericTreeBuilderUtil<>(SelectOptionVO.class);
        final List<SelectOptionVO> list = genericTreeBuilderUtil.buildTree(convert);
        log.info("左侧树构建: {}", list);
        return list;
    }
}
