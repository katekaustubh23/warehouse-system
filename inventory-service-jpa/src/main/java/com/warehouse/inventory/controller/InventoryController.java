package com.warehouse.inventory.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.warehouse.inventory.constant.MessageString;
import com.warehouse.inventory.dao.InventoryDao;
import com.warehouse.inventory.exceptionhandler.ResourceNotFoundException;
import com.warehouse.inventory.mapper.InventoryStrategyFactory;
import com.warehouse.inventory.model.Inventory;
import com.warehouse.inventory.responce.ApiResponse;

@RestController
@RequestMapping("/api/{version}/inventory")
public class InventoryController {

	private final InventoryDao inventoryDao;
	private final InventoryStrategyFactory inventoryFactory;

	public InventoryController(InventoryDao inventoryDao, InventoryStrategyFactory inventoryFactory) {
		this.inventoryDao = inventoryDao;
		this.inventoryFactory = inventoryFactory;
	}

	@GetMapping()
	public ApiResponse<List<Map<String, Object>>> getAll(@PathVariable String version) {
		List<Inventory> list = inventoryDao.findAll();
		List<Map<String, Object>> result = list.stream().map(inventoryFactory.resolved(version)::format).toList();
		return ApiResponse.success(MessageString.FETCH_ALL_DATA.getDisplayName(), result, HttpStatus.OK.value());
	}

	@GetMapping("/{id}")
	public ApiResponse<Map<String, Object>> getById(@PathVariable int id, @PathVariable String version) {
		Inventory inv = inventoryDao.findById(id);
		if (inv == null) {
			throw new ResourceNotFoundException("Inventory not found with ID: " + id);
		}
		Map<String, Object> result = inventoryFactory.resolved(version).format(inv);

		return ApiResponse.success(MessageString.FETCHED_DATA.getDisplayName(), result, HttpStatus.OK.value());
	}

	@PostMapping
	public ApiResponse<Map<String, Object>> create(@RequestBody Inventory inv, @PathVariable String version) {
		Inventory invSaved = inventoryDao.save(inv);
		Map<String, Object> result = inventoryFactory.resolved(version).format(invSaved);
		return ApiResponse.success(MessageString.CREATED_DATA.getDisplayName(), result, HttpStatus.CREATED.value());
	}

	@PutMapping("/{id}")
	public ApiResponse<String> update(@PathVariable UUID id, @RequestBody Inventory inv) {
		inv.setId(id);
		inventoryDao.update(inv);
		return ApiResponse.success(MessageString.UPDATED_DATA.getDisplayName(), String.valueOf(id),
				HttpStatus.OK.value());
	}

	@DeleteMapping("/{id}")
	public ApiResponse<String> delete(@PathVariable int id) {
		inventoryDao.delete(id);
		return ApiResponse.success(MessageString.FETCHED_DATA.getDisplayName(), String.valueOf(id),
				HttpStatus.OK.value());
	}
}