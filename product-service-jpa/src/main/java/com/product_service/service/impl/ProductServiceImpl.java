package com.product_service.service.impl;

import com.product_service.entity.Product;
import com.product_service.repository.ProductRepository;
import com.product_service.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }
    @Override
    public Product save(Product product) {
        return repository.save(product);
    }

    @Override
    public List<Product> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Product> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Product update(Long id, Product newProduct) {
        return repository.findById(id)
                .map(product -> {
                    product.setName(newProduct.getName());
                    product.setDescription(newProduct.getDescription());
                    product.setPrice(newProduct.getPrice());
                    product.setAvailableQuantity(newProduct.getAvailableQuantity());
                    return repository.save(product);
                })
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Product> searchByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }
}
