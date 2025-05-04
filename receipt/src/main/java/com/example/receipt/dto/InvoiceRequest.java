package com.example.receipt.dto;

import java.time.LocalDate;

public class InvoiceRequest {
    private LocalDate date;
    private String canWeight;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getCanWeight() { return canWeight; }
    public void setCanWeight(String canWeight) { this.canWeight = canWeight; }
} 