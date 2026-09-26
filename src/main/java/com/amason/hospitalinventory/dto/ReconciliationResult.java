package com.amason.hospitalinventory.dto;

import com.amason.hospitalinventory.model.StockMovement;

// The full, honest picture of a reconciliation check - shows what the 
// system THOUGHT stock was, what was actually counted, and whether a 
// correction was needed - never hides the math from the person doing it
public class ReconciliationResult {
    private int calculatedStock;
    private int countedQuantity;
    private int variance;
    private boolean discrepancyFound;
    private StockMovement pendingAdjustment;

    public int getCalculatedStock() { return calculatedStock; }
    public void setCalculatedStock(int calculatedStock) { this.calculatedStock = calculatedStock; }

    public int getCountedQuantity() { return countedQuantity; }
    public void setCountedQuantity(int countedQuantity) { this.countedQuantity = countedQuantity; }

    public int getVariance() { return variance; }
    public void setVariance(int variance) { this.variance = variance; }

    public boolean isDiscrepancyFound() { return discrepancyFound; }
    public void setDiscrepancyFound(boolean discrepancyFound) { this.discrepancyFound = discrepancyFound; }

    public StockMovement getPendingAdjustment() { return pendingAdjustment; }
    public void setPendingAdjustment(StockMovement pendingAdjustment) { this.pendingAdjustment = pendingAdjustment; }
}
