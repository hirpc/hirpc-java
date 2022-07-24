package dev.hirpc.plugin.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import dev.hirpc.plugin.BeanLoadAfterContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
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
@ConditionalOnClass(MongoClient.class)
@ConditionalOnMissingBean(MongoTemplate.class)
@ConditionalOnResource(resources = {"config.properties"})
@PropertySource(value = {"classpath:config.properties"})
@AutoConfigureAfter(MongoRemoteAutoConfiguration.class)
public class MongoLocalAutoConfiguration {

    @Resource
    private Environment environment;

    @Bean
    public MongodbProperty mongodbProperty() {
        String uri = this.environment.getProperty("hirpc.mongodb.uri");
        Assert.notNull(uri, "[Mongodb配置] - [本地配置] - 未获取到\"uri\"");
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
        log.info("[Mongodb配置] - [本地配置] - Mongodb初始化完成！");
        return new BeanLoadAfterContainer<>();
    }
}
