package com.amason.hospitalinventory.controller;

import com.amason.hospitalinventory.dto.ReconciliationRequest;
import com.amason.hospitalinventory.dto.ReconciliationResult;
import com.amason.hospitalinventory.repository.UserRepository;
import com.amason.hospitalinventory.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reconciliation")
public class ReconciliationController {

    @Autowired
    private StockService stockService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ReconciliationResult reconcile(@RequestBody ReconciliationRequest request,
                                            Authentication authentication) {
        Long performedById = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"))
            .getId();

        return stockService.reconcile(request.getProductId(), request.getCountedQuantity(), performedById);
    }
}
