package dev.hirpc.plugin.spring;

import dev.hirpc.plugin.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author JT
 * @date 2022/7/16
 * @desc
 */
@Slf4j
@Configuration
@AutoConfigureOrder
public class SpringUtilAutoConfiguration {


    @Resource
    private ApplicationContext applicationContext;

    @PostConstruct
    public void initSpringUtil() {
        SpringUtil.setApplicationContext(applicationContext);
        log.debug("SpringUtil 初始化完成!");
    }
}
