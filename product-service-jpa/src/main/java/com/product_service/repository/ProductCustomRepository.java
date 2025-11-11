package com.product_service.repository;

import com.product_service.entity.projection.ProductListDTO;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface ProductCustomRepository {

    List<ProductListDTO> findProductWithRelations();
//    Page<ProductProjection> findProductsWithBasicInfo(Pageable pageable);
}