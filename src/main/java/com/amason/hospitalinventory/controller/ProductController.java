package com.amason.hospitalinventory.controller;

import com.amason.hospitalinventory.model.Product;
import com.amason.hospitalinventory.repository.ProductRepository;
import com.amason.hospitalinventory.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockService stockService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    // GET /api/products/{id}/stock - returns just the current calculated
    // stock level for one product, using the same logic our backend
    // safety checks already rely on
    @GetMapping("/{id}/stock")
    public int getCurrentStock(@PathVariable Long id) {
        return stockService.calculateCurrentStock(id);
    }
}
