package com.warehouse.model;

import com.warehouse.annotation.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OutBox {
    private Long id;
    @Column("payload")
    private String payload;
    @Column("type")
    private String type;
    @Column("status")
    private String status;

    @Column("created_at")
    private LocalDateTime createdAt;


}
