package com.warehouse.inventory.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Inventory {
	private UUID id;
    private String name;
    private UUID categoryId;
    private int quantity;
    private String location;
    private UUID warehouseId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
