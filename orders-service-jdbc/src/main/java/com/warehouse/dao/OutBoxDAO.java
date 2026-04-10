package com.warehouse.dao;

import com.warehouse.constant.TableNames;
import com.warehouse.dao.helper.GenericJdbcRepository;
import com.warehouse.model.OutBox;
import com.warehouse.utility.EntityMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class OutBoxDAO {

    private final JdbcTemplate jdbcTemplate;

    private GenericJdbcRepository genericJdbcRepository;

    public void saveOutBox(OutBox outBox) {
            genericJdbcRepository.insert(
                    TableNames.ORDER_SCHEMA.name().toLowerCase()+"." + TableNames.OUTBOX.name().toLowerCase(),
                    EntityMapper.toMap(outBox));
    }

    public List<OutBox> getPendingMessages() {
        return null;
    }
}
