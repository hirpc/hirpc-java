package dev.hirpc.plugin.consul;

/**
 * @author JT
 * @date 2022/8/3
 * @desc
 */
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
