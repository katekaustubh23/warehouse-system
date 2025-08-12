package com.warehouse.inventory.mapper;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.warehouse.inventory.model.Inventory;

@Component("v1InventoryStrategy")
public class InventoryV1Strategy implements InventoryResponseStrategy {

	@Override
	public Map<String, Object> format(Inventory inventory) {
		return Map.of(
	            "id", inventory.getId(),
	            "itemName", inventory.getName(),
	            "quantity", inventory.getQuantity()
	        );
	}

}
