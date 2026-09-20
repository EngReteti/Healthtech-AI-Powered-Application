package com.amason.hospitalinventory.controller;

import com.amason.hospitalinventory.model.StockMovement;
import com.amason.hospitalinventory.model.MovementStatus;
import com.amason.hospitalinventory.repository.StockMovementRepository;
import com.amason.hospitalinventory.repository.UserRepository;
import com.amason.hospitalinventory.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stock-movements")
public class StockMovementController {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public StockMovement recordMovement(@RequestBody StockMovement movement) {
        return stockService.recordMovement(movement);
    }

    @GetMapping
    public List<StockMovement> getAllMovements() {
        return stockMovementRepository.findAll();
    }

    // GET /api/stock-movements/pending - returns only movements 
    // waiting for approval, so the Approvals page doesn't need to 
    // filter the full history itself
    @GetMapping("/pending")
    public List<StockMovement> getPendingMovements() {
        return stockMovementRepository.findByStatus(MovementStatus.PENDING);
    }

    // POST /api/stock-movements/{id}/approve - identifies the real 
    // approver from their own token (same Authentication pattern as 
    // /api/users/me), so the frontend never has to send its own id
    @PostMapping("/{id}/approve")
    public StockMovement approve(@PathVariable Long id, Authentication authentication) {
        Long approverId = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"))
            .getId();
        return stockService.approveMovement(id, approverId);
    }

    @PostMapping("/{id}/reject")
    public StockMovement reject(@PathVariable Long id, Authentication authentication) {
        Long approverId = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"))
            .getId();
        return stockService.rejectMovement(id, approverId);
    }
}
