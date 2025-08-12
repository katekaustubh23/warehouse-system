package com.warehouse.inventory.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.warehouse.inventory.dao.InventoryDao;
import com.warehouse.inventory.model.Inventory;

@Repository
public class InventoryDaoImpl implements InventoryDao{
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Properties queries;
    
    public InventoryDaoImpl(NamedParameterJdbcTemplate jdbcTemplate, @Qualifier("inventoryQueries") Properties queries) {
        this.jdbcTemplate = jdbcTemplate;
        this.queries = queries;
    }

    @Override
    public List<Inventory> findAll() {
        return jdbcTemplate.query(
            queries.getProperty("inventory.getAll"),
            new BeanPropertyRowMapper<>(Inventory.class)
        );
    }

    @Override
    public Inventory findById(int id) {
        Map<String, Object> params = Map.of("id", id);
        return jdbcTemplate.queryForObject(
            queries.getProperty("inventory.fetch.by.id"),
            new MapSqlParameterSource(params),
            new BeanPropertyRowMapper<>(Inventory.class)
        );
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

}
