package com.z.module.system.aop;

import cn.hutool.core.util.StrUtil;
import com.z.framework.security.util.SecurityUtils;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: DataRightInterceptor
 * @Package com/example/aop/DataRightInterceptor.java
 * @Description: 通过实现org.hibernate.resource.jdbc.spi.StatementInspector
 * 动态调整sql, 扩展特殊sql相关功能
 * @date 2022/5/14 下午10:22
 */
public class CustomStatementInspector implements StatementInspector {

    private static final Logger log = LoggerFactory.getLogger(CustomStatementInspector.class);

    public static final ThreadLocal<Map<String, String>> replaceTable = new ThreadLocal<>();

    /**
     * 重写StatementInspector的inspect接口，
     * 参数为hibernate处理后的原始SQL，返回值为我们修改后的SQL
     *
     * @param sql
     * @return
     */
    @Override
    public String inspect(String sql) {
        try {
            // 1. 是否登录状态, 未登录不允许查看数据
            final boolean authenticated = SecurityUtils.isAuthenticated();
            if (!authenticated) {
                // 可以显示未登录不允许查看任何数据 1<>1
                log.info("当前未登录, 看着办吧");
                return sql;
            }

            // 2. 登录状态下加载权限, 根据类型增加不同sql过滤
            log.info("原SQL：{}", sql);
            Statements statements = CCJSqlParserUtil.parseStatements(sql);
            StringBuilder sqlStringBuilder = new StringBuilder();
            int i = 0;
            for (Statement statement : statements.getStatements()) {
                if (null != statement) {
                    if (i++ > 0) {
                        sqlStringBuilder.append(';');
                    }
                    sqlStringBuilder.append(this.doSqlParser(statement));
                }
            }
            String newSql = sqlStringBuilder.toString();
            log.info("处理后SQL：{}", newSql);
            return newSql;
        } catch (Exception e) {
            log.error("组织筛选解析失败，解析SQL异常", e);
            e.printStackTrace();
        }
        return null;
    }

    private String doSqlParser(Statement statement) {
        if (statement instanceof Select) {
            this.doSelectParser(((Select) statement).getSelectBody());
        }else if (statement instanceof Delete){
            this.doDeleteParser((Delete) statement);
        }
        return statement.toString();
    }

    /**
     * @Description: 删除时做一些操作，控制误删除
     * @author: zhaozhiwei
     * @data: 2024/10/13-16:10
     * @param delete :
     * @return: void
    */

    private void doDeleteParser(Delete delete) {
        // 如何根据id判断数据是否能删除
        System.out.println("delete");
    }

    public void doSelectParser(SelectBody selectBody) {
        if (selectBody instanceof PlainSelect) {
            processPlainSelect((PlainSelect) selectBody);
        }
    }

    /**
     * @param plainSelect :
     * @data: 2022/5/14-下午11:20
     * @User: zhaozhiwei
     * @method: processPlainSelect
     * @return: void
     * @Description:
     * 在这里扩展各种的条件
     * 1. 数据权限扩展
     * 2. 动态表替换, 支持相同domain,不同表结构
     */
    protected void processPlainSelect(PlainSelect plainSelect) {
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            Table fromTable = (Table) fromItem;

            // 换表
            this.replaceTable(fromTable);

            //TODO  这里可扩展
        }
    }

    private void replaceTable(Table fromTable) {
        Map<String, String> map = replaceTable.get();

        // 必要时才会去替换
        if (!Objects.isNull(map)) {
            String oldName = map.get("oldName");
            String newName = map.get("newName");

            if (!StrUtil.isEmpty(oldName) && !StrUtil.isEmpty(newName)) {
                final String name = fromTable.getName();
                if (name.startsWith(oldName)) {
                    // 如果表名匹配，则将旧表换成新表
                    fromTable.setName(newName);
                    log.info("表替换,将 {} 替换为 {}", oldName, newName);
                }
            }
        }
    }

    /**
     * @param currentExpression :
     * @param appendExpression  :
     * @data: 2022/5/14-下午10:40
     * @User: zhaozhiwei
     * @method: builderExpression
     * @return: net.sf.jsqlparser.expression.Expression
     * @Description: 描述
     * 根据规则，　添加权限条件
     */
    protected Expression joinExpression(Expression currentExpression, Expression appendExpression) {
        final StringValue stringValue = new StringValue("1");
        final EqualsTo allCondition = new EqualsTo(stringValue, stringValue);

        if (currentExpression == null) {
            //            之前如果是全部查询(没有条件), 则返回1=1
            currentExpression = allCondition;
        }

        if (currentExpression instanceof OrExpression) {
            return new AndExpression(new Parenthesis(currentExpression), appendExpression);
        } else {
            return new AndExpression(currentExpression, appendExpression);
        }
    }
}
