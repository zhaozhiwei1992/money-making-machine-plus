package com.z.framework.common.repository;

import com.z.framework.common.service.DatabaseMetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: CommonSqlRepository
 * @Package com/example/repository/CommonSqlRepository.java
 * @Description: 纯sql方式的数据操作集合
 * @date 2022/6/17 上午10:45
 */
@Repository
public class CommonSqlRepository extends JdbcDaoSupport {

    private static final Logger log = LoggerFactory.getLogger(CommonSqlRepository.class);

    public CommonSqlRepository(JdbcTemplate jdbcTemplate, DatabaseMetaService databaseMetaService) {
        this.databaseMetaService = databaseMetaService;
        super.setJdbcTemplate(jdbcTemplate);
    }

    /**
     * @param sql
     * @param o（不可传null）
     * @return
     */
    public List queryForList(String sql, Object o) {
        return this.getJdbcTemplate().queryForList(sql, o.getClass());
    }

    public List queryForList(String sql) {
        return this.getJdbcTemplate().queryForList(sql);
    }

    public int update(String sql) {
        return this.getJdbcTemplate().update(sql);
    }

    public void execute(String sql) {
        this.getJdbcTemplate().execute(sql);
    }

    public int queryForInt(String sql) {
        //single query expected.
        Number number = this.getJdbcTemplate().queryForObject(sql, Integer.class);
        return number != null ? number.intValue() : 0;
    }

    public int queryForTotal(String sql) {
        List list = this.queryForList(sql);
        return list != null ? list.size() : 0;
    }

    private final DatabaseMetaService databaseMetaService;

    public List<String> findAllColsByTableName(String tableName) {
        return databaseMetaService.getTableColumnsMap().get(tableName);
    }

    public void insertDatas(String tableCode, List<Map<String, Object>> lists) {
        List<String> columns = findAllColsByTableName(tableCode);

        StringBuilder vchsql = new StringBuilder();
        StringBuilder valuesql = new StringBuilder();
        vchsql.append("insert into ").append(tableCode).append("(");
        List list = new ArrayList();
        for (String i : columns) {
            vchsql.append(i).append(",");
            valuesql.append("?,");
        }
        vchsql.delete(vchsql.length() - 1, vchsql.length());
        vchsql.append(")values(");
        vchsql.append(valuesql.delete(valuesql.length() - 1, valuesql.length()));
        vchsql.append(")");
        for (Map id : lists) {
            List listDto = new ArrayList();
            for (String key : columns) {
                Object o = id.get(key.toLowerCase());
                if (o instanceof Enum) {
                    listDto.add(o.toString());
                } else {
                    listDto.add(o);
                }
            }
            list.add(listDto);
        }
        try {
            this.getJdbcTemplate()
                    .batchUpdate(
                            vchsql.toString(),
                            new BatchPreparedStatementSetter() {
                                @Override
                                public int getBatchSize() {
                                    return list.size();
                                }

                                @Override
                                public void setValues(PreparedStatement ps, int i) throws SQLException {
                                    List params = (List) list.get(i);
                                    for (int j = 0; j < params.size(); j++) {
                                        Object o = params.get(j);
                                        setValues2Type(ps, o, j);
                                    }
                                }
                            }
                    );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @param columns   : 要更新的列, 不指定则全列更新
     * @param datas     : 待更新的数据
     * @param pkcolumn  : 主键, 默认为 id
     * @param tableName :
     * @data: 2022/6/26-下午4:19
     * @User: zhaozhiwei
     * @method: updateAllByPK
     * @return: int
     * @Description: 描述
     */
    public int updateAllByPK(String tableName, String pkcolumn, Collection<? extends Map> datas,
                             Collection<String> columns) {
        StringBuilder sb = new StringBuilder("update ");
        sb.append(tableName);
        List<List> updateParamArrList = new ArrayList<>();
        int index;
        sb.append(" set ");
        // 没有指定就更新所有列
        if (Objects.isNull(columns) || columns.size() < 1) {
            columns = findAllColsByTableName(tableName);
        }
        for (String colName : columns) {
            if (!pkcolumn.equalsIgnoreCase(colName)) {
                sb.append(" ").append(colName).append("=?,");
                index = 0;
                for (Map m : datas) {
                    // 多条update语句, arr就有多条记录
                    if (updateParamArrList.size() == index) {
                        updateParamArrList.add(new ArrayList());
                    }
                    // 每个update语句对应的更新数据的参数集合
                    List updateParamList = updateParamArrList.get(index);
                    Object o = m.get(colName.toLowerCase());
                    if (o instanceof Enum) {
                        updateParamList.add(o.toString());
                    } else {
                        // polardb时，字段类型为number时，不能插入空串
                        if (o != null && o.equals("")) {
                            o = null;
                        }
                        updateParamList.add(o);
                    }
                    index++;
                }
            }
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append(" where ").append(pkcolumn).append("=? ");
        index = 0;
        for (Map m : datas) {
            List l = updateParamArrList.get(index++);
            l.add(m.get(pkcolumn));
        }

        log.debug("sql:{}  :: {}", sb, updateParamArrList);
        try {
            this.getJdbcTemplate()
                    .batchUpdate(
                            sb.toString(),
                            new BatchPreparedStatementSetter() {
                                public int getBatchSize() {
                                    return updateParamArrList.size();
                                }

                                public void setValues(PreparedStatement ps, int i) throws SQLException {
                                    List params = updateParamArrList.get(i);
                                    for (int j = 0; j < params.size(); j++) {
                                        Object o = params.get(j);
                                        setValues2Type(ps, o, j);
                                    }
                                }
                            }
                    );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return updateParamArrList.size();
    }

    public void deleteBySql(String tablecode, String whereSql) {
        String delSql = "delete from " + tablecode + (whereSql != null && !"".equals(whereSql) ? whereSql : "");
        this.getJdbcTemplate().execute(delSql);
    }

    private void setValues2Type(PreparedStatement ps, Object o, int index) throws SQLException {
        if (null == o) {
            ps.setNull(index + 1, 0);
        } else if (o instanceof String) {
            ps.setString(index + 1, String.valueOf(o));
        } else if (o instanceof Date) {
            ps.setDate(index + 1, (java.sql.Date) o);
        } else if (o instanceof Integer) {
            ps.setInt(index + 1, (Integer) o);
        } else if (o instanceof BigDecimal) {
            ps.setBigDecimal(index + 1, (BigDecimal) o);
        } else if (o instanceof Number) {
            ps.setBigDecimal(index + 1, new BigDecimal(o.toString()));
        } else if (o instanceof Clob) {
            ps.setClob(index + 1, (Clob) o);
        } else if (o instanceof Blob) {
            ps.setBlob(index + 1, (Blob) o);
        } else {
            throw new RuntimeException("参数" + index + "类型未知：" + o.getClass());
        }
    }

    public String parseInSql(String colcode, Collection<String> guids) {
        StringBuilder vchsql = new StringBuilder();
        vchsql.append(colcode).append(" in( ");
        int index = 0;
        Iterator iterator = guids.iterator();
        while (iterator.hasNext()) {
            index++;
            vchsql.append("'").append(iterator.next()).append("'");
            if (index == 998) {
                index = 0;
                vchsql.append(") or ").append(colcode).append(" in (");
            } else {
                if (iterator.hasNext()) {
                    vchsql.append(",");
                }
            }
        }
        return vchsql.append(")").toString();
    }
}
