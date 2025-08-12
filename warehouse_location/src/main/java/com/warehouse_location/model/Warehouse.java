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

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public Integer getMaxCapacity() {
//        return maxCapacity;
//    }
//
//    public void setMaxCapacity(Integer maxCapacity) {
//        this.maxCapacity = maxCapacity;
//    }
//
//    public Integer getUsedCapacity() {
//        return usedCapacity;
//    }
//
//    public void setUsedCapacity(Integer usedCapacity) {
//        this.usedCapacity = usedCapacity;
//    }
}
