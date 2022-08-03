package dev.hirpc.plugin.consul;

/**
 * @author JT
 * @date 2022/8/3
 * @desc
 */
public interface ConsulConfigResource {

    /**
     * Consul 配置中心 Mysql配置文件地址
     * @return
     */
    String getRemoteMysqlConfigPath();

    /**
     * Consul 配置中心 Mongo配置文件地址
     * @return
     */
    String getRemoteMongoConfigPath();

    /**
     * Consul 配置中心 Redis配置文件地址
     * @return
     */
    String getRemoteRedisConfigPath();
}
