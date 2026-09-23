package com.amason.hospitalinventory.controller;

import com.amason.hospitalinventory.model.StockBatch;
import com.amason.hospitalinventory.model.Product;
import com.amason.hospitalinventory.repository.StockBatchRepository;
import com.amason.hospitalinventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stock-batches")
public class StockBatchController {

    @Autowired
    private StockBatchRepository stockBatchRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<StockBatch> getAllBatches() {
        return stockBatchRepository.findAll();
    }

    // GET /api/stock-batches/expiring?days=30 - reuses the repository 
    // method we designed way back in Phase 4, finally put to use
    @GetMapping("/expiring")
    public List<StockBatch> getExpiringBatches(@RequestParam(defaultValue = "30") int days) {
        LocalDate cutoff = LocalDate.now().plusDays(days);
        return stockBatchRepository.findByExpiryDateLessThanEqual(cutoff);
    }

    @PostMapping
    public StockBatch createBatch(@RequestBody StockBatch batch) {
        // Re-fetch the full product, same pattern we used to fix the 
        // null-fields issue on StockMovement earlier - ensures the 
        // response shows real product data, not just an echoed {id: X}
        Product product = productRepository.findById(batch.getProduct().getId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        batch.setProduct(product);

        return stockBatchRepository.save(batch);
    }
}
