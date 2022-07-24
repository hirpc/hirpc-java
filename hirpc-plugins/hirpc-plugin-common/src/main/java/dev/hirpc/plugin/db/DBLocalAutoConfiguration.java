package dev.hirpc.plugin.db;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * @author JT
 * @date 2022/7/10
 * {@code description}
 */
@Slf4j
@Configuration
@AutoConfigureAfter(DBRemoteAutoConfiguration.class)
@ConditionalOnClass(DruidDataSource.class)
@ConditionalOnMissingBean(DruidDataSource.class)
@ConditionalOnResource(resources = {"config.properties"})
@PropertySource(value = {"classpath:config.properties"})
public class DBLocalAutoConfiguration {

    @Resource
    private Environment environment;

    @Bean
    public DBConfig localDBConfig() {
        String localDBConfigTemplate = "hirpc.datasource.{}";
        DBConfig localDBConfig = new DBConfig();
        localDBConfig.setName(this.environment.getProperty(StrUtil.format(localDBConfigTemplate, "name"), localDBConfig.getName()));
        localDBConfig.setUrl(this.environment.getProperty(StrUtil.format(localDBConfigTemplate, "url"), localDBConfig.getUrl()));
        localDBConfig.setUsername(this.environment.getProperty(StrUtil.format(localDBConfigTemplate, "username"), localDBConfig.getUsername()));
        localDBConfig.setPassword(this.environment.getProperty(StrUtil.format(localDBConfigTemplate, "password"), localDBConfig.getPassword()));
        localDBConfig.setDriverClassName(this.environment.getProperty(StrUtil.format(localDBConfigTemplate, "driverClassName"), localDBConfig.getDriverClassName()));
        localDBConfig.setInitSize(this.environment.getProperty(StrUtil.format(localDBConfigTemplate, "initSize"), Integer.class, localDBConfig.getInitSize()));
        localDBConfig.setMinIdle(this.environment.getProperty(StrUtil.format(localDBConfigTemplate, "minIdle"), Integer.class, localDBConfig.getMinIdle()));
        localDBConfig.setMaxActive(this.environment.getProperty(StrUtil.format(localDBConfigTemplate, "maxActive"), Integer.class, localDBConfig.getMaxActive()));
        localDBConfig.setMaxWait(this.environment.getProperty(StrUtil.format(localDBConfigTemplate, "timeBetweenEvictionRunsMillis"), Integer.class, localDBConfig.getTimeBetweenEvictionRunsMillis()));
        localDBConfig.setMinEvictableIdleTimeMillis(this.environment.getProperty(StrUtil.format(localDBConfigTemplate, "minEvictableIdleTimeMillis"), Integer.class, localDBConfig.getMinEvictableIdleTimeMillis()));
        localDBConfig.setValidationQuery(this.environment.getProperty(StrUtil.format(localDBConfigTemplate, "validationQuery"), localDBConfig.getValidationQuery()));
        localDBConfig.setTestWhileIdle(this.environment.getProperty(StrUtil.format(localDBConfigTemplate, "isTestWhileIdle"), Boolean.class, localDBConfig.isTestWhileIdle()));
        localDBConfig.setTestOnBorrow(this.environment.getProperty(StrUtil.format(localDBConfigTemplate, "isTestOnBorrow"), Boolean.class, localDBConfig.isTestOnBorrow()));
        localDBConfig.setTestOnReturn(this.environment.getProperty(StrUtil.format(localDBConfigTemplate, "isTestOnReturn"), Boolean.class, localDBConfig.isTestOnReturn()));
        localDBConfig.setPoolPreparedStatements(this.environment.getProperty(StrUtil.format(localDBConfigTemplate, "isPoolPreparedStatements"), Boolean.class, localDBConfig.isPoolPreparedStatements()));
        localDBConfig.setMaxPoolPreparedStatementPerConnectionSize(this.environment.getProperty(StrUtil.format(localDBConfigTemplate, "maxPoolPreparedStatementPerConnectionSize"), Integer.class, localDBConfig.getMaxPoolPreparedStatementPerConnectionSize()));
        localDBConfig.setFilters(this.environment.getProperty(StrUtil.format(localDBConfigTemplate, "filters"), localDBConfig.getFilters()));
        localDBConfig.setConnectionProperties(this.environment.getProperty(StrUtil.format(localDBConfigTemplate, "connectionProperties"), localDBConfig.getConnectionProperties()));
        localDBConfig.setUseGlobalDataSourceStat(this.environment.getProperty(StrUtil.format(localDBConfigTemplate, "isUseGlobalDataSourceStat"), Boolean.class, localDBConfig.isUseGlobalDataSourceStat()));
        return localDBConfig;
    }



    @ConditionalOnBean(name = "localDBConfig")
    @Bean
    public DruidDataSource dataSource() {
        DBConfig localDBConfig = localDBConfig();
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(localDBConfig.getDriverClassName());
        druidDataSource.setUrl(localDBConfig.getUrl());
        druidDataSource.setUsername(localDBConfig.getUsername());
        druidDataSource.setPassword(localDBConfig.getPassword());
        druidDataSource.setInitialSize(localDBConfig.getInitSize());
        druidDataSource.setMinIdle(localDBConfig.getMinIdle());
        druidDataSource.setMaxActive(localDBConfig.getMaxActive());
        druidDataSource.setTimeBetweenEvictionRunsMillis(localDBConfig.getTimeBetweenEvictionRunsMillis());
        druidDataSource.setMinEvictableIdleTimeMillis(localDBConfig.getMinEvictableIdleTimeMillis());
        druidDataSource.setValidationQuery(localDBConfig.getValidationQuery());
        druidDataSource.setTestWhileIdle(localDBConfig.isTestWhileIdle());
        druidDataSource.setTestOnBorrow(localDBConfig.isTestOnBorrow());
        druidDataSource.setTestOnReturn(localDBConfig.isTestOnReturn());
        druidDataSource.setPoolPreparedStatements(localDBConfig.isPoolPreparedStatements());
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(localDBConfig.getMaxPoolPreparedStatementPerConnectionSize());
        druidDataSource.setConnectionProperties(localDBConfig.getConnectionProperties());
        druidDataSource.setUseGlobalDataSourceStat(localDBConfig.isUseGlobalDataSourceStat());
        log.info("[Mysql配置] -  Druid数据源加载完成！ 使用配置[Local]");
        return druidDataSource;
    }



}
