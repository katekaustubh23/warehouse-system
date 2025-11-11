package com.product_service.controller;

import com.common.response.ApiResponse;
import com.common.response.ResponseUtils;
import com.product_service.entity.Product;
import com.product_service.entity.dto.ProductRequest;
import com.product_service.entity.projection.ProductListDTO;
import com.product_service.entity.projection.ProductListPageDTO;
import com.product_service.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    private final ProductService service;

    private final ResponseUtils responseUtils;

    public ProductController(
            ProductService service,
            ResponseUtils responseUtils
    ) {
        this.service = service;
        this.responseUtils = responseUtils;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> create(@RequestBody ProductRequest product) {
        return responseUtils.buildSuccess(service.save(product), "Product Created", HttpStatus.CREATED);
//                new ResponseEntity<>(service.save(product), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductListDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(service.update(id, product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> search(@RequestParam String name) {
        return ResponseEntity.ok(service.searchByName(name));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<Page<ProductListPageDTO>>> getAllPaginated(@RequestParam(value = "search", required = false) String search,
                                                                                 @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<ProductListPageDTO> productPage = service.getAllPaginated(search, pageable);
        return responseUtils.buildSuccess(productPage, "Products retrieved successfully", HttpStatus.OK);
    }
}
