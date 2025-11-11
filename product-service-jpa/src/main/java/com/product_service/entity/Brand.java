package com.product_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "brands", schema = "product_schema")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Brand implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    private String country;

    // removed relationship from entity class for the improvement performance
//    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY) //non-owing / inverse side
//    private List<Product> products;
}
