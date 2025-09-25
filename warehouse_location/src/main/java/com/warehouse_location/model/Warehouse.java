package com.warehouse_location.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class Warehouse {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotNull
    @Min(0)
    private Integer maxCapacity;
    @NotNull @Min(0)
    private Integer usedCapacity;

}
