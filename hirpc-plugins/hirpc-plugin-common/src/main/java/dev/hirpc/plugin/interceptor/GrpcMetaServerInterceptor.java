package dev.hirpc.plugin.interceptor;

import com.alibaba.fastjson2.JSON;
import dev.hirpc.plugin.config.TraceKey;
import dev.hirpc.plugin.domain.GrpcRequestClient;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.MDC;

/**
 * @author JT
 * @date 2022/8/14
 * @desc
 */
@Slf4j
@GrpcGlobalServerInterceptor
public class GrpcMetaServerInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

        headers.put(Metadata.Key.of("hrpc_md_message", Metadata.ASCII_STRING_MARSHALLER), "{\"server_name\":\"locationservice\",\"trace_id\":\"aaaaaaa.aaaa\",\"request_timeout\":3000000000,\"namespace\":\"development\",\"client_info\":{\"ip\":\"192.168.1.1\",\"ua\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36\"}}");
        String value = headers.get(Metadata.Key.of("hrpc_md_message", Metadata.ASCII_STRING_MARSHALLER));
        GrpcRequestClient grpcRequestClient = JSON.parseObject(value, GrpcRequestClient.class);
//        CacheOperate grpcHeaderCacheOperate = CacheManager.getGrpcHeaderCache();
//        grpcHeaderCacheOperate.set(String.valueOf(Thread.currentThread().getId()), grpcRequestClient);

        log.info("ServerInterceptor Thread id: {}", Thread.currentThread().getId());
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(next.startCall(call, headers)) {

            @Override
            public void onMessage(ReqT message) {
                MDC.put(TraceKey.SERVER_NAME.getKey(), grpcRequestClient.getServerName());
                MDC.put(TraceKey.TRACE_ID.getKey(), grpcRequestClient.getTraceId());
                super.onMessage(message);
            }

            @Override
            public void onComplete() {
                super.onComplete();
            }

            @Override
            public void onHalfClose() {
                MDC.remove(TraceKey.SERVER_NAME.getKey());
                MDC.remove(TraceKey.TRACE_ID.getKey());
                super.onHalfClose();
            }
        };
    }
}
