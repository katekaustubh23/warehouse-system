package com.product_service.repository.impl;

import com.product_service.entity.projection.ProductListDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class ProductQueriesRepository {

    @PersistenceContext
    private EntityManager entityManager;


    public List<ProductListDTO> findProductWithRelations() {
        String jpql = """
        SELECT new com.product_service.entity.projection.ProductListDTO(
            p.id,
            p.name,
            p.price,
            d.color,
            d.size,
            c.name,
            b.name
        )
        FROM Product p
        JOIN ProductCategory c ON c.id = p.categoryId
        JOIN ProductDetails d ON d.product = p
        JOIN Brand b ON b.id = p.brandId
        """;

        return entityManager.createQuery(jpql, ProductListDTO.class).getResultList();
    }
}
