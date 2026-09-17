package com.amason.hospitalinventory.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // SKU = "Stock Keeping Unit" - a unique code identifying this exact 
    // product, e.g. "MED-PARA500-001". Must be unique - no two products 
    // can share one
    @Column(nullable = false, unique = true)
    private String sku;

    // Stored as text matching our schema: MEDICATION / CONSUMABLE / EQUIPMENT
    // We use a plain String here (not an enum like Role) because product 
    // categories may grow over time and don't need strict database-level locking
    @Column(nullable = false)
    private String category;

    // BigDecimal is used for money instead of double/float - this avoids 
    // tiny rounding errors that regular decimal numbers can cause, which 
    // matters a lot once you're tracking real currency
    @Column(nullable = false)
    private BigDecimal unitPrice;

    // Defaults to "KES" (Kenyan Shillings) for every new product, since 
    // this system is built for a Kenyan hospital context - stored 
    // explicitly rather than assumed, so pricing is never ambiguous
    @Column(nullable = false)
    private String currency = "KES";

    // When stock falls to or below this number, the system should flag 
    // it as "needs reordering" (we'll build that alert logic later)
    @Column(nullable = false)
    private Integer reorderLevel;

    // Defaults to false for every normal product - only flipped to true 
    // for medication requiring stricter two-person tracking
    @Column(nullable = false)
    private Boolean isControlledSubstance = false;

    // --- THE RELATIONSHIP ---
    // @ManyToOne means: many Products can point to the same one Supplier.
    // @JoinColumn tells Hibernate exactly which column in the "products" 
    // table stores that connection - "supplier_id", matching our schema
    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // --- Getters and setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }

    public Boolean getIsControlledSubstance() { return isControlledSubstance; }
    public void setIsControlledSubstance(Boolean isControlledSubstance) { this.isControlledSubstance = isControlledSubstance; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
