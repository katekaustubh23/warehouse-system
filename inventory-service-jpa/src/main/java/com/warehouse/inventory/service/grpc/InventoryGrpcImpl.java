package com.warehouse.inventory.service.grpc;

import com.inventory.grpc.*;
import com.warehouse.inventory.service.RedisStockService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class InventoryGrpcImpl extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final RedisStockService redisStockService;
    private static final Logger logger = LoggerFactory.getLogger(InventoryGrpcImpl.class);
    // This method handles the gRPC request and sends a response back to the client.
    // It takes a HelloRequest object, extracts the name, constructs a greeting message,
    // and then builds a HelloResponse object to send back to the client.
/*  @Override
     public void reserveStockDummy(ReserveStock request, StreamObserver<ReserveStockResponse> responseObserver) {
        logger.info("Received gRPC request: {}", request);
        long orderId = request.getOrderId();
        List<ItemStatus> responseItems = new ArrayList<>();
        for ( OrderItem orderItem : request.getItemsList()) {
            logger.info("Processing order item: productId={}, quantity={}", orderItem.getProductId(), orderItem.getQuantity());
            boolean isValid = redisStockService.reverseStock(orderItem.getProductId(), orderItem.getQuantity());
            if (!isValid) {

                logger.info("Stock reservation failed for productId={}, quantity={}", orderItem.getProductId(), orderItem.getQuantity());
                responseItems.add(ItemStatus.newBuilder()
                        .setProductId(orderItem.getProductId())
                        .setReserved(false)
                        .build());
            }else{
                responseItems.add(ItemStatus.newBuilder()
                        .setProductId(orderItem.getProductId())
                        .setReserved(true)
                        .build());
            }
        }

        ReserveStockResponse response = ReserveStockResponse.newBuilder()
                .setOrderId(orderId)
                .addAllItems(responseItems)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    } */


    @Override
    public void reserveStock(ReserveStock request, StreamObserver<ReserveStockResponse> responseObserver) {
        logger.info("Received gRPC request: {}", request);
        long orderId = request.getOrderId();
        List<ItemStatus> responseItems = new ArrayList<>();

        boolean isReserved = redisStockService.reserveStock(orderId, request.getItemsList());
        if(isReserved){
                for ( OrderItem orderItem : request.getItemsList()) {
                    logger.info("Stock reserved for productId={}, quantity={}", orderItem.getProductId(), orderItem.getQuantity());
                    responseItems.add(ItemStatus.newBuilder()
                            .setProductId(orderItem.getProductId())
                            .setReserved(true)
                            .build());
                }
        } else{
            for ( OrderItem orderItem : request.getItemsList()) {
                logger.info("Stock reservation failed for productId={}, quantity={}", orderItem.getProductId(), orderItem.getQuantity());
                responseItems.add(ItemStatus.newBuilder()
                        .setProductId(orderItem.getProductId())
                        .setReserved(false)
                        .build());
            }
        }

        ReserveStockResponse response = ReserveStockResponse.newBuilder()
                .setOrderId(orderId)
                .addAllItems(responseItems)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
