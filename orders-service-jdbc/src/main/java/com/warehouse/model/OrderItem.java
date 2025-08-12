package com.warehouse.model;

import com.warehouse.annotation.Column;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class OrderItem {
	
	private Long id;
	
	@Column("order_id")
	private Long orderId;
	
	@Column("product_id")
	private Long productId;
	
	@Column("quantity")
	private int quantity;
	
	@Column("warehouse_id")
	private Long warehouseId;

}
