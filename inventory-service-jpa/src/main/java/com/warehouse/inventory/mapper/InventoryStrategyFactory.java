package com.warehouse.inventory.mapper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class InventoryStrategyFactory {
	
	private final Map<String, InventoryResponseStrategy> strategyMap;
	
	public InventoryStrategyFactory( 
			@Qualifier("v1InventoryStrategy") InventoryResponseStrategy v1,
	        @Qualifier("v2InventoryStrategy") InventoryResponseStrategy v2) {
		strategyMap = new HashMap<>();
		strategyMap.put(VersionEnum.V1.toString().toLowerCase(), v1);
		strategyMap.put(VersionEnum.V2.toString().toLowerCase(), v2);
	}
	
	public InventoryResponseStrategy resolved(String version) {
		return strategyMap.getOrDefault(version, strategyMap.get(VersionEnum.V1.toString().toLowerCase()));
	}
}
