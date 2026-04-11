package com.warehouse.dao.helper;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

/**
 * A generic repository to handle dynamic JDBC insert operations using NamedParameterJdbcTemplate.
 * This helps avoid boilerplate insert logic for each table and supports dynamic table and column handling.
 */
@Component
public class GenericJdbcRepository {

	private final Logger logger = LoggerFactory.getLogger(GenericJdbcRepository.class);

	private final NamedParameterJdbcTemplate namedJdbcTemplate;
	
	private final QueryExecutor sqlTemplateRenderer;

	public GenericJdbcRepository(NamedParameterJdbcTemplate namedJdbcTemplate,
			QueryExecutor sqlTemplateRenderer) {
		this.namedJdbcTemplate = namedJdbcTemplate;
		this.sqlTemplateRenderer = sqlTemplateRenderer;
	}
	
	// --- COMMON SQL BUILDER ---
    private String buildInsertSQL(String tableName, Set<String> columns) {
        String columnSql = String.join(", ", columns);
        String placeholderSql = columns.stream()
                .map(col -> ":" + col)
                .collect(Collectors.joining(", "));
        return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columnSql, placeholderSql);
    }

	// --- COMMON UPDATE SQL BUILDER ---
	private StringBuilder buildUpdateSQL(String tableName, Map<String, Object> columns) {
		StringBuilder setClause = new StringBuilder();
		setClause.append("UPDATE ").append(tableName).append(" SET ");
		boolean isCommaSeparation = true;
		for (String fieldName : columns.keySet()) {
			if(!isCommaSeparation){
				setClause.append(", ");
			}
			setClause.append(fieldName).append(" = :").append(fieldName);
			isCommaSeparation = false;
		}
		setClause.append(" WHERE ");
		return setClause;
	}

	 /**
     * Generic method to insert a record into any table dynamically.
     * 
     * @param tableName    the name of the table to insert data into
     * @param columnValues a map of column names to their values
     * @return generated primary key (if applicable), else null
     */
	public Optional<Long> insert(String tableName, Map<String, Object> columnValues) {
		long start = System.currentTimeMillis();
        String sql = buildInsertSQL(tableName, columnValues.keySet());
		logger.info("SQL query :: " + sql);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedJdbcTemplate.update(sql, new MapSqlParameterSource(columnValues), keyHolder, new String[] {"id"});

        logDuration("Insert", tableName, start);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
	}
	
	/**
	 * Generic batch insert into any table using NamedParameterJdbcTemplate.
	 *
	 * @param tableName the name of the table
	 * @param records   list of maps representing rows (column name → value)
	 * @return array of update counts
	 */
	public int[] batchInsert(String tableName, List<Map<String, Object>> records) {
		 if (records == null || records.isEmpty()) return new int[0];

	        long start = System.currentTimeMillis();
	        Set<String> columns = records.get(0).keySet();
	        String sql = buildInsertSQL(tableName, columns);

	        MapSqlParameterSource[] batchParams = records.stream()
	                .map(MapSqlParameterSource::new)
	                .toArray(MapSqlParameterSource[]::new);

	        int[] result = namedJdbcTemplate.batchUpdate(sql, batchParams);
	        logDuration("BatchInsert", tableName, start);
	        return result;
	}
	
	public List<Map<String, Object>> fetchQuery(String ftlFile, Map<String, Object> params){
		String sql = sqlTemplateRenderer.render(ftlFile, params);
		return namedJdbcTemplate.query(sql, new MapSqlParameterSource(params), new ColumnMapRowMapper());
	}

	private void logDuration(String label, String tableName, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        logger.info("[{}] completed for {} in {} ms", label, tableName, duration);
        if (duration > 1000) {
            logger.warn(" [{}] Slow operation on {}: {} ms", label, tableName, duration);
        }
    }

	/**
	 * Generic function to update Order table on multiple field using NamedParameterJdbcTemplate
	 * @param s table name
	 * @param mapColumns column name and value to update
	 * @param id record id to update
	 * @return number of rows updated
	 *  */
	public Optional<Long> updateOrder(String tableName, Map<String, Object> mapColumns, Long id) {
		long start = System.currentTimeMillis();
		StringBuilder sql = buildUpdateSQL(tableName, mapColumns);
		Map<String, Object> params = new HashMap<>(mapColumns);
		sql.append("id = :id");
		for (String key : mapColumns.keySet()) {
			params.put(key, mapColumns.get(key));
		}
		params.put("id", id);
		logger.info("SQL Update query :: " + sql + " update for Id : " + id);
		int rowsUpdated = namedJdbcTemplate.update(sql.toString(), params);
		logDuration("Update", tableName, start);
		return Optional.of(Long.valueOf(rowsUpdated));
	}
}
