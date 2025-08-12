package com.warehouse.dao.mapper;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

public class MapToObjectMapper {

	@SuppressWarnings("unchecked")
	public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz) {
		try {
			T instance = clazz.getDeclaredConstructor().newInstance();

			for (Field field : clazz.getDeclaredFields()) {
				field.setAccessible(true);

				String fieldName = field.getName();
				Object value = map.get(fieldName);

				if (value != null) {
					if (field.getType().isEnum()) {
						value = Enum.valueOf((Class<Enum>) field.getType(), value.toString());
					} else if (field.getType() == LocalDateTime.class && value instanceof Timestamp) {
						value = ((Timestamp) value).toLocalDateTime();
					}
					field.set(instance, value);
				}
			}

			return instance;

		} catch (Exception e) {
			throw new RuntimeException("Failed to map map to object", e);
		}
	}
}
