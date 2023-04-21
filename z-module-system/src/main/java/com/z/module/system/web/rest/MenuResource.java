package com.z.module.system.web.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.z.module.system.domain.Menu;
import com.z.module.system.repository.MenuRepository;
import com.z.module.system.service.MenuService;
import com.z.framework.security.util.SecurityUtils;
import com.z.framework.common.web.rest.constants.ResponseCodeEnum;
import com.z.framework.common.web.rest.vm.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
@RequestMapping("/api")
@Transactional(rollbackFor = Exception.class)
public class MenuResource {

    private final Logger log = LoggerFactory.getLogger(MenuResource.class);

    private final MenuRepository menuRepository;

    public MenuResource(MenuRepository menuRepository, MenuService menuService) {
        this.menuRepository = menuRepository;
        this.menuService = menuService;
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
    public ResponseEntity<Menu> createMenu(@RequestBody Menu menu) throws URISyntaxException {

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
        return ResponseEntity
                .created(new URI("/api/menus/" + result.getId()))
                .body(result);
    }

    /**
     * {@code GET  /menus} : get all the menus.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of menus in body.
     */
    @Operation(description = "获取所有菜单")
    @GetMapping("/menus")
    public ResponseData<List<Menu>> getAllMenus(
            Pageable pageable, String key) {
        log.debug("REST request to get a page of UiComponents");

        // 根据id, 升序
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), sort);

        Page<Menu> taskPage;
        // 搜索
        if (StrUtil.isNotEmpty(key)) {
            final Menu task = new Menu();
            final List<String> cols = Collections.singletonList("name");
            //      2. 将传入属性, 填充给界面显示字段
            final Map<String, String> map = cols.stream().collect(Collectors.toMap(s -> s, key2 -> key));
            //      3. 动态构建查询条件
            BeanUtil.fillBeanWithMap(map, task, true);
            log.info("填充后对象信息 {}", task);

            //创建匹配器，即如何使用查询条件
            //构建对象
            ExampleMatcher matcher = ExampleMatcher
                    .matchingAny()
                    //改变默认字符串匹配方式：模糊查询
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                    //改变默认大小写忽略方式：忽略大小写
                    .withIgnoreCase(true)
                    //名字采用“开始匹配”的方式查询
                    .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.startsWith())
                    //忽略属性：是否关注。因为是基本类型，需要忽略掉
                    .withIgnorePaths("id");

            //创建实例
            Example<Menu> ex = Example.of(task, matcher);
            taskPage = menuRepository.findAll(ex, pageable);
        } else {
            taskPage = menuRepository.findAll(pageable);
        }

        final ResponseData<List<Menu>> listResponseData = new ResponseData<>();
        listResponseData.setData(taskPage.getContent());
        listResponseData.setCode("0");
        listResponseData.setCount(Long.valueOf(taskPage.getTotalElements()).intValue());
        return listResponseData;

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
    public List<Tree<Long>> getMenusTree() {
        log.debug("REST request to get Menus Tree");

        List<Menu> allMenusOrderByOrdernumAsc;
        final String currentLoginName = SecurityUtils.getCurrentLoginName();
        allMenusOrderByOrdernumAsc = menuService.findAllMenusByLogin(currentLoginName);

        //树形结构一些特殊配置
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        // 自定义属性名 都要默认值的
        treeNodeConfig.setWeightKey("ordernum");
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
     * 路由数据结构
     * export default [
     * {
     * path: '/example/uiexample',
     * name: 'DynamicUiExample',
     * component: UiExample,
     * meta: { authorities: [Authority.USER] },
     * },
     * {
     * path: '/example/helloworld',
     * name: 'HelloWorld',
     * component: HelloWorld,
     * meta: { authorities: [Authority.USER] },
     * },
     * ]
     */
    @GetMapping("/menus/route")
    public List<Map<String, Object>> getMenusRoute() {
        log.debug("REST request to get Menus Tree");

        final List<Menu> allMenusOrderByOrdernumAsc = menuRepository.findAllByOrderByOrderNumAsc();
        final List<Map<String, Object>> collect = allMenusOrderByOrdernumAsc
                .stream()
                //            保留url不是#的, 并且config里配置了组件的
//                .filter(menu -> !"#".equals(menu.getUrl()) && menu.getConfig().contains("component"))
                //            构建成router需要的形式
                .map(menu -> {
                    final Map<String, Object> map = new HashMap<>();
                    map.put("path", menu.getUrl());
                    final JSONObject jsonObject = JSONUtil.parseObj(menu.getConfig());
                    final String component = jsonObject.getStr("component");
                    map.put("name", component);
                    // component字段是字符串，前端需要把这个字符串转化为前端定义的组件
                    map.put("component", component);

                    final Map<String, Object> metaMap = new HashMap<>();
                    metaMap.put("menuid", menu.getId());
                    map.put("meta", metaMap);

                    return map;
                })
                .collect(Collectors.toList());

        return collect;
    }

    /**
     * {@code GET  /menus/:id} : get the "id" menu.
     *
     * @param id the id of the menu to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the menu, or with status {@code
     * 404 (Not Found)}.
     */
    @GetMapping("/menus/{id}")
    public ResponseEntity<Menu> getMenu(@PathVariable Long id) {
        log.debug("REST request to get Menu : {}", id);
        Optional<Menu> menu = menuRepository.findById(id);
        return null;
    }

    @Operation(description = "删除菜单")
    @DeleteMapping("/menus")
    public ResponseData<String> deleteMenu(@RequestBody List<Long> idList) {
        log.debug("REST request to delete Examples, ids: {}", idList);
        this.menuRepository.deleteAllByIdIn(idList);
        final ResponseData<String> responseData = new ResponseData<>();
        responseData.setCode(ResponseCodeEnum.SUCCESS.getCode());
        responseData.setMsg(ResponseCodeEnum.SUCCESS.getMsg());
        return responseData;
    }

    @Operation(description = "获取所有一级菜单")
    @GetMapping("/menus/root")
    public ResponseData<List<Menu>> getAllRootMenus() {
        log.debug("REST request to get a page of Menus");

        List<Menu> menuList = menuRepository.findAllByParentIdOrderByOrderNumAsc(0L);

        final ResponseData<List<Menu>> listResponseData = new ResponseData<>();
        listResponseData.setData(menuList);
        listResponseData.setCode("0");
        return listResponseData;
    }
}
