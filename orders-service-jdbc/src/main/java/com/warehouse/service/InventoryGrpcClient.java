package com.warehouse.service;
import com.inventory.grpc.InventoryServiceGrpc;
import com.inventory.grpc.ReserveStock;
import com.inventory.grpc.ReserveStockResponse;
import com.warehouse.model.OrderCreatedEvent;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class InventoryGrpcClient {

    private final static Logger logger = LoggerFactory.getLogger(InventoryGrpcClient.class);

    @GrpcClient("inventory-service")
    private InventoryServiceGrpc.InventoryServiceBlockingStub stub;

    public ReserveStockResponse reserve(OrderCreatedEvent event) {
        logger.info("Reserving stock (calling from order service) for orderId={}", event.getOrderId());
        ReserveStock reserveStock = ReserveStock.newBuilder()
                .setOrderId(event.getOrderId())
                .addAllItems(event.getItems().stream()
                        .map(i -> com.inventory.grpc.OrderItem.newBuilder()
                                .setProductId(i.getProductId())
                                .setQuantity(i.getQuantity())
                                .build())
                        .toList())
                .build();

        ReserveStockResponse response = stub.reserveStock(reserveStock);
        logger.info("Response from inventory service for orderId={} is {}", event.getOrderId(), response);
        return response;
    }

//
//    public ReserveResponse reserveStock(List<OrderItem> orderItems) {
//        ReserveRequest request = ReserveRequest.newBuilder()
//                .addAllItems(orderItems.stream()
//                        .map(i -> Item.newBuilder()
//                                .setProductId(i.getProductId())
//                                .setQuantity(i.getQuantity())
//                                .build())
//                        .toList())
//                .build();
//
//        Metadata metadata = new Metadata();
//        Metadata.Key<String> authKey =
//                Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);
//
//        metadata.put(authKey, "Bearer internal-secret");
//
//        // ✅ Correct way: interceptor
//        ClientInterceptor interceptor =
//                MetadataUtils.newAttachHeadersInterceptor(metadata);
//
//        // Attach interceptor to stub
//        InventoryServiceGrpc.InventoryServiceBlockingStub securedStub =
//                inventoryStub.withInterceptors(interceptor);
//        return securedStub.reserveStock(request);
//    }
}
