package com.amason.hospitalinventory.model;

import jakarta.persistence.*;

// Same idea as User: @Entity turns this class into a real database table,
// and @Table names it "suppliers" to match our schema design
@Entity
@Table(name = "suppliers")
public class Supplier implements java.io.Serializable {

    // Same pattern as before - auto-generated unique ID for each supplier
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Every supplier needs a name - this is required
    @Column(nullable = false)
    private String name;

    // Phone and email are optional (no nullable = false) because a supplier
    // might only have one or the other, or we might not have it on file yet
    private String phone;

    private String email;

    private String address;

    // --- Getters and setters ---
    // Same reason as in User: Hibernate and Spring need these to read/write
    // each field when saving or loading a Supplier from the database

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
