package com.warehouse.service;

import java.util.List;
import java.util.Map;

import com.warehouse.model.Orders;

public interface OrderService {

	Long placeOrder(Orders order);

	Map<String, Object> getOrder(Long id);

	List<Map<String, Object>> listOrders(int page, int size);

	void updateStatus(Long id, String status);

}
