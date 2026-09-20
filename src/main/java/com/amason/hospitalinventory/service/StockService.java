package com.amason.hospitalinventory.service;

import com.amason.hospitalinventory.model.MovementStatus;
import com.amason.hospitalinventory.model.MovementType;
import com.amason.hospitalinventory.model.StockMovement;
import com.amason.hospitalinventory.repository.StockMovementRepository;
import com.amason.hospitalinventory.model.Product;
import com.amason.hospitalinventory.repository.ProductRepository;
import com.amason.hospitalinventory.model.User;
import com.amason.hospitalinventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StockService {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public StockMovement recordMovement(StockMovement movement) {

        Product product = productRepository.findById(movement.getProduct().getId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        movement.setProduct(product);

        User performedBy = userRepository.findById(movement.getPerformedBy().getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        movement.setPerformedBy(performedBy);

        boolean isControlled = product.getIsControlledSubstance();

        MovementStatus status = determineInitialStatus(movement.getType(), isControlled);
        movement.setStatus(status);

        boolean isReduction =
            movement.getType() == MovementType.DISPENSED ||
            movement.getType() == MovementType.TRANSFER ||
            movement.getType() == MovementType.DAMAGE ||
            movement.getType() == MovementType.EXPIRED;

        if (isReduction && status == MovementStatus.DIRECT) {
            int currentStock = calculateCurrentStock(product.getId());

            if (currentStock < movement.getQuantity()) {
                throw new IllegalStateException(
                    "Cannot record movement: only " + currentStock +
                    " units available, but " + movement.getQuantity() + " requested."
                );
            }
        }

        return stockMovementRepository.save(movement);
    }

    public int calculateCurrentStock(Long productId) {
        List<StockMovement> movements = stockMovementRepository.findByProductId(productId);
        int stock = 0;

        for (StockMovement movement : movements) {
            boolean countsTowardStock =
                movement.getStatus() == MovementStatus.DIRECT ||
                movement.getStatus() == MovementStatus.APPROVED;

            if (!countsTowardStock) {
                continue;
            }

            switch (movement.getType()) {
                case IN:
                    stock += movement.getQuantity();
                    break;
                case DISPENSED:
                case TRANSFER:
                case DAMAGE:
                case EXPIRED:
                    stock -= movement.getQuantity();
                    break;
                case ADJUSTMENT:
                    stock += movement.getQuantity();
                    break;
            }
        }

        return stock;
    }

    public MovementStatus determineInitialStatus(MovementType type, boolean isControlledSubstance) {
        boolean requiresApproval =
            type == MovementType.ADJUSTMENT || isControlledSubstance;

        if (requiresApproval) {
            return MovementStatus.PENDING;
        }

        return MovementStatus.DIRECT;
    }

    // NEW: Approves a pending movement - only meant to be called after 
    // confirming the caller is an AUDITOR (that check happens in the 
    // controller/security layer, not here)
    public StockMovement approveMovement(Long movementId, Long approverId) {
        StockMovement movement = stockMovementRepository.findById(movementId)
            .orElseThrow(() -> new RuntimeException("Movement not found"));

        if (movement.getStatus() != MovementStatus.PENDING) {
            throw new IllegalStateException("Only PENDING movements can be approved.");
        }

        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new RuntimeException("Approver not found"));

        movement.setStatus(MovementStatus.APPROVED);
        movement.setApprovedBy(approver);

        return stockMovementRepository.save(movement);
    }

    // NEW: Rejects a pending movement - it will never count toward 
    // stock, but stays in the ledger permanently as a record that 
    // someone tried and was correctly refused
    public StockMovement rejectMovement(Long movementId, Long approverId) {
        StockMovement movement = stockMovementRepository.findById(movementId)
            .orElseThrow(() -> new RuntimeException("Movement not found"));

        if (movement.getStatus() != MovementStatus.PENDING) {
            throw new IllegalStateException("Only PENDING movements can be rejected.");
        }

        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new RuntimeException("Approver not found"));

        movement.setStatus(MovementStatus.REJECTED);
        movement.setApprovedBy(approver);

        return stockMovementRepository.save(movement);
    }
}
