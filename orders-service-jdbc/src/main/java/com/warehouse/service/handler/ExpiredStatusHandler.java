package com.warehouse.service.handler;

import com.warehouse.constant.OrderStatus;
import com.warehouse.model.Orders;
import org.springframework.stereotype.Component;

@Component
public class ExpiredStatusHandler implements OrderStatusHandler {

    @Override
    public OrderStatus getHandledStatus() {
        return OrderStatus.EXPIRED;
    }

}
