package com.warehouse_location.dao;

import com.warehouse_location.model.Warehouse;
import com.warehouse_location.service.WarehouseService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class WarehouseRepository {

    private final JdbcTemplate jdbcTemplate;

    public WarehouseRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    private Warehouse mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Warehouse w = new Warehouse();
        w.setId(rs.getLong("id"));
        w.setName(rs.getString("name"));
        w.setAddress(rs.getString("address"));
        w.setMaxCapacity(rs.getInt("max_capacity"));
        w.setUsedCapacity(rs.getInt("used_capacity"));
        return w;
    }

    public List<Warehouse> findAll() {
        return jdbcTemplate.query("SELECT * FROM warehouse_schema.warehouse ORDER BY id", this::mapRow);
    }

    public Warehouse findById(Long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM warehouse_schema.warehouse WHERE id = ?", this::mapRow, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Long save(Warehouse w) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO warehouse_schema.warehouse (name, address, max_capacity, used_capacity) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, w.getName());
            ps.setString(2, w.getAddress());
            ps.setInt(3, w.getMaxCapacity());
            ps.setInt(4, w.getUsedCapacity() != null ? w.getUsedCapacity() : 0);
            return ps;
        }, keyHolder);
        Number idNumber = (Number) keyHolder.getKeys().get("id");
        return idNumber.longValue();
    }

    public int update(Warehouse w) {
        return jdbcTemplate.update(
                "UPDATE warehouse_schema.warehouse SET name=?, address=?, max_capacity=?, used_capacity=?, updated_at=now() WHERE id=?",
                w.getName(), w.getAddress(), w.getMaxCapacity(), w.getUsedCapacity(), w.getId()
        );
    }

    public int delete(Long id) {
        return jdbcTemplate.update("DELETE FROM warehouse_schema.warehouse WHERE id=?", id);
    }
}
