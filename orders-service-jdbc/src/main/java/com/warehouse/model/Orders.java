package com.warehouse.model;

import java.time.LocalDateTime;
import java.util.List;

import com.warehouse.annotation.Column;
import com.warehouse.constant.OrderStatus;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class Orders {

	private Long id;
	
	@Column("user_id")
    private Long userId;
	
	@Column("order_date")
    private LocalDateTime orderDate;
	
	@Column("status")
    private OrderStatus status;
	
	@Column("delivery_address")
    private String deliveryAddress;
    
    private List<OrderItem> items;

    public static Orders orderMapper(Long id, Long userId, LocalDateTime orderDate,
                                     OrderStatus status, String deliveryAddress, List<OrderItem> items) {
        Orders order = Orders.builder()
                .id(id)
                .userId(userId)
                .orderDate(orderDate)
                .status(status)
                .deliveryAddress(deliveryAddress)
                .items(items)
                .build();

    	return order;
    }
}
