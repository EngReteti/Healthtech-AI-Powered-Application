package com.amason.hospitalinventory.service;

import com.amason.hospitalinventory.dto.AnomalyResult;
import com.amason.hospitalinventory.model.MovementType;
import com.amason.hospitalinventory.model.StockMovement;
import com.amason.hospitalinventory.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnomalyDetectionService {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    private static final java.util.Set<MovementType> REDUCTION_TYPES = java.util.Set.of(
        MovementType.DISPENSED, MovementType.TRANSFER, MovementType.DAMAGE, MovementType.EXPIRED
    );

    /**
     * Scans every movement and returns the ones that look statistically 
     * unusual, each with a plain-English reason attached. This is 
     * explainable, rule-based detection - not a black-box model - so 
     * every flag can be understood and questioned by a real person.
     */
    public List<AnomalyResult> detectAnomalies() {
        List<StockMovement> allMovements = stockMovementRepository.findAll();
        List<AnomalyResult> results = new ArrayList<>();

        for (StockMovement movement : allMovements) {

            // RULE 1: unusually large quantity compared to this 
            // product's OTHER movements of the same type
            Double averageQuantity = calculateAverageQuantity(
                allMovements, movement.getProduct().getId(), movement.getType(), movement.getId()
            );

            if (averageQuantity != null && averageQuantity > 0
                    && movement.getQuantity() > averageQuantity * 3) {
                results.add(new AnomalyResult(movement,
                    String.format(
                        "Quantity (%d) is more than 3x this product's usual %s amount (avg %.1f)",
                        movement.getQuantity(), movement.getType(), averageQuantity
                    )
                ));
                // Don't double-flag the same movement under Rule 2 too - 
                // one clear reason is more useful than a confusing stack
                continue;
            }

            // RULE 2: a single reduction that removed more than half 
            // the stock that existed right before it happened
            if (REDUCTION_TYPES.contains(movement.getType())) {
                int stockBeforeThisMovement = calculateStockBeforeMovement(
                    allMovements, movement
                );

                if (stockBeforeThisMovement > 0
                        && movement.getQuantity() > stockBeforeThisMovement * 0.5) {
                    results.add(new AnomalyResult(movement,
                        String.format(
                            "Removed %d units - more than half of the %d units available just before this movement",
                            movement.getQuantity(), stockBeforeThisMovement
                        )
                    ));
                }
            }
        }

        return results;
    }

    // Average quantity of this product's other movements of the SAME 
    // type, EXCLUDING the movement we're currently checking (so a 
    // movement never gets compared against itself)
    private Double calculateAverageQuantity(List<StockMovement> all, Long productId,
                                              MovementType type, Long excludeMovementId) {
        List<StockMovement> comparable = all.stream()
            .filter(m -> m.getProduct().getId().equals(productId))
            .filter(m -> m.getType() == type)
            .filter(m -> !m.getId().equals(excludeMovementId))
            .toList();

        if (comparable.isEmpty()) {
            return null;
        }

        double sum = comparable.stream().mapToInt(StockMovement::getQuantity).sum();
        return sum / comparable.size();
    }

    // Replays this product's history up to (but not including) the 
    // given movement, to find out how much stock existed right 
    // before it happened - reuses the exact same DIRECT/APPROVED 
    // logic as StockService's real stock calculation, for consistency
    private int calculateStockBeforeMovement(List<StockMovement> all, StockMovement target) {
        int stock = 0;

        List<StockMovement> earlierMovements = all.stream()
            .filter(m -> m.getProduct().getId().equals(target.getProduct().getId()))
            .filter(m -> m.getId() < target.getId())
            .toList();

        for (StockMovement m : earlierMovements) {
            boolean counts = m.getStatus().toString().equals("DIRECT")
                || m.getStatus().toString().equals("APPROVED");

            if (!counts) continue;

            if (m.getType() == MovementType.IN) {
                stock += m.getQuantity();
            } else if (REDUCTION_TYPES.contains(m.getType())) {
                stock -= m.getQuantity();
            } else if (m.getType() == MovementType.ADJUSTMENT) {
                stock += m.getQuantity();
            }
        }

        return stock;
    }
}
