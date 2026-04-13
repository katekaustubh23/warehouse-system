package com.warehouse.inventory.dao;

import java.util.List;

import com.warehouse.inventory.model.Inventory;
import com.warehouse.inventory.model.InventoryEvent;
import com.warehouse.inventory.model.InventoryReserved;

public interface InventoryDao {

	List<Inventory> findAll();
    Inventory findById(int id);
    Inventory save(Inventory inventory);
    void update(Inventory inventory);
    void delete(int id);

    void quantityUpdate(int productId, int quantityChange);
    String allocateInventory(InventoryEvent event);

    InventoryReserved updateReserveQuantity(InventoryReserved reserved);

    void batchSaveReserveQuantity(List<InventoryReserved> reserveds);

    List<InventoryReserved> findByOrderId(Long orderId);

    int releasedStock(Long orderId);

    void makeReleased(Long orderId);

    void updateReservationStatus(Long orderId, String confirmed);
}
