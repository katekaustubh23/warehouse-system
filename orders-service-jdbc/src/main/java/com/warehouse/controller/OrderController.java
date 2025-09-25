package com.warehouse.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.warehouse.constant.MessageString;
import com.warehouse.model.Orders;
import com.warehouse.response.ApiResponse;
import com.warehouse.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	public ApiResponse<Long> placeOrder(@RequestBody Orders order) {
		Long id = orderService.placeOrder(order);
		return ApiResponse.success(MessageString.CREATED_DATA.toString(), id, HttpStatus.CREATED.value());
	}

	@GetMapping("/{id}")
	public ApiResponse<Map<String, Object>> getOrder(@PathVariable("id") Long id) {
		return  ApiResponse.success(MessageString.FETCHED_DATA.toString(), orderService.getOrder(id), HttpStatus.OK.value());
	}

	@GetMapping
	public ApiResponse<List<Map<String, Object>>> listOrders(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return ApiResponse.success(MessageString.FETCHED_DATA.toString(),orderService.listOrders(page, size), HttpStatus.OK.value());
	}

	@PutMapping("/{id}/status")
	public ApiResponse<String> updateStatus(@PathVariable Long id, @RequestParam String status) {
		orderService.updateStatus(id, status);
		return ApiResponse.success(MessageString.FETCHED_DATA.toString(),"Status has been updated", HttpStatus.OK.value());
	}
}
