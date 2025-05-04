package com.example.receipt.controller;

import com.example.receipt.dto.InvoiceRequest;
import com.example.receipt.model.InvoiceData;
import com.example.receipt.repository.InvoiceRepository;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.text.DecimalFormat;
import java.util.List;

@RestController
@RequestMapping("/api/invoice")
@CrossOrigin(origins = "http://localhost:4200")
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;

    @Value("${invoice.template-name:INVOICE_Formatted[1].docx}")
    private String templateName;

    @Value("${invoice.output-dir:D:/JMI_INVOICE}")
    private String outputDir;

    // Set your rate per can here
    private static final BigDecimal RATE_PER_CAN = new BigDecimal("12345678"); // Change as needed

    public InvoiceController(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveDailyEntry(@RequestBody InvoiceRequest request) {
        InvoiceData data = new InvoiceData();
        data.setDate(request.getDate());
        data.setCanWeight(request.getCanWeight());
        invoiceRepository.save(data);
        return ResponseEntity.ok("Saved");
    }

    @GetMapping("/generate")
    public ResponseEntity<Resource> generateInvoice() throws IOException {
        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.LocalDate start = now.withDayOfMonth(1);
        java.time.LocalDate end = now.withDayOfMonth(now.lengthOfMonth());
        List<InvoiceData> all = invoiceRepository.findByDateBetween(start, end);

        // Calculate totals
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (InvoiceData entry : all) {
            BigDecimal canWeight = parseBigDecimal(entry.getCanWeight());
            BigDecimal amount = canWeight.multiply(RATE_PER_CAN);
            totalAmount = totalAmount.add(amount);
        }

        BigDecimal sgst = totalAmount.multiply(new BigDecimal("0.025"));
        BigDecimal cgst = totalAmount.multiply(new BigDecimal("0.025"));
        BigDecimal grandTotal = totalAmount.add(sgst).add(cgst);
        BigDecimal roundedGrandTotal = new BigDecimal(Math.round(grandTotal.doubleValue()));
        BigDecimal roundOff = roundedGrandTotal.subtract(grandTotal);
        String amountWords = convertNumberToWords(roundedGrandTotal.longValue()) + " only";

        // Open template
        InputStream templateStream = getClass().getResourceAsStream("/templates/" + templateName);
        if (templateStream == null) throw new IOException("Template not found: " + templateName);
        XWPFDocument doc = new XWPFDocument(templateStream);

        // Replace placeholders
        for (XWPFParagraph para : doc.getParagraphs()) {
            for (XWPFRun run : para.getRuns()) {
                String text = run.getText(0);
                if (text == null) continue;
                // Replace day placeholders
                for (int i = 1; i <= 31; i++) {
                    String tag = "${day" + i + "}";
                    for (InvoiceData entry : all) {
                        if (entry.getDate().getDayOfMonth() == i && text.contains(tag)) {
                            text = text.replace(tag, entry.getCanWeight());
                        }
                    }
                }
                // Replace totals/taxes/words
                text = text.replace("${totalAmount}", formatAmount(totalAmount));
                text = text.replace("${sgst}", formatAmount(sgst));
                text = text.replace("${cgst}", formatAmount(cgst));
                text = text.replace("${roundOff}", formatAmount(roundOff));
                text = text.replace("${grandTotal}", formatAmount(roundedGrandTotal));
                text = text.replace("${amountWords}", amountWords);
                run.setText(text, 0);
            }
        }

        String fileName = "Invoice_" + now.getMonth().toString() + "_" + now.getYear() + ".docx";
        Path path = Paths.get(outputDir);
        Files.createDirectories(path);
        Path file = path.resolve(fileName);

        try (FileOutputStream out = new FileOutputStream(file.toFile())) {
            doc.write(out);
        }
        doc.close();

        Resource resource = new UrlResource(file.toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // Helper to parse canWeight safely
    private static BigDecimal parseBigDecimal(String value) {
        try {
            return new BigDecimal(value.trim());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    // Helper to format amounts to 2 decimal places
    private static String formatAmount(BigDecimal amount) {
        return new DecimalFormat("#,##0.00").format(amount);
    }

    // Simple number to words (for demo, not Indian format)
    private static String convertNumberToWords(long number) {
        if (number == 0) return "zero";
        return NumberToWordsConverter.convert(number);
    }
} 