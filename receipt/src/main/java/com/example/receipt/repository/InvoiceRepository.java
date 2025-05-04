package com.example.receipt.repository;

import com.example.receipt.model.InvoiceData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<InvoiceData, Long> {
    List<InvoiceData> findByDateBetween(LocalDate start, LocalDate end);
} 