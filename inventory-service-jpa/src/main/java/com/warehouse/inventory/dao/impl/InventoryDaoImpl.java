package com.warehouse.inventory.dao.impl;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.warehouse.inventory.model.InventoryEvent;
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
                UPDATE inventory
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

}
