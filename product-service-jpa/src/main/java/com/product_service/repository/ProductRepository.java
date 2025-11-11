package com.product_service.repository;

import com.product_service.entity.Product;
import com.product_service.entity.projection.ProductListPageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

    @Query("""
        SELECT 
            p.id AS id,
            p.name AS name,
            p.price AS price,
            c.name AS categoryName,
            b.name AS brandName ,
            d.color AS color,
            d.size AS size
        FROM Product p 
        JOIN ProductCategory c ON c.id = p.categoryId
        JOIN Brand b ON b.id = p.brandId
        JOIN ProductDetails d ON d.product = p
            WHERE
                     (:search IS NULL OR :search = '' OR
                      p.name ILIKE CONCAT('%', :search, '%') OR
                      p.description ILIKE CONCAT('%', :search, '%') OR
                      c.name ILIKE CONCAT('%', :search, '%') OR
                      b.name ILIKE CONCAT('%', :search, '%') OR
                      d.color ILIKE CONCAT('%', :search, '%') OR
                      d.size ILIKE CONCAT('%', :search, '%')
                     )
        """)
    Page<ProductListPageDTO> findProductPagination(@Param("search") String search, Pageable pageable);
//    List<Product> findByNameContainingIgnoreCase(String name);
}
