package com.warehouse.service.impl;

import java.time.LocalDateTime;
import java.util.*;

import com.warehouse.config.PropertyConfiguration;
import com.warehouse.model.InventoryHoldResultEvent;
import com.warehouse.model.OrderCreatedEvent;
import com.warehouse.service.OrderProducerService;
import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.warehouse.constant.OrderStatus;
import com.warehouse.dao.OrderDAO;
import com.warehouse.model.Orders;
import com.warehouse.service.OrderService;
import com.warehouse.service.OrderStatusHandler;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {
	private final OrderDAO orderDAO;
	private final ApplicationContext ctx;
	private final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	private final Map<OrderStatus, OrderStatusHandler> handler = new EnumMap<>(OrderStatus.class);
	private final OrderProducerService orderProducerService;
//	private final String orderTopic;
//	private final String inventoryResultTopic;
//	private final PropertyConfiguration;

	public OrderServiceImpl(OrderDAO orderDao, ApplicationContext ctx, OrderProducerService orderProducerService) {
		this.orderDAO = orderDao;
		this.ctx = ctx;
		this.orderProducerService = orderProducerService;
		// Can be replaced with common property configuration file
//		this.orderTopic = env.getProperty("order.topic.order-created","order.created");
//		this.inventoryResultTopic = env.getProperty("order.topic.inventory-hold-result","inventory.hold.result");
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
	@Transactional
	public Long placeOrder(Orders order) {
		UUID requestId = UUID.randomUUID();
		logger.info("Placing order for userId={}, itemCount={}", order.getUserId(), order.getItems().size());
		order.setOrderDate(LocalDateTime.now());
		order.setStatus(OrderStatus.PENDING);
		Optional<Long> orderId = orderDAO.saveOrder(order);
		order.getItems().forEach(i -> i.setOrderId(orderId.get()));
        orderDAO.saveOrderItems(order.getItems());

		// 2) publish OrderCreatedEvent — key by orderId for partitioning
//		OrderCreatedEvent evt = new OrderCreatedEvent();
//		evt.setOrderId(orderId.orElse(-1L));
//		evt.setUserId(order.getUserId());
//		evt.setItems(order.getItems());
//		evt.setRequestId(requestId.toString());
//		evt.setCreatedAt(LocalDateTime.now());

		orderProducerService.sendOrderCreatedMessage(String.valueOf(orderId));

		return orderId.get();
	}

	@Override
	public Map<String, Object> getOrder(Long id) {
		logger.info("Fetch order by Id={}", id);
		Map<String, Object> order = orderDAO.fetchOrder(id);
		return order;
	}

	@Override
	public List<Map<String, Object>> listOrders(Integer page, Integer size) {
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

	// This method will be called by Kafka listener below
//	public void handleInventoryHoldResult(InventoryHoldResultEvent event) {
//		// idempotency: check requestId or allocations already processed
//		UUID requestId = null;
//		try {
//			if(event.getRequestId() != null) requestId = UUID.fromString(event.getRequestId());
//		} catch (Exception ex) { /* ignore parse error */ }
//
//		Long orderId = event.getOrderId();
//		if (InventoryHoldResultEvent.HOLD_SUCCESS.equals(event.getStatus())) {
//			// avoid double allocation if already processed
//			if (requestId != null && repo.allocationsExist(orderId, requestId)) {
//				// already processed — ignore
//				return;
//			}
//
//			// update order status to CONFIRMED
//			repo.updateOrderStatus(orderId, "CONFIRMED");
//
//			// convert incoming allocations to repo AllocationRow
//			List<AllocationRow> rows = Optional.ofNullable(event.getAllocations())
//					.orElse(Collections.emptyList())
//					.stream()
//					.map(a -> new AllocationRow(
//							a.getItemId(), a.getProductId(), a.getQuantity(),
//							a.getWarehouseId(), a.getLatitude(), a.getLongitude()))
//					.collect(Collectors.toList());
//
//			// insert allocations idempotently
//			repo.insertAllocations(orderId, rows, requestId != null ? requestId : UUID.randomUUID());
//
//		} else {
//			// hold failed -> set order status to REJECTED (or CANCELLED) and no allocation
//			repo.updateOrderStatus(orderId, "REJECTED");
//		}
//	}
}
