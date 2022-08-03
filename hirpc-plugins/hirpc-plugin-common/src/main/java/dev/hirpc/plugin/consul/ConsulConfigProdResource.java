package dev.hirpc.plugin.consul;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author JT
 * @date 2022/8/3
 * @desc
 */
@Profile("prod")
@Configuration
public class ConsulConfigProdResource implements ConsulConfigResource{
    @Override
    public String getRemoteMysqlConfigPath() {
        return "production/database/mysql";
    }

    @Override
    public String getRemoteMongoConfigPath() {
        return "production/databases/mongodb";
    }

    @Override
    public String getRemoteRedisConfigPath() {
        return "production/databases/redis";
    }
}
