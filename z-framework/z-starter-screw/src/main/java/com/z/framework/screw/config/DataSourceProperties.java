package com.z.framework.screw.config;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.sql.DataSource;

/**
 * @Title: DataSourceProperties
 * @Package com/z/framework/screw/config/DataSourceProperties.java
 * @Description: 存储数据源配置信息,
 * {@see com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty}
 * @author zhaozhiwei
 * @date 2023/6/12 下午4:47
 * @version V1.0
 */
@ConfigurationProperties("spring.datasource")
@Slf4j
@Data
@ToString
public class DataSourceProperties {
    private String poolName;
    private Class<? extends DataSource> type;
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String jndiName;
    private Boolean seata = true;
    private Boolean p6spy = true;
    private Boolean lazy;
    private String publicKey;
}
