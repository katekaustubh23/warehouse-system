package com.warehouse.service.handler;

import com.warehouse.constant.OrderStatus;
import org.springframework.stereotype.Service;

@Service
public class ConfirmSuccessStatusHandler  implements OrderStatusHandler {

    @Override
    public OrderStatus getHandledStatus() {
        return OrderStatus.CONFIRM_SUCCESS;
    }
}
