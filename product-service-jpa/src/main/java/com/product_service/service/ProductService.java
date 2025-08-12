package com.product_service.service;

import com.product_service.entity.Product;
import org.springframework.boot.context.properties.bind.BindResult;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product save(Product product);

    List<Product> getAll();

    Optional<Product> getById(Long id);

    Product update(Long id, Product product);

    void delete(Long id);

    List<Product> searchByName(String name);
}
