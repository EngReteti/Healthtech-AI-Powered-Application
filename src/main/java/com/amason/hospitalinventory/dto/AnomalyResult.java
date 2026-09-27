package com.amason.hospitalinventory.dto;

import com.amason.hospitalinventory.model.StockMovement;

// A simple wrapper: the actual movement that looked unusual, plus a 
// plain-English explanation of WHY - this is what makes it 
// "explainable," not just a mysterious flag
import java.io.Serializable;

public class AnomalyResult implements Serializable {

    private StockMovement movement;
    private String reason;

    public AnomalyResult(StockMovement movement, String reason) {
        this.movement = movement;
        this.reason = reason;
    }

    public StockMovement getMovement() { return movement; }
    public void setMovement(StockMovement movement) { this.movement = movement; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
