package dev.hirpc.plugin.redis;

/**
 * @author JT
 * @date 2022/7/23
 * @desc
 */
public enum RedisMode {

    /**
     * 单例模式
     */
    STANDALONE("standalone"),

    /**
     * 集群模式
     */
    CLUSTER("cluster"),

    /**
     * 哨兵模式
     */
    SENTINEL("sentinel");

    private final String value;

    RedisMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
