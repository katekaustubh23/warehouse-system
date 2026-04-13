package com.warehouse.inventory.mapper;

import java.util.Map;

import com.warehouse.inventory.model.Inventory;

public interface InventoryResponseStrategy {
	Map<String, Object> format(Inventory inventory);
}
