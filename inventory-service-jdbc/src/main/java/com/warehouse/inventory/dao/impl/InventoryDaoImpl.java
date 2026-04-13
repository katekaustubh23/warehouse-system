package com.warehouse.inventory.dao.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.inventory.grpc.OrderItem;
import com.warehouse.inventory.model.InventoryEvent;
import com.warehouse.inventory.model.InventoryReserved;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.warehouse.inventory.dao.InventoryDao;
import com.warehouse.inventory.model.Inventory;

@Repository
public class InventoryDaoImpl implements InventoryDao{

    private final Logger logger = LoggerFactory.getLogger(InventoryDaoImpl.class);
	private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Properties queries;
    private final RedisTemplate<String, Object> redisTemplate;
    
    public InventoryDaoImpl(NamedParameterJdbcTemplate jdbcTemplate,
                            @Qualifier("inventoryQueries") Properties queries,
                            RedisTemplate<String, Object> redisTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.queries = queries;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<Inventory> findAll() {
        System.out.println("************************* " + java.util.TimeZone.getDefault().getID());
        return jdbcTemplate.query(
            queries.getProperty("inventory.getAll"),
            new BeanPropertyRowMapper<>(Inventory.class)
        );
    }

    @Override
    public Inventory findById(int id) {
        Map<String, Object> params = Map.of("id", id);

        String key = "stock:product:" + id;
        String key2 = "data:product:" + id;
        Object cachedStock = redisTemplate.opsForValue().get(key);
        Map<Object, Object> invObject = redisTemplate.opsForHash().entries(key2);
        if(cachedStock != null && !invObject.isEmpty()){
            logger.info("Cache hit for productId=" + id + ", stock=" + cachedStock);
            Inventory inv = new Inventory();
            inv.setProductId(Long.valueOf(id));
            inv.setQuantity((Integer) cachedStock);
            inv.setId(UUID.fromString(invObject.get("id").toString()));
            inv.setName(invObject.get("itemName").toString());
            return inv;

        }
        Inventory inventory = jdbcTemplate.queryForObject(
            queries.getProperty("inventory.getById"),
            new MapSqlParameterSource(params),
            new BeanPropertyRowMapper<>(Inventory.class)
        );

        if(inventory != null){
            Map<String, Object> map = new HashMap<>();
            map.put("id", inventory.getId());
            map.put("itemName", inventory.getName());
            map.put("productId", inventory.getProductId());
            map.put("quantity", inventory.getQuantity());

            redisTemplate.opsForValue().set(key,inventory.getQuantity(),10, TimeUnit.MINUTES);
            redisTemplate.opsForHash().putAll(key2,map);
            redisTemplate.expire(key2,10, TimeUnit.MINUTES);
        }

        return inventory;
    }

    @Override
    public Inventory save(Inventory inv) {
        Map<String, Object> params = Map.of(
            "name", inv.getName(),
            "category_id", inv.getCategoryId(),
            "quantity", inv.getQuantity(),
            "location", inv.getLocation(),
            "warehouse_id", inv.getWarehouseId()
        );
        return jdbcTemplate.queryForObject(
                queries.getProperty("inventory.insert.returning"),
                new MapSqlParameterSource(params),
                new BeanPropertyRowMapper<>(Inventory.class)
            );
    }

    @Override
    public void update(Inventory inv) {
        Map<String, Object> params = Map.of(
            "name", inv.getName(),
            "category_id", inv.getCategoryId(),
            "quantity", inv.getQuantity(),
            "location", inv.getLocation(),
            "warehouse_id", inv.getWarehouseId(),
            "id", inv.getId()
        );
        jdbcTemplate.update(queries.getProperty("inventory.update"), new MapSqlParameterSource(params));
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update(
            queries.getProperty("inventory.delete"),
            Map.of("id", id)
        );
    }

    @Override
    public void quantityUpdate(int productId, int quantityChange) {
        String sql = """
                UPDATE inventory_schema.inventory
                        SET quantity = quantity + :quantityChange
                        WHERE product_id = :productId
                """;
        jdbcTemplate.update(
            sql,
            Map.of("quantityChange", quantityChange, "productId", productId)
        );
    }

    @Override
    public String allocateInventory(InventoryEvent event) {
//        Map<String, Object> warehouse = repo.findWarehouseWithMaxStock(event.getProductId());
//        if (warehouse == null || (int) warehouse.get("quantity") < event.getQuantity()) {
//            return "FAILED";
//        }

        Long warehouseId =  101L;//((Number) warehouse.get("warehouse_id")).longValue();
//        repo.deductStock(warehouseId, event.getProductId(), event.getQuantity());
        return "ALLOCATED to warehouse " + warehouseId;
    }

    /**
     *  function to store reserve quantity into DB and set expiry time for reservation
     *  */
    @Override
    public InventoryReserved updateReserveQuantity(InventoryReserved reserved){
        Map<String, Object> params = Map.of(
                "orderId", reserved.getOrderId(),
                "productId", reserved.getProductId(),
                "quantity", reserved.getQuantity(),
                "reserved", reserved.getStatus(),
                "expiredAt", reserved.getExpiredAt()
        );
        String sql = """
                INSERT INTO inventory_schema.reserved_qnty (order_id, product_id, quantity, status, expiry_time, created_at, updated_at)
                VALUES (:orderId, :productId, :quantity, :reserved, :expiredAt, NOW(), NOW())
                ON CONFLICT (order_id) DO UPDATE SET
                    quantity = EXCLUDED.quantity,
                    status = EXCLUDED.status,
                    expiry_time = EXCLUDED.expiry_time
                RETURNING id
                """;
        Integer id = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(params), Integer.class);
        reserved.setId(id);
        return reserved;
    }

