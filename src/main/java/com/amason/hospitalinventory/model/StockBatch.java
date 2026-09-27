package com.amason.hospitalinventory.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "stock_batches")
public class StockBatch implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Each batch belongs to exactly one Product - same relationship 
    // pattern we just used between Product and Supplier
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // A code identifying this specific delivery, e.g. "BATCH-2026-0817"
    // Usually printed on the manufacturer's packaging
    @Column(nullable = false)
    private String batchNumber;

    // LocalDate is used (not LocalDateTime) because expiry is just a 
    // calendar day, not a specific time - "expires on 2026-12-01", 
    // not "expires at 2026-12-01 3:45pm"
    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private LocalDate receivedDate;

    // --- Getters and setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public LocalDate getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }
}
