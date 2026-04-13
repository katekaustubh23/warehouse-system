package com.warehouse.inventory.mapper;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.warehouse.inventory.model.Inventory;

@Component("v2InventoryStrategy")
public class InventoryV2Strategy implements InventoryResponseStrategy{

	@Override
	public Map<String, Object> format(Inventory inventory) {
		return Map.of(
	            "id", inventory.getId(),
	            "productName", inventory.getName(),
	            "quantity", inventory.getQuantity(),
	            "category", inventory.getCategoryId(),
	            "location", inventory.getLocation()
	          );
	}

}
