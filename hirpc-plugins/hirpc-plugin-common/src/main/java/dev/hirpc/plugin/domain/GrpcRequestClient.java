package dev.hirpc.plugin.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JT
 * @date 2022/8/14
 * @desc
 */
@Setter
@Getter
public class GrpcRequestClient {

    @JSONField(name = "server_name")
    private String serverName;

    @JSONField(name = "trace_id")
    private String traceId;

    @JSONField(name = "request_timeout")
    private Long requestTimeout;

    @JSONField(name = "namespace")
    private String namespace;

    @JSONField(name = "client_info")
    private ClientInfo clientInfo;

    @Setter
    @Getter
    private static class ClientInfo {

        @JSONField(name = "ip")
        private String ip;

        @JSONField(name = "ua")
        private String ua;

    }
}
