package com.warehouse.service.impl;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import org.springframework.stereotype.Service;

import com.warehouse.constant.OrderStatus;
import com.warehouse.dao.OrderDAO;
import com.warehouse.model.Orders;
import com.warehouse.service.OrderService;
import com.warehouse.service.OrderStatusHandler;

@Service
public class OrderServiceImpl implements OrderService {
	private final OrderDAO orderDAO;
	private final ApplicationContext ctx;
	private final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	private final Map<OrderStatus, OrderStatusHandler> handler = new EnumMap<>(OrderStatus.class);

	public OrderServiceImpl(OrderDAO orderDao, ApplicationContext ctx) {
		this.orderDAO = orderDao;
		this.ctx = ctx;
	}

	@PostConstruct
	public void initHandler() {
		Map<String, OrderStatusHandler> beans = ctx.getBeansOfType(OrderStatusHandler.class);
		logger.info("show all key set {}", beans.keySet().toString());
		for (OrderStatusHandler orderStatushandler : beans.values()) {
			handler.put(orderStatushandler.getHandledStatus(), orderStatushandler);
		}
	}

	@Override
	public Long placeOrder(Orders order) {
		logger.info("Placing order for userId={}, itemCount={}", order.getUserId(), order.getItems().size());
		order.setOrderDate(LocalDateTime.now());
		order.setStatus(OrderStatus.PENDING);
		Optional<Long> orderId = orderDAO.saveOrder(order);
		order.getItems().forEach(i -> i.setOrderId(orderId.get()));
        orderDAO.saveOrderItems(order.getItems());
		return orderId.get();
	}

	@Override
	public Map<String, Object> getOrder(Long id) {
		logger.info("Fetch order by Id={}", id);
		Map<String, Object> order = orderDAO.fetchOrder(id);
		return order;
	}

	@Override
	public List<Map<String, Object>> listOrders(int page, int size) {
		logger.info("Order list with size= {} and page ={}", size, page);
		List<Map<String, Object>> order = orderDAO.fetchListOrder(page, size);
		return order;
	}
	
	public List<Map<String, Object>> findItemsByOrderId(Long orderId) {
        logger.info("Fetching items for orderId: {}", orderId);
        List<Map<String, Object>> orderItem = orderDAO.fetchListOrderItem(orderId);
        return orderItem;
    }

	@Override
	public void updateStatus(Long orderId, String status) {
//		Map<String, Object> order = getOrder(orderId);
        OrderStatus targetStatus = OrderStatus.valueOf(status.toUpperCase());
        OrderStatusHandler statusHandler = handler.get(targetStatus);
        if (statusHandler == null) {
            throw new IllegalArgumentException("No handler found for status: " + status);
        }
//        statusHandler.updateStatus(order);
        orderDAO.updateStatus(statusHandler, orderId);
        logger.info("Order {} status updated to {}", orderId, statusHandler.getHandledStatus());

	}
}
