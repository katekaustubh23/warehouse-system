package com.warehouse.utility;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NamingUtils {

	public static String toCamelCase(String underscore) {

		if (underscore == null || underscore.isEmpty())
			return underscore;

		String[] parts = underscore.split("_");
		List<String> nonEmptyStrings = Arrays.stream(parts).filter(p -> !p.isEmpty()).collect(Collectors.toList());

		if (nonEmptyStrings.isEmpty())
			return "";

		StringBuilder result = new StringBuilder(parts[0]);
		for (int i = 1; i < nonEmptyStrings.size(); i++) {
			String part = nonEmptyStrings.get(i);
			result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
		}
		return result.toString();
	}
}
