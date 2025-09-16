package com.z.module.system.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.z.module.system.aop.CustomStatementInspector;
import com.z.module.system.domain.EleUnion;
import com.z.module.system.repository.EleUnionRepository;
import com.z.framework.common.service.DatabaseMetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: null.java
 * @Package com.example.service
 * @Description:
 * 统一获取基础数据的接口
 * 提供两种类型, select和tree类型封装
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonEleService {

    private static final Logger log = LoggerFactory.getLogger(CommonEleService.class);

    private final EleUnionRepository eleUnionRepository;

    public CommonEleService(EleUnionRepository eleUnionRepository, DatabaseMetaService databaseMetaService) {
        this.eleUnionRepository = eleUnionRepository;
        this.databaseMetaService = databaseMetaService;
    }

    public List<EleUnion> findAll(){
        return eleUnionRepository.findAll();
    }

    /**
     * @data: 2022/6/17-下午3:06
     * @User: zhaozhiwei
     * @method: findMapping
     * @param beanName :
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @Description: 基础要素统一规范，只能通过ele的方式来引入,不再支持bean的方式(太黑盒)
     */
    public List<Map<String, Object>> findMapping(String beanName) {
        if (StrUtil.isEmpty(beanName)) {
            throw new RuntimeException("请传入bean名");
        }

        final Object bean = SpringUtil.getBean(beanName);
        //      固定数据获取方式, findAll
        final List<Object> findAll = ReflectUtil.invoke(bean, "findAll");

        //      转换为mapping需要格式
        return findAll
            .stream()
            .map(obj -> {
                final Map<String, Object> stringObjectMap = BeanUtil.beanToMap(obj);
                //   把id作为value
                stringObjectMap.put("value", stringObjectMap.get("id"));
                //   用户表没有name字段, 特殊处理下
                if (Objects.isNull(stringObjectMap.get("name"))) {
                    stringObjectMap.put("name", stringObjectMap.get("firstName"));
                }
                return stringObjectMap;
            })
            .collect(Collectors.toList());
    }

    /**
     * @data: 2022/6/17-下午3:00
     * @User: zhaozhiwei
     * @method: findTreeMapping
     * @param beanName :
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @Description: 转成树形结构
     */
    public List<Map<String, Object>> findTreeMapping(String beanName) {
        if (StrUtil.isEmpty(beanName)) {
            throw new RuntimeException("请传入bean名");
        }

        final Object bean = SpringUtil.getBean(beanName);
        //      固定数据获取方式, findAll
        final List<Object> findAll = ReflectUtil.invoke(bean, "findAll");

        //      转换为mapping需要格式
        return findAll
            .stream()
            .map(obj -> {
                final Map<String, Object> stringObjectMap = BeanUtil.beanToMap(obj);
                //                    把id作为value
                stringObjectMap.put("value", stringObjectMap.get("id"));
                return stringObjectMap;
            })
            .collect(Collectors.toList());
    }

    /**
     * @data: 2022/6/17-下午3:09
     * @User: zhaozhiwei
     * @method: findAllEleCategory

     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @Description: 获取所有的基础要素分类
     * 一部分通过union表获取
     * 另外的需要查询ele_开头的表名进行收集
     */
    public List<Map<String, Object>> findAllEleCategory() {
        final List<Map<String, Object>> maps = new ArrayList<>();
        //1. 查询所有ele_union中数据, 然后提取eleCatcode/name
        final List<EleUnion> eleUnionAll = eleUnionRepository.findAll();
        // 去重
        Set<EleUnion> eleUnionTreeSet = new TreeSet<>(Comparator.comparing(EleUnion::getEleCatCode));
        eleUnionTreeSet.addAll(eleUnionAll);

        // 每种数据，只保留一条作为左侧数据源
        eleUnionTreeSet.forEach(eleUnion -> maps.add(BeanUtil.beanToMap(eleUnion)));

        //2. 查询数据库中除 ele_union外所有ele_开头的表, 分别查询其中eleCatcode/name, 只需每个查一条就行
        final List<String> allTableNamesStartWithEle = this.findAllTableNamesStartWithEle();
        for (String tableName : allTableNamesStartWithEle) {
            if ("ele_union".equals(tableName) || "ele_leavetype".equals(tableName)) {
                continue;
            }

            Map<String, String> map = new HashMap<>();
            map.put("oldName", "ele_union");
            map.put("newName", tableName);

            CustomStatementInspector.replaceTable.set(map);
            // 这里要注意： 如果使用同一个映射对象, 虽然修改了表,但是唯一 *id不能重复* , 否则因为缓存会被替换回去
            // 比如在EleUnion上述数据中存在1, 这里替换表虽然也查到id为1的数据, 但是会被上边替换, 使用缓存里的数据
            final List<EleUnion> all1 = eleUnionRepository.findAll();
            // 换完以后清理
            CustomStatementInspector.replaceTable.remove();

            if (!all1.isEmpty()) {
                maps.add(BeanUtil.beanToMap(all1.get(0)));
            }
        }
        return maps;
    }

    private final DatabaseMetaService databaseMetaService;

    public List<String> findAllTableNamesStartWithEle() {
        final List<String> tables = databaseMetaService.getTables();
        return tables.stream().filter(s -> s.startsWith("ele_")).collect(Collectors.toList());
    }

    /**
     * @data: 2022/6/20-下午11:04
     * @User: zhaozhiwei
     * @method: findElementInfoByEleCatCode
     * @param eleCatCode :
     * @return: java.util.List<com.example.domain.EleUnion>
     * @Description: 根据eleCatCode获取完整的基础信息列表
     */
    public List<EleUnion> findElementInfoByEleCatCode(String eleCatCode) {
        //1. eleCatCode直接存在于ele_union表
        List<EleUnion> byEleCatCode;

        byEleCatCode = eleUnionRepository.findByEleCatCode(eleCatCode);

        //2. eleCatCode是表明的一部分如 ele_eleCatCode(约定)
        if (byEleCatCode.isEmpty()) {
            //            如果汇总表没有则查询单表
            final List<String> allTableNamesStartWithEle = this.findAllTableNamesStartWithEle();
            //            判断表存在
            if (allTableNamesStartWithEle.contains(eleCatCode)) {
                Map<String, String> map = new HashMap<>();
                map.put("oldName", "ele_union");
                map.put("newName", "ele_" + eleCatCode);
                CustomStatementInspector.replaceTable.set(map);

                byEleCatCode = eleUnionRepository.findByEleCatCode(eleCatCode);

                // 用完删掉好习惯
                CustomStatementInspector.replaceTable.remove();
            }
        }

        return byEleCatCode;
    }

    public List<Tree<Long>> transToMapping(String eleCatCode) {
        // 1. 根据eleCatCode获取基础数据信息
        final List<EleUnion> elementInfoByEleCatCode = this.findElementInfoByEleCatCode(String.valueOf(eleCatCode));
        //树形结构一些特殊配置
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        // 自定义属性名 都要默认值的
        treeNodeConfig.setWeightKey("eleCode");
        //        最大递归深度
        treeNodeConfig.setDeep(3);

        //转换器

        return TreeUtil.build(
            elementInfoByEleCatCode,
            0L,
            treeNodeConfig,
            (eleUnion, tree) -> {
                tree.setId(eleUnion.getId());
                tree.setParentId(Long.valueOf(eleUnion.getParentId()));
                //              tree.setWeight(treeNode.getWeight());
                tree.setName(eleUnion.getEleName());
                // 属性扩展, 只显示界面展示需要的属性即可
                tree.putExtra("label", eleUnion.getEleName());
                tree.putExtra("name", eleUnion.getEleName());
                tree.putExtra("code", eleUnion.getEleCode());
                tree.putExtra("value", eleUnion.getEleCode());
            }
        );
    }
}
