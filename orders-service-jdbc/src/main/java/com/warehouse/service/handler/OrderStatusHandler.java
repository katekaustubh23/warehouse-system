package com.warehouse.service.handler;

import com.warehouse.constant.OrderStatus;
import com.warehouse.model.Orders;

public interface OrderStatusHandler {
	OrderStatus getHandledStatus();
}
