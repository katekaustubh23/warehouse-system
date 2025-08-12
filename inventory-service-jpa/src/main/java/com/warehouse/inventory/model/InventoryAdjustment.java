package com.warehouse.inventory.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryAdjustment {

	private UUID id;
	private UUID inventoryId;
	private LocalDate adjustmentDate;
	private int quantityChange;
	private String reason;
	private String adjustedBy;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
