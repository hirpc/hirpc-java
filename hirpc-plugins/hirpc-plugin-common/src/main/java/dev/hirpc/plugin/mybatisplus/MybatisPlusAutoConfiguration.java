package dev.hirpc.plugin.mybatisplus;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.MybatisConfiguration;
import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.mapper.LogicSqlInjector;
import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;
import dev.hirpc.plugin.BeanLoadAfterContainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import java.io.IOException;

/**
 * @author JT
 * @date 2022/7/17
 * @desc
 */
@Slf4j
@Configuration
@AutoConfigureOrder
@ConditionalOnBean(DruidDataSource.class)
@ConditionalOnClass(value = {GlobalConfiguration.class, TransactionAwareDataSourceProxy.class})
public class MybatisPlusAutoConfiguration {

    @Value("${mybatis.xmlPath:}")
    private String mybatisXmlPath;

    @Bean
    public GlobalConfiguration globalConfiguration() {
        GlobalConfiguration globalConfiguration = new GlobalConfiguration(new LogicSqlInjector());
        globalConfiguration.setLogicDeleteValue("-1");
        globalConfiguration.setLogicNotDeleteValue("1");
        globalConfiguration.setIdType(0);
        globalConfiguration.setDbColumnUnderline(false);
        globalConfiguration.setRefresh(true);
        return globalConfiguration;
    }

    @ConditionalOnBean(GlobalConfiguration.class)
    @Bean
    public MybatisSqlSessionFactoryBean sqlSessionFactory(DruidDataSource dataSource) throws IOException {
        // 加载Mybatis-plus 配置
       MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(false);

        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sqlSessionFactoryBean.setConfiguration(configuration);
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setPlugins(new Interceptor[] {
                new PaginationInterceptor()                   // 添加分页功能
        });
        if (StrUtil.isNotBlank(mybatisXmlPath)) {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            sqlSessionFactoryBean.setMapperLocations(resolver.getResources(mybatisXmlPath));
            log.info("[Mybatis-Plus配置] - xml路径配置成功: {}", mybatisXmlPath);
        } else {
            log.info("[Mybatis-Plus配置] - 未配置xml路径，若后续启用，可在application.yml配置参数[mybatis.xmlPath]");
        }
        return sqlSessionFactoryBean;
    }


    @ConditionalOnBean(MybatisSqlSessionFactoryBean.class)
    @Bean
    public BeanLoadAfterContainer<MybatisSqlSessionFactoryBean> sqlSessionFactoryAfterLog() {
        log.info("[Mybatis-Plus配置] - mybatisPlus 插件加载完毕!");
        return new BeanLoadAfterContainer<>();
    }



}
