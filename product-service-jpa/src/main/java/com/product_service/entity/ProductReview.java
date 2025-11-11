package com.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="product_review", schema = "product_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductReview implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String reviewerName;
    private Integer rating;
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @Column(name="product_id", nullable = false)
    private Integer productId;
}
