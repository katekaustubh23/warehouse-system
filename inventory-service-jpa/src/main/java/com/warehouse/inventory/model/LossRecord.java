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
public class LossRecord {
	private UUID id;
    private UUID inventoryId;
    private LocalDate lossDate;
    private int quantityLost;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
