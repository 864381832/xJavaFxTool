package com.xwintop.xTransfer.datasource.bean;

import lombok.Data;

@Data
public class DataSourceConfigDruid implements DataSourceConfig {
    private String id;//获取数据源时唯一标识，全局不能重复
    private boolean enable = true;//是否开启

    private String url;//数据库连接url
    private String username;//用户名
    private String password;//密码
    private boolean decrypt = false;//是否使用加密
    private String publicKey;//加密公钥
    private String driverClassName = "oracle.jdbc.OracleDriver";
    private int initialSize = 5;
    private int minIdle = 5;
    private int maxActive = 30;
    private int maxWait = 60000;
    private int timeBetweenEvictionRunsMillis = 60000;
    private int minEvictableIdleTimeMillis = 30000;
    private String validationQuery = "SELECT 1 FROM DUAL";
    private boolean testWhileIdle = true;
    private boolean testOnBorrow = false;
    private boolean testOnReturn = false;
    private boolean poolPreparedStatements = true;
    private int maxPoolPreparedStatementPerConnectionSize = 20;
    private String filters = "stat,wall,slf4";
    private String connectionProperties = "druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000";
}