    @Override
    public void batchSaveReserveQuantity(List<InventoryReserved> reserveds) {
        String sql = """
        INSERT INTO inventory_schema.reserved_qnty 
        (order_id, product_id, quantity, status, expiry_time, created_at, updated_at)
        VALUES (:orderId, :productId, :quantity, :status, :expiryTime, NOW(), NOW())
        ON CONFLICT (order_id, product_id) DO UPDATE SET
            quantity = EXCLUDED.quantity,
            status = EXCLUDED.status,
            expiry_time = EXCLUDED.expiry_time
        """;

        List<MapSqlParameterSource> batchParams = reserveds.stream()
                .map(item -> new MapSqlParameterSource()
                        .addValue("orderId", item.getOrderId())
                        .addValue("productId", item.getProductId())
                        .addValue("quantity", item.getQuantity())
                        .addValue("status", "RESERVED")
                        .addValue("expiryTime", item.getExpiredAt())
                )
                .toList();

        jdbcTemplate.batchUpdate(sql, batchParams.toArray(new MapSqlParameterSource[0]));
    }

    @Override
    public List<InventoryReserved> findByOrderId(Long orderId) {

        String sql = """
        SELECT id, order_id, product_id, quantity, status, expiry_time
        FROM inventory_schema.reserved_qnty
        WHERE order_id = :orderId
    """;

        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {

            InventoryReserved obj = new InventoryReserved();

            obj.setId(rs.getInt("id"));
            obj.setOrderId(rs.getLong("order_id"));
            obj.setProductId(rs.getLong("product_id"));
            obj.setQuantity(rs.getInt("quantity"));
            obj.setStatus(rs.getString("status"));

            Timestamp expiry = rs.getTimestamp("expiry_time");
            if (expiry != null) {
                obj.setExpiredAt(expiry.toLocalDateTime());
            }

            return obj;
        });
    }

    /* release stock for expired order */
    @Override
    public int releasedStock(Long orderId) {
        String sql = """
                WITH updated_rows AS (
                    SELECT product_id, quantity
                    FROM inventory_schema.reserved_qnty
                    WHERE order_id = :orderId
                      AND status = 'RESERVED'
                )
                UPDATE inventory_schema.inventory i
                SET quantity = i.quantity + ur.quantity
                FROM updated_rows ur
                WHERE i.product_id = ur.product_id;
                """;


        return jdbcTemplate.update(sql, Map.of("orderId", orderId));
    }

    /*** mark the reservation as released after stock is released for expired order*/
    @Override
    public void makeReleased(Long orderId) {
        String sql = """
        UPDATE inventory_schema.reserved_qnty
        SET status = 'RELEASED',
            updated_at = EXTRACT(EPOCH FROM NOW()) * 1000
        WHERE order_id = :orderId
          AND status = 'RESERVED'
    """;

        jdbcTemplate.update(sql, Map.of("orderId", orderId));
    }

    @Override
    public void updateReservationStatus(Long orderId, String confirmed) {
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);
        params.put("status", confirmed);
        params.put("updatedAt", LocalDateTime.now());
        String sql = """
            UPDATE inventory_schema.reserved_qnty
            SET status = :status,
                updated_at = :updatedAt
            WHERE order_id = :orderId
              AND status = 'RESERVED';
        """;

        jdbcTemplate.update(sql, params);
    }

}
