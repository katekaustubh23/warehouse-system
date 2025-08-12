package com.warehouse.service;

import org.springframework.stereotype.Component;

import com.warehouse.constant.OrderStatus;
import com.warehouse.model.Orders;

@Component
public class ProcessingStatusHandler implements OrderStatusHandler{

	@Override
	public void updateStatus(Orders order) {
		 order.setStatus(OrderStatus.PROCESSING);
	}

	@Override
	public OrderStatus getHandledStatus() {
		return OrderStatus.PROCESSING;
	}

}
