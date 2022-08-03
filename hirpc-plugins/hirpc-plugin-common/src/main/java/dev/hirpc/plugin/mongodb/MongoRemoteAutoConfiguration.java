package dev.hirpc.plugin.mongodb;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import dev.hirpc.plugin.BeanLoadAfterContainer;
import dev.hirpc.plugin.consul.ConsulConfigProperty;
import dev.hirpc.plugin.consul.ConsulConfigResource;
import dev.hirpc.plugin.utils.ConsulConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * @author JT
 * @date 2022/7/17
 * @desc
 */
@Slf4j
@Configuration
@AutoConfigureOrder
@AutoConfigureBefore(MongoLocalAutoConfiguration.class)
@ConditionalOnClass(MongoClient.class)
@ConditionalOnBean(ConsulConfigProperty.class)
public class MongoRemoteAutoConfiguration {

    @Resource
    private ConsulConfigResource consulConfigResource;

    @Resource
    private ConsulConfigProperty consulConfigProperty;

    @Bean
    public MongodbProperty mongodbProperty() {
        // 载入consul远程Mongodb配置
        String res = ConsulConfigUtil.getConfigWithAcl(consulConfigProperty, consulConfigResource.getRemoteMongoConfigPath());
        Assert.notNull(res, "[Mongodb配置] - [Consul配置] - 未获取到远程配置!");
        JSONObject dbConfigObj = JSON.parseObject(res);
        String address = dbConfigObj.getString("address");
        String username = dbConfigObj.getString("username");
        String password = dbConfigObj.getString("password");
        Assert.hasText(address, "[Mongodb配置] - [Consul配置] - 未获取到地址\"address\"!");
        Assert.hasText(username, "[Mongodb配置] - [Consul配置] - 未获取到用户名\"username\"!");
        Assert.hasText(password, "[Mongodb配置] - [Consul配置] - 未获取到密码\"password\"!");

        String uri = StrUtil.format("mongodb://{}:{}@{}", username, password, address);
        MongodbProperty mongodbProperty = new MongodbProperty();
        mongodbProperty.setUri(uri);
        return mongodbProperty;
    }

    @ConditionalOnBean(MongodbProperty.class)
    @Bean
    public MongoTemplate mongoTemplate(MongodbProperty mongodbProperty) {
        MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(new ConnectionString(mongodbProperty.getUri()));
        return new MongoTemplate(factory);
    }

    @ConditionalOnBean(MongoTemplate.class)
    @Bean
    public BeanLoadAfterContainer<MongoTemplate> mongoTemplateAfterLog() {
        log.info("[Mongodb配置] - [Consul配置] - Mongodb初始化完成！");
        return new BeanLoadAfterContainer<>();
    }

}
