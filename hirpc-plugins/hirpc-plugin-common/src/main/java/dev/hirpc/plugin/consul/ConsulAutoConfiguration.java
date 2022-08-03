package dev.hirpc.plugin.consul;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import dev.hirpc.plugin.BeanLoadAfterContainer;
import dev.hirpc.plugin.spring.SpringUtilAutoConfiguration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * @author JT
 * @date 2022/7/15
 * @desc
 */
@Slf4j
@Setter
@Getter
@Configuration
@AutoConfigureAfter(SpringUtilAutoConfiguration.class)
public class ConsulAutoConfiguration {

    @Resource
    private Environment environment;

    @Resource
    private ApplicationContext applicationContext;

    @ConditionalOnProperty(value = "spring.cloud.consul.enable", havingValue = "true")
    @Bean
    public ConsulConfigProperty consulConfigProperty() {
        String consulKeyTemplate = "spring.cloud.consul.{}";
        String hostKey = StrUtil.format(consulKeyTemplate, "host");
        String portKey = StrUtil.format(consulKeyTemplate, "port");
        String aclTokenKey = StrUtil.format(consulKeyTemplate, "discovery.acl-token");
        String host = this.environment.getProperty(hostKey);
        String port = this.environment.getProperty(portKey);
        String aclToken = this.environment.getProperty(aclTokenKey);
        Assert.notBlank(host, StrUtil.format("未找到配置项: [{}]", hostKey));
        Assert.notBlank(port, StrUtil.format("未找到配置项: [{}]", portKey));
        Assert.notBlank(aclToken, StrUtil.format("未找到配置项: [{}]", aclTokenKey));

        ConsulConfigProperty consulConfigProperty = new ConsulConfigProperty();
        consulConfigProperty.setHost(host);
        consulConfigProperty.setPort(port);
        consulConfigProperty.setAclToken(aclToken);
        return consulConfigProperty;
    }

    @ConditionalOnBean(name = "consulConfigProperty")
    @Bean
    public BeanLoadAfterContainer<ConsulConfigProperty> consulConfigPropertyAfterLog() {
        log.info("[Consul配置] - 配置项已加载完成！");
        return new BeanLoadAfterContainer<ConsulConfigProperty>();
    }

    @Bean("consulConfigResource")
    @Profile("dev")
    public ConsulConfigResource consulCOnfigDevResource() {
        return new ConsulConfigDevResource();
    }

    @Profile("prod")
    @Bean("consulConfigResource")
    public ConsulConfigResource consulConfigProdResource() {
        return new ConsulConfigProdResource();
    }

}
