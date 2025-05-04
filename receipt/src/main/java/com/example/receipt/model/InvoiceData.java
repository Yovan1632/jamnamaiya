package com.example.receipt.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "invoice_data")
public class InvoiceData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String canWeight;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getCanWeight() { return canWeight; }
    public void setCanWeight(String canWeight) { this.canWeight = canWeight; }
} 