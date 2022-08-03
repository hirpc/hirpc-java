package dev.hirpc.plugin.consul;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author JT
 * @date 2022/8/3
 * @desc
 */
@Profile("dev")
@Configuration
public class ConsulConfigDevResource implements ConsulConfigResource{

    @Override
    public String getRemoteMysqlConfigPath() {
        return "development/database/mysql";
    }

    @Override
    public String getRemoteMongoConfigPath() {
        return "development/databases/mongodb";
    }

    @Override
    public String getRemoteRedisConfigPath() {
        return "development/databases/redis";
    }
}
