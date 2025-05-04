package com.example.receipt.repository;

import com.example.receipt.model.ReceiptData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<ReceiptData, Long>, JpaSpecificationExecutor<ReceiptData> {
    Optional<ReceiptData> findTopByOrderByChallanNoDesc();
    Optional<ReceiptData> findByChallanNo(String challanNo);
} 