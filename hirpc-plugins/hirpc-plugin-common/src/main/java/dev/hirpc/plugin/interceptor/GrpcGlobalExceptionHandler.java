package dev.hirpc.plugin.interceptor;

import io.grpc.*;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;

/**
 * @author JT
 * @date 2022/8/6
 * @desc
 */
@GrpcGlobalServerInterceptor
public class GrpcGlobalExceptionHandler implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> next) {
        ServerCall.Listener<ReqT> delegate = next.startCall(serverCall, metadata);
        return new ExceptionHandlingServerCallListener<>(delegate, serverCall, metadata);
    }

    private class ExceptionHandlingServerCallListener<ReqT, RespT> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {

        private ServerCall<ReqT, RespT> serverCall;
        private Metadata metadata;

        ExceptionHandlingServerCallListener(ServerCall.Listener<ReqT> listener, ServerCall<ReqT, RespT> serverCall,
                                            Metadata metadata) {
            super(listener);
            this.serverCall = serverCall;
            this.metadata = metadata;
        }

        @Override
        public void onHalfClose() {
            try {
                super.onHalfClose();
            } catch (RuntimeException ex) {
                handleException(ex, serverCall, metadata);
                throw ex;
            }
        }

        @Override
        public void onReady() {
            try {
                super.onReady();
            } catch (RuntimeException ex) {
                handleException(ex, serverCall, metadata);
                throw ex;
            }
        }

        private void handleException(RuntimeException exception, ServerCall<ReqT, RespT> serverCall, Metadata metadata) {
            if (exception instanceof IllegalArgumentException) {
                serverCall.close(Status.INVALID_ARGUMENT.withDescription(exception.getMessage()), metadata);
            } else {
                serverCall.close(Status.UNKNOWN.withDescription(exception.getMessage()), metadata);
            }
        }
    }
}
