package com.warehouse.utility;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import com.warehouse.annotation.Column;

public class EntityMapper {

	public static <T> Map<String, Object> toMap(T object) {
		Map<String, Object> mapColumn = new LinkedHashMap<>();
		for (Field field : object.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			try {
				String columnName = null;
				if (field.isAnnotationPresent(Column.class)) {
					columnName = field.getAnnotation(Column.class).value();
				}
				Object value = field.get(object);

				// Convert Enum to string
				if (value instanceof Enum<?>) {
					value = ((Enum<?>) value).name().toString();
				}

				if (columnName != null && value != null) {
					mapColumn.put(columnName, value);
				}

			} catch (Exception e) {
				throw new RuntimeException("Error on mapping entity ", e);
			}
		}
		return mapColumn;
	}
}
