package com.warehouse.service;

import com.warehouse.constant.OrderStatus;
import com.warehouse.model.Orders;

public interface OrderStatusHandler {
	OrderStatus getHandledStatus();

	void updateStatus(Orders order);
}
