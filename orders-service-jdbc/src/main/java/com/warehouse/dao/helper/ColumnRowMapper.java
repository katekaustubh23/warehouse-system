package com.warehouse.dao.helper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;


import org.springframework.jdbc.core.RowMapper;

import com.warehouse.utility.NamingUtils;

public class ColumnRowMapper implements RowMapper<Map<String, Object>>{

	@Override
	public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
		Map<String, Object> result = new LinkedHashMap<>();
		ResultSetMetaData meta = rs.getMetaData();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
            String column = meta.getColumnLabel(i);
            result.put(NamingUtils.toCamelCase(column), rs.getObject(i));
        }
		return result;
	}

	

}
