package com.amason.hospitalinventory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
public class StockMovement implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which product this movement affects - required, every movement 
    // must be tied to a real product
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Which specific batch this movement affects - OPTIONAL, because 
    // EQUIPMENT (like a reusable wheelchair) doesn't need batch tracking, 
    // only MEDICATION/CONSUMABLE do
    @ManyToOne
    @JoinColumn(name = "batch_id", nullable = true)
    private StockBatch batch;

    // Which department this stock is going TO - optional, since an "IN" 
    // movement (arriving from a supplier) might sit in general storage 
    // before being assigned to a department
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = true)
    private Department department;

    // The type of movement - IN, DISPENSED, TRANSFER, DAMAGE, EXPIRED, ADJUSTMENT
    // @Enumerated(STRING) stores it as readable text in the database,
    // same reasoning as we used for Role
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

    // Always a positive number - we never store negative quantities.
    // Whether it ADDS or SUBTRACTS from stock is decided later by 
    // looking at the "type" field, not by the sign of this number
    @Column(nullable = false)
    private Integer quantity;

    // A human-readable explanation, e.g. "Delivery from supplier", 
    // "Dispensed to patient in ICU", "Broken during transport"
    @Column(nullable = false)
    private String reason;

    // Whether this movement counts toward stock yet - DIRECT, PENDING, 
    // APPROVED, or REJECTED. Same reasoning as MovementType above
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementStatus status;

    // WHO logged this movement - required. This is what makes every 
    // movement traceable to a real person, closing the "who did this?" gap
    @ManyToOne
    @JoinColumn(name = "performed_by", nullable = false)
    private User performedBy;

    // WHO approved it - only filled in for ADJUSTMENT movements, or for 
    // ANY movement involving a controlled substance (two-person rule). 
    // Optional because most movements (a normal IN or DISPENSED on a 
    // regular product) don't need a second approver
    @ManyToOne
    @JoinColumn(name = "approved_by", nullable = true)
    private User approvedBy;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // --- Getters and setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public StockBatch getBatch() { return batch; }
    public void setBatch(StockBatch batch) { this.batch = batch; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public MovementType getType() { return type; }
    public void setType(MovementType type) { this.type = type; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public MovementStatus getStatus() { return status; }
    public void setStatus(MovementStatus status) { this.status = status; }

    public User getPerformedBy() { return performedBy; }
    public void setPerformedBy(User performedBy) { this.performedBy = performedBy; }

    public User getApprovedBy() { return approvedBy; }
    public void setApprovedBy(User approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
