package com.warehouse_location.controller;

import com.warehouse_location.dao.WarehouseRepository;
import com.warehouse_location.model.Warehouse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouses")
public class WarehouseController {

    private final WarehouseRepository warehouseRepository;

    public WarehouseController(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @GetMapping
    public List<Warehouse> getAll() {
        return warehouseRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Warehouse> getById(@PathVariable Long id) {
        Warehouse w = warehouseRepository.findById(id);
        return w != null ? ResponseEntity.ok(w) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Warehouse> create(@Valid @RequestBody Warehouse warehouse) {
        Long id = warehouseRepository.save(warehouse);
        warehouse.setId(id);
        return ResponseEntity.ok(warehouse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Warehouse> update(@PathVariable Long id, @Valid @RequestBody Warehouse warehouse) {
        warehouse.setId(id);
        int updated = warehouseRepository.update(warehouse);
        if (updated > 0) {
            return ResponseEntity.ok(warehouseRepository.findById(id));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        int deleted = warehouseRepository.delete(id);
        if (deleted > 0) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
