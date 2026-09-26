package com.amason.hospitalinventory.service;

import com.amason.hospitalinventory.model.MovementStatus;
import com.amason.hospitalinventory.model.MovementType;
import com.amason.hospitalinventory.model.StockMovement;
import com.amason.hospitalinventory.repository.StockMovementRepository;
import com.amason.hospitalinventory.model.Product;
import com.amason.hospitalinventory.repository.ProductRepository;
import com.amason.hospitalinventory.model.User;
import com.amason.hospitalinventory.repository.UserRepository;
import com.amason.hospitalinventory.model.StockBatch;
import com.amason.hospitalinventory.repository.StockBatchRepository;
import com.amason.hospitalinventory.dto.ReconciliationResult;
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

    @Autowired
    private StockBatchRepository stockBatchRepository;

    public StockMovement recordMovement(StockMovement movement) {

        Product product = productRepository.findById(movement.getProduct().getId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        movement.setProduct(product);

        User performedBy = userRepository.findById(movement.getPerformedBy().getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        movement.setPerformedBy(performedBy);

        if (movement.getBatch() != null && movement.getBatch().getId() != null) {
            StockBatch batch = stockBatchRepository.findById(movement.getBatch().getId())
                .orElseThrow(() -> new RuntimeException("Batch not found"));
            movement.setBatch(batch);
        }

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

    /**
     * Compares a real physical count against the system's calculated 
     * stock. If they match, nothing happens - the system was already 
     * correct. If they don't, a PENDING ADJUSTMENT is automatically 
     * created, ready for an Auditor to review on the Approvals page - 
     * this is the actual closing of the loop between "what we think 
     * we have" and "what's really on the shelf."
     */
    public ReconciliationResult reconcile(Long productId, int countedQuantity, Long performedById) {
        int calculatedStock = calculateCurrentStock(productId);
        int variance = countedQuantity - calculatedStock;

        ReconciliationResult result = new ReconciliationResult();
        result.setCalculatedStock(calculatedStock);
        result.setCountedQuantity(countedQuantity);
        result.setVariance(variance);

        if (variance == 0) {
            result.setDiscrepancyFound(false);
            return result;
        }

        result.setDiscrepancyFound(true);

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        User performedBy = userRepository.findById(performedById)
            .orElseThrow(() -> new RuntimeException("User not found"));

        StockMovement adjustment = new StockMovement();
        adjustment.setProduct(product);
        adjustment.setType(MovementType.ADJUSTMENT);
        // We store the ABSOLUTE variance amount as quantity, and the 
        // reason states the direction clearly - keeps quantity always 
        // positive, matching every other movement in the system
        adjustment.setQuantity(Math.abs(variance));
        adjustment.setReason(String.format(
            "Physical count found %d units, system expected %d (variance: %+d)",
            countedQuantity, calculatedStock, variance
        ));
        adjustment.setPerformedBy(performedBy);
        adjustment.setStatus(MovementStatus.PENDING);

        StockMovement saved = stockMovementRepository.save(adjustment);
        result.setPendingAdjustment(saved);

        return result;
    }
}
