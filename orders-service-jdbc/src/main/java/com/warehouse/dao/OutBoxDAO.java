package com.warehouse.dao;

import com.warehouse.constant.TableNames;
import com.warehouse.dao.helper.GenericJdbcRepository;
import com.warehouse.model.OutBox;
import com.warehouse.utility.EntityMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
@RequiredArgsConstructor
public class OutBoxDAO {

    private final JdbcTemplate jdbcTemplate;

    private final GenericJdbcRepository genericJdbcRepository;

    public void saveOutBox(OutBox outBox) {
            genericJdbcRepository.insert(
                    TableNames.ORDER_SCHEMA.name().toLowerCase()+"." + TableNames.OUTBOX.name().toLowerCase(),
                    EntityMapper.toMap(outBox));
    }

    public List<OutBox> getPendingMessages() {
        return null;
    }

    public List<OutBox> findTop50ByStatus(String name) {
        String sql = "SELECT * FROM "
                + TableNames.ORDER_SCHEMA.name().toLowerCase() + "." + TableNames.OUTBOX.name().toLowerCase()
                + " WHERE status = ? ORDER BY created_at ASC LIMIT 50";
        return jdbcTemplate.query(sql, new Object[]{name}, (rs, rowNum) -> {
            OutBox outBox = new OutBox();
            outBox.setId(rs.getLong("id"));
            outBox.setType(rs.getString("type"));
            outBox.setPayload(rs.getString("payload"));
            outBox.setStatus(rs.getString("status"));
            outBox.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return outBox;
        });
    }

    public int updateOutBox(Long orderId, String status) {
//        Map<String, Object> params = new HashMap<>();
//        params.put("status", status);
//        params.put("id", orderId);
        String sql = "UPDATE "
                + TableNames.ORDER_SCHEMA.name().toLowerCase() + "." + TableNames.OUTBOX.name().toLowerCase()
                + " SET status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status, orderId);
    }
}
