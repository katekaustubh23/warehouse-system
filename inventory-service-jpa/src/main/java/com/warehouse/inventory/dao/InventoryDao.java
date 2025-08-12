package com.warehouse.inventory.dao;

import java.util.List;

import com.warehouse.inventory.model.Inventory;

public interface InventoryDao {

	List<Inventory> findAll();
    Inventory findById(int id);
    Inventory save(Inventory inventory);
    void update(Inventory inventory);
    void delete(int id);
}
