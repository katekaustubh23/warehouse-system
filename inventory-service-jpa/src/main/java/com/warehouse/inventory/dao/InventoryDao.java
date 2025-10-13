package com.warehouse.inventory.dao;

import java.util.List;

import com.warehouse.inventory.model.Inventory;
import com.warehouse.inventory.model.InventoryEvent;

public interface InventoryDao {

	List<Inventory> findAll();
    Inventory findById(int id);
    Inventory save(Inventory inventory);
    void update(Inventory inventory);
    void delete(int id);
    String allocateInventory(InventoryEvent event);
}
