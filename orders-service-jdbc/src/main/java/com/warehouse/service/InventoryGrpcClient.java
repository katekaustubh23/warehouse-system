package com.warehouse.service;

import com.inventory.grpc.InventoryServiceGrpc;
import com.inventory.grpc.Item;
import com.inventory.grpc.ReserveRequest;
import com.inventory.grpc.ReserveResponse;
import com.warehouse.model.OrderItem;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryGrpcClient {

    @GrpcClient("inventory-service")
    private InventoryServiceGrpc.InventoryServiceBlockingStub inventoryStub;

    public ReserveResponse reserveStock(List<OrderItem> orderItems) {
        ReserveRequest request = ReserveRequest.newBuilder()
                .addAllItems(orderItems.stream()
                        .map(i -> Item.newBuilder()
                                .setProductId(i.getProductId())
                                .setQuantity(i.getQuantity())
                                .build())
                        .toList())
                .build();

        Metadata metadata = new Metadata();
        Metadata.Key<String> authKey =
                Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

        metadata.put(authKey, "Bearer internal-secret");

        // ✅ Correct way: interceptor
        ClientInterceptor interceptor =
                MetadataUtils.newAttachHeadersInterceptor(metadata);

        // Attach interceptor to stub
        InventoryServiceGrpc.InventoryServiceBlockingStub securedStub =
                inventoryStub.withInterceptors(interceptor);
        return securedStub.reserveStock(request);
    }
}
