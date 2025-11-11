package com.product_service.service.impl;

import com.product_service.entity.Product;
import com.product_service.entity.dto.ProductRequest;
import com.product_service.entity.projection.ProductListDTO;
import com.product_service.entity.projection.ProductListPageDTO;
import com.product_service.repository.ProductRepository;
import com.product_service.repository.impl.ProductQueriesRepository;
import com.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepos;
    private final ProductQueriesRepository productQueriesRepository;

    @Override
    public Product save(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSku(request.getSku());
        product.setCategoryId(request.getCategoryId());
        product.setBrandId(request.getBrandId());
        product.setProductDetails(request.getDetails());

        if (request.getReviews() != null) {
            product.setProductReview(request.getReviews());
        }
        return productRepos.save(product);
    }

    @Override
    public List<ProductListDTO> getAll() {
        return productQueriesRepository.findProductWithRelations();
    }

    @Override
    public Optional<Product> getById(Long id) {
        return productRepos.findById(id);
    }

    @Override
    public Product update(Long id, Product newProduct) {
        return productRepos.findById(id)
                .map(product -> {
                    product.setName(newProduct.getName());
                    product.setDescription(newProduct.getDescription());
                    product.setPrice(newProduct.getPrice());
                    product.setAvailableQuantity(newProduct.getAvailableQuantity());
                    return productRepos.save(product);
                })
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public void delete(Long id) {
        productRepos.deleteById(id);
    }

    @Override
    public List<Product> searchByName(String name) {
        return null;//repository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public Page<ProductListPageDTO> getAllPaginated(String search, Pageable pageable) {
        return productRepos.findProductPagination(search, pageable);
    }
}
