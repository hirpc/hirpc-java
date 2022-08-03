package dev.hirpc.plugin.db;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import dev.hirpc.plugin.BeanLoadAfterContainer;
import dev.hirpc.plugin.consul.ConsulAutoConfiguration;
import dev.hirpc.plugin.consul.ConsulConfigProperty;
import dev.hirpc.plugin.consul.ConsulConfigResource;
import dev.hirpc.plugin.utils.ConsulConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author JT
 * @date 2022/7/16
 * @desc
 */
@Slf4j
@Configuration
@ConditionalOnClass(DruidDataSource.class)
@ConditionalOnBean(ConsulConfigProperty.class)
@AutoConfigureBefore(DBLocalAutoConfiguration.class)
@AutoConfigureAfter(ConsulAutoConfiguration.class)
public class DBRemoteAutoConfiguration {

    private final Map<String, String> configNameMapper = new HashMap<>();

    @Resource(name = "consulConfigResource")
    private ConsulConfigResource consulConfigResource;

    @Resource
    private ConsulConfigProperty consulConfigProperty;

    @PostConstruct
    public void initConfigNameMapper() {
        // Key: 字段名称 value: Consul - Mysql配置 配置名称
        this.configNameMapper.put("username", "username");
        this.configNameMapper.put("password", "password");
        this.configNameMapper.put("dbname", "dbname");
        this.configNameMapper.put("url", "url");
        this.configNameMapper.put("address", "address");
        this.configNameMapper.put("port", "port");
        this.configNameMapper.put("maxActive", "max_open_conns");
    }

    @Bean
    public DBConfig remoteDBConfig() {
        // 载入consul远程mysql配置
        log.debug("[Mysql配置] - [Consul配置] - 开始载入配置中心Mysql配置...");
        String res = ConsulConfigUtil.getConfigWithAcl(consulConfigProperty, consulConfigResource.getRemoteMysqlConfigPath());
        Assert.notNull(res, "[Mysql配置] - [Consul配置] - 未获取到远程配置!");
        JSONObject dbConfigObj = JSON.parseObject(res);
        String address = dbConfigObj.getString("address");
        String port = dbConfigObj.getString("port");
        String dbname = dbConfigObj.getString("dbname");
        Assert.hasText(address, "[Mysql配置] - [Consul配置] - 未获取到地址\"address\"!");
        Assert.hasText(port, "[Mysql配置] - [Consul配置] - 未获取到端口\"port\"!");
        Assert.hasText(dbname, "[Mysql配置] - [Consul配置] - 未获取到数据库名称\"dbname\"!");
        String url = StrUtil.format(
                "jdbc:mysql://{}:{}/{}?useSSL=false&serverTimezone=GMT%2b8&autoReconnect=true",
                address, port, dbname
        );
        dbConfigObj.put("url", url);


        // 配置转换
        DBConfig dbConfig = new DBConfig();
        Field[] fields= ReflectUtil.getFields(DBConfig.class);
        for (Field field : fields) {
            String configName = configNameMapper.get(field.getName());
            if (StrUtil.isBlankIfStr(configName)) {
                continue;
            }
            String value = dbConfigObj.getString(configName);
            if (StrUtil.isBlankIfStr(value)) {
                continue;
            }
            ReflectUtil.setFieldValue(dbConfig, field, value);
        }
        log.debug("[Mysql配置] - [Consul配置] - 载入配置中心Mysql配置成功!");
        return dbConfig;
    }

    @ConditionalOnBean(name = "remoteDBConfig")
    @Bean
    public BeanLoadAfterContainer<DBConfig> remoteDBConfigAfterLog() {
        log.info("[Mysql配置] - [Consul配置] - 配置项加载完成！");
        return new BeanLoadAfterContainer<>();
    }

    @ConditionalOnBean(name = "remoteDBConfig")
    @Bean
    public DruidDataSource dataSource() {
        DBConfig remoteDBConfig = remoteDBConfig();
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(remoteDBConfig.getDriverClassName());
        druidDataSource.setUrl(remoteDBConfig.getUrl());
        druidDataSource.setUsername(remoteDBConfig.getUsername());
        druidDataSource.setPassword(remoteDBConfig.getPassword());
        druidDataSource.setInitialSize(remoteDBConfig.getInitSize());
        druidDataSource.setMinIdle(remoteDBConfig.getMinIdle());
        druidDataSource.setMaxActive(remoteDBConfig.getMaxActive());
        druidDataSource.setTimeBetweenEvictionRunsMillis(remoteDBConfig.getTimeBetweenEvictionRunsMillis());
        druidDataSource.setMinEvictableIdleTimeMillis(remoteDBConfig.getMinEvictableIdleTimeMillis());
        druidDataSource.setValidationQuery(remoteDBConfig.getValidationQuery());
        druidDataSource.setTestWhileIdle(remoteDBConfig.isTestWhileIdle());
        druidDataSource.setTestOnBorrow(remoteDBConfig.isTestOnBorrow());
        druidDataSource.setTestOnReturn(remoteDBConfig.isTestOnReturn());
        druidDataSource.setPoolPreparedStatements(remoteDBConfig.isPoolPreparedStatements());
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(remoteDBConfig.getMaxPoolPreparedStatementPerConnectionSize());
        druidDataSource.setConnectionProperties(remoteDBConfig.getConnectionProperties());
        druidDataSource.setUseGlobalDataSourceStat(remoteDBConfig.isUseGlobalDataSourceStat());
        return druidDataSource;
    }

    @ConditionalOnBean(name = "dataSource")
    @Bean
    public BeanLoadAfterContainer<DruidDataSource> dataSourceAfterLog() {
        log.info("[Mysql配置] -  Druid数据源加载完成！ 使用配置[Consul]");
        return new BeanLoadAfterContainer<>();
    }
}
