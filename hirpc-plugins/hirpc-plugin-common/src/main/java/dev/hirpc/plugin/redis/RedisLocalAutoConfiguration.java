package dev.hirpc.plugin.redis;

import cn.hutool.core.util.StrUtil;
import dev.hirpc.plugin.BeanLoadAfterContainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.redisson.api.RedissonClient;
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
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;

/**
 * @author JT
 * @date 2022/7/17
 * @desc
 */
@Slf4j
@Configuration
@AutoConfigureOrder
@AutoConfigureAfter(RedisRemoteAutoConfiguration.class)
@ConditionalOnClass({LettuceConnectionFactory.class, RedissonClient.class})
@ConditionalOnMissingBean({RedisTemplate.class, RedissonClient.class})
@ConditionalOnResource(resources = {"config.properties"})
@PropertySource(value = {"classpath:config.properties"})
public class RedisLocalAutoConfiguration {

    @Resource
    private Environment environment;

    private Map<String, Function<RedisProperty, LettuceConnectionFactory>> redisFactoryMap = new HashMap<>();

    @PostConstruct
    public void initRedisFactoryMap() {
        // 单例模式
        this.redisFactoryMap.put(RedisMode.STANDALONE.getValue(), (redisProperty) -> {
            String conn = redisProperty().getNodes().get(0);
            String[] conns = conn.split(":");
            RedisStandaloneConfiguration redisStandaloneConfiguration = conns.length > 1 ? new RedisStandaloneConfiguration(conns[0], Integer.valueOf(conns[1])) :
                    new RedisStandaloneConfiguration(conns[0]);
            if (Objects.nonNull(redisProperty.getDatabase())) {
                redisStandaloneConfiguration.setDatabase(redisProperty.getDatabase());
            }
            if (StrUtil.isNotBlank(redisProperty.getPassword())) {
                redisStandaloneConfiguration.setPassword(redisProperty.getPassword());
            }
            return new LettuceConnectionFactory(redisStandaloneConfiguration);
        });
        // 集群模式
        this.redisFactoryMap.put(RedisMode.CLUSTER.getValue(), (redisProperty) -> {
            RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
            List<String> nodes = redisProperty.getNodes();
            for (String node : nodes) {
                String[] hostPort = node.split(":");
                redisClusterConfiguration.addClusterNode(new RedisNode(hostPort[0], Integer.valueOf(hostPort[1])));
            }
            if (StrUtil.isNotBlank(redisProperty.getPassword())) {
                redisClusterConfiguration.setPassword(redisProperty.getPassword());
            }
            redisClusterConfiguration.setMaxRedirects(redisProperty.getMaxRedirects());
            return new LettuceConnectionFactory(redisClusterConfiguration);
        });
        this.redisFactoryMap.put(RedisMode.SENTINEL.getValue(), redisProperty -> {
            RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
            List<String> nodes = redisProperty.getNodes();
            for (String node : nodes) {
                String[] hostPort = node.split(":");
                redisSentinelConfiguration.addSentinel(new RedisNode(hostPort[0], Integer.valueOf(hostPort[1])));
            }
            if (Objects.nonNull(redisProperty.getDatabase())) {
                redisSentinelConfiguration.setDatabase(redisProperty.getDatabase());
            }
            if (StrUtil.isNotBlank(redisProperty.getPassword())) {
                redisSentinelConfiguration.setPassword(redisProperty.getPassword());
            }
            return new LettuceConnectionFactory(redisSentinelConfiguration);
        });
    }

    @Bean
    public RedisProperty redisProperty() {
        String localRedisConfigTemplate = "hirpc.redis.{}";
        RedisProperty redisProperty = new RedisProperty();
        redisProperty.setName(this.environment.getProperty(StrUtil.format(localRedisConfigTemplate, "name"), redisProperty.getName()));
        redisProperty.setMode(this.environment.getProperty(StrUtil.format(localRedisConfigTemplate, "mode"), redisProperty.getMode()));
        String nodes = this.environment.getProperty(StrUtil.format(localRedisConfigTemplate, "nodes"));
        if (StrUtil.isNotBlank(nodes)) {
            redisProperty.setNodes(Arrays.asList(nodes.split(",")));
        }
        redisProperty.setSentinels(this.environment.getProperty(StrUtil.format(localRedisConfigTemplate, "sentinels"), List.class, redisProperty.getSentinels()));
        redisProperty.setMaxRedirects(this.environment.getProperty(StrUtil.format(localRedisConfigTemplate, "max-redirects"), Integer.class, redisProperty.getMaxRedirects()));
        redisProperty.setDatabase(this.environment.getProperty(StrUtil.format(localRedisConfigTemplate, "database"), Integer.class, redisProperty.getDatabase()));
        redisProperty.setPassword(this.environment.getProperty(StrUtil.format(localRedisConfigTemplate, "password"), redisProperty.getPassword()));
        redisProperty.setPoolMaxActive(this.environment.getProperty(StrUtil.format(localRedisConfigTemplate, "pool.max-active"), Integer.class, redisProperty.getPoolMaxActive()));
        redisProperty.setPoolMaxIdle(this.environment.getProperty(StrUtil.format(localRedisConfigTemplate, "pool.max-idle"), Integer.class, redisProperty.getPoolMaxIdle()));
        redisProperty.setPoolMinIdle(this.environment.getProperty(StrUtil.format(localRedisConfigTemplate, "pool.min-idle"), Integer.class, redisProperty.getPoolMinIdle()));
        redisProperty.setPoolMaxWait(this.environment.getProperty(StrUtil.format(localRedisConfigTemplate, "pool.max-wait"), Integer.class, redisProperty.getPoolMaxWait()));
        return redisProperty;
    }

    @Bean
    @ConditionalOnBean(RedisProperty.class)
    public LettuceConnectionFactory lettuceConnectionFactory(RedisProperty redisProperty) {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(redisProperty.getPoolMaxActive());
        poolConfig.setMaxIdle(redisProperty.getPoolMaxIdle());
        poolConfig.setMinIdle(redisProperty.getPoolMinIdle());
        poolConfig.setMaxWaitMillis(redisProperty.getPoolMaxWait());
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig).commandTimeout(Duration.ofSeconds(10000L))
                .build();
        Assert.notEmpty(redisProperty.getNodes(), "[Redis配置] - [Local配置] - RedisTemplate创建中，Nodes不能为空！");
        Function<RedisProperty, LettuceConnectionFactory> createRedisFactoryFun = this.redisFactoryMap.get(redisProperty.getMode());
        Assert.notNull(createRedisFactoryFun, StrUtil.format("[Redis配置] - [Local配置] - 未找到该模式: {}", redisProperty.getMode()));
        return createRedisFactoryFun.apply(redisProperty);
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
        log.info("[Redis配置] - Redis插件加载完成！使用配置[Local]");
        return new BeanLoadAfterContainer();
    }

}
