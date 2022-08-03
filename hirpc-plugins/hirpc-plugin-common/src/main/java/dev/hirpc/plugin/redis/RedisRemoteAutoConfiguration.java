package dev.hirpc.plugin.redis;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import dev.hirpc.plugin.BeanLoadAfterContainer;
import dev.hirpc.plugin.consul.ConsulConfigProperty;
import dev.hirpc.plugin.consul.ConsulConfigResource;
import dev.hirpc.plugin.utils.ConsulConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Arrays;

/**
 * @author JT
 * @date 2022/7/17
 * @desc
 */
@Slf4j
@Configuration
@AutoConfigureOrder
@AutoConfigureBefore(RedisLocalAutoConfiguration.class)
@ConditionalOnBean(ConsulConfigProperty.class)
@ConditionalOnClass({LettuceConnectionFactory.class, RedissonClient.class})
public class RedisRemoteAutoConfiguration {

    @Resource(name = "consulConfigResource")
    private ConsulConfigResource consulConfigResource;

    @Resource
    private ConsulConfigProperty consulConfigProperty;

    @Bean
    public RedisProperty redisProperty() {
        String res = ConsulConfigUtil.getConfigWithAcl(consulConfigProperty, consulConfigResource.getRemoteRedisConfigPath());
        Assert.hasText(res, "[Redis配置] - [Consult配置] - 远程Redis配置为空");
        JSONObject redisConfigObj = JSON.parseObject(res);
        String host = redisConfigObj.getString("address");
        String port = redisConfigObj.getString("port");
        String password = redisConfigObj.getString("password");
        Integer db = redisConfigObj.getInteger("db");
        Integer maxRetries =redisConfigObj.getInteger("max_retries");
        Assert.hasText(host, "[Redis配置] - [Consult配置] - address未配置！");
        Assert.hasText(port, "[Redis配置] - [Consult配置] - port未配置！");
        Assert.hasText(password, "[Redis配置] - [Consult配置] - password未配置！");
        Assert.notNull(db, "[Redis配置] - [Consult配置] - db未配置！");
        Assert.notNull(maxRetries, "[Redis配置] - [Consult配置] - maxRetries未配置！");

        RedisProperty redisProperty = new RedisProperty();
        redisProperty.setMode(RedisMode.STANDALONE.getValue());
        redisProperty.setName("Consul-Redis");
        redisProperty.setMaster("master");
        redisProperty.setNodes(Arrays.asList(StrUtil.format("{}:{}", host, port)));
        redisProperty.setMaxRedirects(maxRetries);
        redisProperty.setPassword(password);
        redisProperty.setDatabase(db);
        return redisProperty;
    }


    @ConditionalOnBean(RedisProperty.class)
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory(RedisProperty redisProperty) {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(redisProperty.getPoolMaxActive());
        poolConfig.setMaxIdle(redisProperty.getPoolMaxIdle());
        poolConfig.setMinIdle(redisProperty.getPoolMinIdle());
        poolConfig.setMaxWaitMillis(redisProperty.getPoolMaxWait());
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig).commandTimeout(Duration.ofSeconds(10000L))
                .build();

        Assert.notEmpty(redisProperty.getNodes(), "[Redis配置] - [Consult配置] - RedisTemplate创建中，Nodes不能为空！");
        String conn = redisProperty().getNodes().get(0);
        String[] conns = conn.split(":");
        RedisStandaloneConfiguration redisStandaloneConfiguration = conns.length > 1 ? new RedisStandaloneConfiguration(conns[0], Integer.valueOf(conns[1])) :
                new RedisStandaloneConfiguration(conns[0]);
        redisStandaloneConfiguration.setDatabase(redisProperty.getDatabase());
        redisStandaloneConfiguration.setPassword(redisProperty.getPassword());
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
        factory.afterPropertiesSet();
        return factory;
    }

    @ConditionalOnBean(LettuceConnectionFactory.class)
    @Bean
    public RedisTemplate redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        FastJson2JsonRedisSerializer<Object> fastJson2JsonRedisSerializer = new FastJson2JsonRedisSerializer<>(Object.class);
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(fastJson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(fastJson2JsonRedisSerializer);
        redisTemplate.setDefaultSerializer(stringSerializer);
        return redisTemplate;
    }

    @ConditionalOnBean(RedisTemplate.class)
    @Bean
    public BeanLoadAfterContainer redisTemplateLoadAfter() {
        log.info("[Redis配置] - Redis插件加载完成！使用配置[Consul]");
        return new BeanLoadAfterContainer();
    }

    @ConditionalOnBean(RedisProperty.class)
    @Bean
    public RedissonClient redisson(RedisProperty redisProperty) {
        Assert.notEmpty(redisProperty.getNodes(), "[Redis配置] - [Consult配置] - RedisTemplate创建中，Nodes不能为空！");
        String conn = redisProperty().getNodes().get(0);
        Config config = new Config();
        config.useSingleServer().setAddress(StrUtil.format("redis://{}", conn));
        config.useSingleServer().setDatabase(redisProperty.getDatabase());
        config.useSingleServer().setPassword(redisProperty.getPassword());
        return Redisson.create(config);
    }

    @ConditionalOnBean(RedissonClient.class)
    @Bean
    public BeanLoadAfterContainer redissonAfter() {
        log.info("[redisson配置] - redisson插件加载完成！使用配置[Consul]");
        return new BeanLoadAfterContainer();
    }

}
