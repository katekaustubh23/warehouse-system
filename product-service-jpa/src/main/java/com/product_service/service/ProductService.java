package com.product_service.service;

import com.product_service.entity.Product;
import com.product_service.entity.dto.ProductRequest;
import com.product_service.entity.projection.ProductListDTO;
import com.product_service.entity.projection.ProductListPageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product save(ProductRequest product);

    List<ProductListDTO> getAll();

    Optional<Product> getById(Long id);

    Product update(Long id, Product product);

    void delete(Long id);

    List<Product> searchByName(String name);

    Page<ProductListPageDTO> getAllPaginated(String search, Pageable pageable);
}
