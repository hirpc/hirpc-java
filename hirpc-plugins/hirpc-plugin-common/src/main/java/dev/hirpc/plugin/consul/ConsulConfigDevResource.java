package dev.hirpc.plugin.consul;

/**
 * @author JT
 * @date 2022/8/3
 * @desc
 */
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
