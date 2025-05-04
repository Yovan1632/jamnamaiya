package com.example.receipt.util;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;
import com.example.receipt.repository.ReceiptRepository;
import jakarta.annotation.PostConstruct;

@Component
public class ChallanNumberManager {
    private static AtomicInteger counter;
    private final ReceiptRepository receiptRepository;

    public ChallanNumberManager(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    @PostConstruct
    public void initialize() {
        // Get the maximum challan number from the database
        String maxChallanNo = receiptRepository.findTopByOrderByChallanNoDesc()
                .map(receipt -> receipt.getChallanNo())
                .orElse("0000");
        
        // Convert the string number to integer
        int lastNumber = Integer.parseInt(maxChallanNo);
        counter = new AtomicInteger(lastNumber);
    }

    public String getNextChallanNumber() {
        int number = counter.incrementAndGet();
        return String.format("%04d", number);
    }
    
    public static void setCurrentNumber(int number) {
        counter.set(number);
    }
} 


