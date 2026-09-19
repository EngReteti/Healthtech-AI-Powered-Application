package com.amason.hospitalinventory.controller;

import com.amason.hospitalinventory.model.StockMovement;
import com.amason.hospitalinventory.repository.StockMovementRepository;
import com.amason.hospitalinventory.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stock-movements")
public class StockMovementController {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @PostMapping
    public StockMovement recordMovement(@RequestBody StockMovement movement) {
        return stockService.recordMovement(movement);
    }

    // GET /api/stock-movements - returns every movement ever recorded, 
    // most recent logic handled on the frontend for now (sorting by 
    // id descending gives us newest-first without extra backend work)
    @GetMapping
    public List<StockMovement> getAllMovements() {
        return stockMovementRepository.findAll();
    }
}
