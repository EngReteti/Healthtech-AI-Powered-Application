package com.amason.hospitalinventory.dto;

public class ReconciliationRequest {
    private Long productId;
    private int countedQuantity;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public int getCountedQuantity() { return countedQuantity; }
    public void setCountedQuantity(int countedQuantity) { this.countedQuantity = countedQuantity; }
}
