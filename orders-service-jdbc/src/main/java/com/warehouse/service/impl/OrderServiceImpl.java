package com.warehouse.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.dao.OutBoxDAO;
import com.warehouse.model.OrderConfirmEventDto;
import com.warehouse.model.OrderCreatedEvent;
import com.warehouse.model.OutBox;
import com.warehouse.service.InventoryGrpcClient;
import com.warehouse.service.OrderProducerService;
import jakarta.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import org.springframework.stereotype.Service;

import com.warehouse.constant.OrderStatus;
import com.warehouse.dao.OrderDAO;
import com.warehouse.model.Orders;
import com.warehouse.service.OrderService;
import com.warehouse.service.handler.OrderStatusHandler;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
	private final OrderDAO orderDAO;
	private final OutBoxDAO outBoxDAO;
	private final ObjectMapper objectMapper;
	private final ApplicationContext ctx;
	private final InventoryGrpcClient inventoryGrpcClient;
	private final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	private final Map<OrderStatus, OrderStatusHandler> handler = new EnumMap<>(OrderStatus.class);
//	private final String orderTopic;
//	private final String inventoryResultTopic;
//	private final PropertyConfiguration;

	public OrderServiceImpl(OrderDAO orderDao,OutBoxDAO outBoxDAO,ObjectMapper objectMapper, ApplicationContext ctx, InventoryGrpcClient inventoryGrpcClient) {
		this.orderDAO = orderDao;
		this.outBoxDAO = outBoxDAO;
		this.objectMapper = objectMapper;
		this.ctx = ctx;
		this.inventoryGrpcClient = inventoryGrpcClient;
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
		inventoryGrpcClient.reserve(new OrderCreatedEvent(orderId.get(), order.getItems()));

		return orderId.get();
	}

	@Transactional
	@Override
	public Long confirmOrder(Long orderId, String status) throws JsonProcessingException {
		Map<String, Object> orderMap = getOrder(orderId);

		Timestamp timestamp = (Timestamp) orderMap.get("order_date");

		LocalDateTime orderDate = timestamp.toLocalDateTime();
		Orders order = Orders.orderMapper((Long) orderMap.get("id"),
				(Long) orderMap.get("user_id"), orderDate,
				OrderStatus.valueOf((String) orderMap.get("status")),
				(String) orderMap.get("delivery_address"), new ArrayList<>());

		log.info("Confirming order with id={}, current status={}, target status={}",
				orderId, order.getStatus(), status);

		// Guard check
		if (order.getStatus() != OrderStatus.PENDING) {
			throw new IllegalStateException("Order cannot be confirmed");
		}

		// make is process
		order.setStatus(OrderStatus.PROCESSING); // NOT CONFIRMED
		orderDAO.updateOrder(order, orderId);
		OrderConfirmEventDto orderConfirm = new OrderConfirmEventDto(orderId, status);
		String payload = objectMapper.writeValueAsString(orderConfirm);
		outBoxDAO.saveOutBox(new OutBox().builder()
				.payload(payload)
				.type("CONFIRM_ORDER")
				.status(OrderStatus.NEW.name())
				.createdAt(LocalDateTime.now())
				.build());
		return orderId;
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
        orderDAO.updateStatus(statusHandler.getHandledStatus().name(), orderId);
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
