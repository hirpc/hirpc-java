package dev.hirpc.plugin.config;

/**
 * @author JT
 * @date 2022/8/14
 * @desc
 */
public enum TraceKey {

    /**
     *  服务名称
     */
    SERVER_NAME("server_name"),

    /**
     * 链路追踪 ID
     */
    TRACE_ID("trace_id");

    private String key;

    TraceKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
