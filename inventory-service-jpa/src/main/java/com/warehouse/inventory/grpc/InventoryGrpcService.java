package com.warehouse.inventory.grpc;

import com.inventory.grpc.InventoryServiceGrpc;
import com.inventory.grpc.Item;
import com.inventory.grpc.ReserveRequest;
import com.inventory.grpc.ReserveResponse;
import com.warehouse.inventory.service.RedisStockService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


@GrpcService
public class InventoryGrpcService extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final Logger logger = LoggerFactory.getLogger(InventoryGrpcService.class);

    @Autowired
    private RedisStockService redisStockService;

    @Override
    public void reserveStock(ReserveRequest request,
                             StreamObserver<ReserveResponse> responseObserver) {

        logger.info("Received reserveStock request with {} items", request.getItemsCount());
        List<Item> accepted = new ArrayList<>();
        List<Item> rejected = new ArrayList<>();

        for (Item item : request.getItemsList()) {

            boolean success = redisStockService.reverseStock(
                    item.getProductId(),
                    item.getQuantity()
            );

            if (success) {
                accepted.add(item);
            } else {
                rejected.add(item);
            }
        }

        ReserveResponse response = ReserveResponse.newBuilder()
                .setStatus(rejected.isEmpty() ? "SUCCESS" : "PARTIAL")
                .addAllAccepted(accepted)
                .addAllRejected(rejected)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
