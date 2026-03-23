package com.warehouse.inventory.grpc;

import io.grpc.*;
import org.springframework.stereotype.Component;

@Component
public class GrpcAuthInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall,
                                                                 Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> serverCallHandler) {

        String token = metadata.get(
                Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER)
        );
        if (token == null || !token.equals("Bearer internal-secret")) {
            serverCall.close(Status.UNAUTHENTICATED, metadata);
            return new ServerCall.Listener<>() {};
        }

        return serverCallHandler.startCall(serverCall, metadata);
    }
}
