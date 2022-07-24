package dev.hirpc.plugin.db;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: JT
 * @date: 2019/11/16
 * @Title:
 */
@Setter
@Getter
public class DBConfig extends DBBase {

    private int initSize = 5;    // 连接池初始化连接数
    private int minIdle = 5;     // 连接池最小等待连接数
    private int maxActive = 50;  // 连接池最大连接数
    private int maxWait = 60000;  // 获取连接等待超时的时间
    private int timeBetweenEvictionRunsMillis = 60000; // 间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒

    /*
    一个连接在池中最小生存的时间，单位是毫秒
     */
    private int minEvictableIdleTimeMillis = 300000;
    private String validationQuery = "SELECT 1 ";
    private boolean isTestWhileIdle = true;
    private boolean isTestOnBorrow = true;
    private boolean isTestOnReturn = false;

    /*
    打开PSCache，并且指定每个连接上PSCache的大小
     */
    private boolean isPoolPreparedStatements = true;
    private int maxPoolPreparedStatementPerConnectionSize = 20;

    /*
    配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
     */
    private String filters = "stat,wall,log4j";

    /*
    通过connectProperties属性来打开mergeSql功能；慢SQL记录
     */
    private String connectionProperties = "druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000";

    /*
    合并多个DruidDataSource的监控数据
     */
    private boolean isUseGlobalDataSourceStat = true;

}
