package com.warehouse.service.handler;

import org.springframework.stereotype.Component;

import com.warehouse.constant.OrderStatus;
import com.warehouse.model.Orders;

@Component
public class ProcessingStatusHandler implements OrderStatusHandler {

	@Override
	public OrderStatus getHandledStatus() {
		return OrderStatus.PROCESSING;
	}

}
