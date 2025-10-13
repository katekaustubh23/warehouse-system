package com.warehouse.controller;

import java.util.List;
import java.util.Map;

import com.common.response.ApiResponse;
import com.common.response.ResponseUtils;
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
import com.warehouse.response.ApiResponseOld;
import com.warehouse.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

	private final OrderService orderService;
	private final ResponseUtils responseUtils;

	public OrderController(OrderService orderService,ResponseUtils responseUtils) {
		this.orderService = orderService;
		this.responseUtils = responseUtils;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Long>> placeOrder(@RequestBody Orders order) {
		Long id = orderService.placeOrder(order);
		return responseUtils.buildSuccess(id, MessageString.CREATED_DATA.toString(), HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Map<String, Object>>> getOrder(@PathVariable("id") Long id) {
		return  responseUtils.buildSuccess(orderService.getOrder(id),
				MessageString.FETCHED_DATA.toString(),
				HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<ApiResponse<Object>> listOrders(@RequestParam("page") Integer page,
																@RequestParam("size") Integer size) {
		return responseUtils.buildSuccess(orderService.listOrders(page, size),
				MessageString.FETCHED_DATA.toString(),
				HttpStatus.OK);
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<ApiResponse<String>> updateStatus(@PathVariable Long id, @RequestParam String status) {
		orderService.updateStatus(id, status);
		return responseUtils.buildSuccess("Status has been updated",
				MessageString.FETCHED_DATA.toString(),
				HttpStatus.OK);
	}
}
