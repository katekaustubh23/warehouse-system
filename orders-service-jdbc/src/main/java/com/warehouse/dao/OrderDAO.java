package com.warehouse.dao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.warehouse.constant.TableNames;
import com.warehouse.dao.helper.GenericJdbcRepository;
import com.warehouse.model.OrderItem;
import com.warehouse.model.Orders;
import com.warehouse.service.OrderStatusHandler;
import com.warehouse.utility.EntityMapper;

@Repository
public class OrderDAO {

	private final Logger logger = LoggerFactory.getLogger(OrderDAO.class);

	private final JdbcTemplate jdbcTemplate;

	private GenericJdbcRepository genericJdbcRepository;

	public OrderDAO(GenericJdbcRepository genericJdbcRepository, JdbcTemplate jdbcTemplate) {
		super();
		this.genericJdbcRepository = genericJdbcRepository;
		this.jdbcTemplate = jdbcTemplate;
	}

	public Optional<Long> saveOrder(Orders order) {
		logger.info("Saving new order for userId={}", order.getUserId());
		Map<String, Object> mapColumns = EntityMapper.toMap(order); // to maintain order

		return genericJdbcRepository.insert(
				TableNames.ORDER_SCHEMA.name().toLowerCase() + "." + TableNames.ORDERS.name().toLowerCase(),
				mapColumns);
	}

	public void saveOrderItems(List<OrderItem> items) {
		List<Map<String, Object>> itemMaps = items.stream().map(EntityMapper::toMap).collect(Collectors.toList());
		genericJdbcRepository.batchInsert(
				TableNames.ORDER_SCHEMA.name().toLowerCase() + "." + TableNames.ORDER_ITEMS.name().toLowerCase(),
				itemMaps);
	}

	public Map<String, Object> fetchOrder(Long id) {
		logger.info("order Id ={}", id);
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("orderId", id);
		List<Map<String, Object>> order = genericJdbcRepository.fetchQuery("orderQuries.ftl", params);
		return order.get(0);
	}

	public List<Map<String, Object>> fetchListOrder(int page, int size) {
		logger.info("page number ={} and size ={} for the order ", page, size);
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("offset", (page - 1) * size);
		params.put("limit", size);
		List<Map<String, Object>> order = genericJdbcRepository.fetchQuery("orderQuries.ftl", params);
		return order;
	}

	public List<Map<String, Object>> fetchListOrderItem(Long orderId) {
		logger.info("Fetch order Item list by query using order Id ", orderId);
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("orderId", orderId);
		List<Map<String, Object>> orderItem = genericJdbcRepository.fetchQuery("orderItemQuries.ftl", params);
		return orderItem;
	}

	/*
	 * @Transactional public void createOrderWithItems(Orders order, List<OrderItem>
	 * items) { // Insert into orders table and get generated key KeyHolder
	 * keyHolder = new GeneratedKeyHolder(); jdbcTemplate.update(connection -> {
	 * System.out.println("Connection 1: " + connection); PreparedStatement ps =
	 * connection.prepareStatement(
	 * "INSERT INTO order_schema.orders (user_id, order_date, status, delivery_address) VALUES (?, ?, ?, ?)"
	 * , Statement.RETURN_GENERATED_KEYS ); ps.setLong(1, order.getUserId());
	 * ps.setTimestamp(2, Timestamp.valueOf(order.getOrderDate())); ps.setString(3,
	 * order.getStatus().name()); ps.setString(4, order.getDeliveryAddress());
	 * return ps; }, keyHolder);
	 * 
	 * Map<String, Object> keys = keyHolder.getKeys();
	 * System.out.println("Generated keys: " + keys); // Debug
	 * 
	 * Long orderId = ((Number) keys.get("id")).longValue(); // Use your PK column
	 * name System.out.println("Order ID: " + orderId);
	 * 
	 * // Insert into order_items table for (OrderItem item : items) {
	 * jdbcTemplate.update(connection -> { System.out.println("Connection 2: " +
	 * connection); PreparedStatement ps = connection.prepareStatement(
	 * "INSERT INTO order_schema.order_items (order_id, product_id, quantity, warehouse_id) VALUES (?, ?, ?, ?)"
	 * ); ps.setLong(1, orderId); ps.setLong(2, item.getProductId()); ps.setInt(3,
	 * item.getQuantity()); ps.setInt(4, 1); return ps; }); } }
	 */

	public void updateStatus(OrderStatusHandler statusHandler, Long orderId) {
		jdbcTemplate.update("UPDATE warehouse_order.orders SET status = ? WHERE id = ?",
				statusHandler.getHandledStatus().name(), orderId);

	}

}
