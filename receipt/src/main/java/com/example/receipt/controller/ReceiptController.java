package com.example.receipt.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import com.example.receipt.model.ReceiptData;
import com.example.receipt.dto.ReceiptRequest;
import com.example.receipt.dto.ReceiptResponse;
import com.example.receipt.repository.ReceiptRepository;
import com.example.receipt.util.ChallanNumberManager;

@RestController
@RequestMapping("/api/receipts")
@CrossOrigin(origins = "http://localhost:4200")
public class ReceiptController {

    private final ReceiptRepository receiptRepository;
    private final ChallanNumberManager challanNumberManager;
    private static final String OUTPUT_DIR = "D:\\JMI_TEST";
    private static final String TEMPLATE_NAME = "Jamanamaiya_Ice_Receipt.docx";
    private static final String VENDOR_CODE = "0003818642";
    private static final String HSN_CODE = "22019010";
    private static final String GSTIN = "GSTIN - 24AABFJ5334M1ZC";
    private static final String M_NO = "M.No: 9227499500/8000102030";
    private static final String COMPANY_ADDRESS = "244, G.I.D.C, Porbandar - 360 575";
    private static final String FROM_COMPANY_NAME = "Reliance Industries Ltd. RIL complex (polysilicon factory) vill: Meghpar, P.O Motikhavdi, Jamnagar";
    private static final String COMPANY_TITLE = "Jamanamaiya Ice";

    public ReceiptController(ReceiptRepository receiptRepository, ChallanNumberManager challanNumberManager) {
        this.receiptRepository = receiptRepository;
        this.challanNumberManager = challanNumberManager;
    }

    @GetMapping
    public ResponseEntity<?> getReceipts(
            @RequestParam(required = false) String challanNumber,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        
        try {
            Specification<ReceiptData> spec = Specification.where(null);
            
            if (challanNumber != null && !challanNumber.trim().isEmpty()) {
                spec = spec.and((root, query, cb) -> 
                    cb.like(root.get("challanNo"), "%" + challanNumber + "%"));
            }
            
            if (startDate != null) {
                spec = spec.and((root, query, cb) -> 
                    cb.greaterThanOrEqualTo(root.get("date"), startDate));
            }
            
            if (endDate != null) {
                spec = spec.and((root, query, cb) -> 
                    cb.lessThanOrEqualTo(root.get("date"), endDate));
            }
            
            Pageable pageable = 
                PageRequest.of(page, size, 
                    Sort.by("createdAt").descending());
            
            Page<ReceiptData> pageResult = 
                receiptRepository.findAll(spec, pageable);
            
            return ResponseEntity.ok()
                .body(new HashMap<String, Object>() {{
                    put("content", pageResult.getContent());
                    put("totalElements", pageResult.getTotalElements());
                    put("totalPages", pageResult.getTotalPages());
                    put("currentPage", pageResult.getNumber());
                }});
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error retrieving receipts: " + e.getMessage());
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<Resource> generateReceipt(@RequestBody ReceiptRequest request) throws IOException {
        try {
            System.out.println("Starting receipt generation...");
            System.out.println("Request data: " + request.toString());

            // Create receipt data
            ReceiptData receiptData = new ReceiptData();
            receiptData.setName(FROM_COMPANY_NAME);
            receiptData.setVehicleNo(request.getVehicleNo());
            receiptData.setIceSlabs(request.getIceSlabs());
            receiptData.setOrderNo(request.getOrderNo());
            receiptData.setAsnCode(request.getAsnCode());    
            receiptData.setScnCode(request.getScnCode());    

            // Generate challan number
            String challanNo = challanNumberManager.getNextChallanNumber();
            System.out.println("Generated challan number: " + challanNo);
            receiptData.setChallanNo(challanNo);

            // Create output directory if it doesn't exist
            Path outputPath = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
                System.out.println("Created output directory: " + outputPath);
            }

            // Read the template from classpath
            System.out.println("Attempting to read template file: /templates/" + TEMPLATE_NAME);
            InputStream templateStream = getClass().getResourceAsStream("/templates/" + TEMPLATE_NAME);
            if (templateStream == null) {
                System.err.println("Template file not found in classpath!");
                throw new IOException("Template file not found in classpath: " + TEMPLATE_NAME);
            }
            System.out.println("Successfully loaded template file");

            // Create the document
            XWPFDocument doc = new XWPFDocument(templateStream);
            templateStream.close();

            // Get current date
            LocalDate currentDate = LocalDate.now();
            String formattedDate = currentDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // Prepare replacements for placeholders
            Map<String, String> replacements = new HashMap<>();
            replacements.put("${challanNo}", challanNo);
            replacements.put("${date}", formattedDate);
            replacements.put("${vendorCode}", VENDOR_CODE);
            replacements.put("${hsnCode}", HSN_CODE);
            replacements.put("${vehicleNo}", receiptData.getVehicleNo());
            replacements.put("${iceSlab}", String.valueOf(receiptData.getIceSlabs()));
            replacements.put("${orderNo}", request.getOrderNo() != null ? request.getOrderNo() : "");
            replacements.put("${companyName}", FROM_COMPANY_NAME);
            replacements.put("${gstin}", GSTIN);
            replacements.put("${mno}", M_NO);
            replacements.put("${asnCode}", request.getAsnCode());
            replacements.put("${scnCode}", request.getScnCode());
            replacements.put("${companyTitle}", COMPANY_TITLE);
            replacements.put("${companyAddress}", COMPANY_ADDRESS);


            // Replace placeholders in all paragraphs
            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                replaceInParagraph(paragraph, replacements);
            }
            // Replace placeholders in all tables
            for (org.apache.poi.xwpf.usermodel.XWPFTable table : doc.getTables()) {
                for (org.apache.poi.xwpf.usermodel.XWPFTableRow row : table.getRows()) {
                    for (org.apache.poi.xwpf.usermodel.XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            replaceInParagraph(paragraph, replacements);
                        }
                    }
                }
            }

            // Save receipt data to database
            System.out.println("Saving receipt data to database...");
            receiptData.setCreatedAt(LocalDateTime.now());
            receiptRepository.save(receiptData);

            // Save the document
            String dateForFileName = currentDate.format(java.time.format.DateTimeFormatter.ofPattern("dd_MM_yyyy"));
            String outputFileName = OUTPUT_DIR + File.separator + "Receipt_" + challanNo + "_" + dateForFileName + ".docx";
            System.out.println("Saving document to: " + outputFileName);
            FileOutputStream out = new FileOutputStream(outputFileName);
            doc.write(out);
            out.close();
            doc.close();

            // Return the generated file
            Path file = Paths.get(outputFileName);
            Resource resource = new UrlResource(file.toUri());
            System.out.println("Successfully generated receipt!");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            System.err.println("Error generating receipt: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate receipt: " + e.getMessage(), e);
        }
    }

    @GetMapping("/download/{challanNo}")
    public ResponseEntity<Resource> downloadReceipt(@PathVariable String challanNo) throws IOException {
        try {
            // Find the receipt by challan number
            ReceiptData receiptData = receiptRepository.findByChallanNo(challanNo)
                    .orElseThrow(() -> new RuntimeException("Receipt not found with challan number: " + challanNo));

            // Get current date
            LocalDate currentDate = LocalDate.now();
            String dateForFileName = currentDate.format(java.time.format.DateTimeFormatter.ofPattern("dd_MM_yyyy"));
            String outputFileName = OUTPUT_DIR + File.separator + "Receipt_" + challanNo + "_" + dateForFileName + ".docx";

            // Check if file already exists
            Path file = Paths.get(outputFileName);
            if (Files.exists(file)) {
                Resource resource = new UrlResource(file.toUri());
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            }

            // If file doesn't exist, generate it
            // Read the template
            InputStream templateStream = getClass().getResourceAsStream("/templates/" + TEMPLATE_NAME);
            if (templateStream == null) {
                throw new IOException("Template file not found in classpath: " + TEMPLATE_NAME);
            }

            // Create the document
            XWPFDocument doc = new XWPFDocument(templateStream);
            templateStream.close();

            // Format date for display
            String formattedDate = currentDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // Prepare replacements for placeholders
            Map<String, String> replacements = new HashMap<>();
            replacements.put("${challanNo}", challanNo);
            replacements.put("${date}", formattedDate);
            replacements.put("${vendorCode}", VENDOR_CODE);
            replacements.put("${hsnCode}", HSN_CODE);
            replacements.put("${vehicleNo}", receiptData.getVehicleNo());
            replacements.put("${iceSlab}", String.valueOf(receiptData.getIceSlabs()));
            replacements.put("${orderNo}", receiptData.getOrderNo() != null ? receiptData.getOrderNo() : "");
            replacements.put("${companyName}", FROM_COMPANY_NAME);
            replacements.put("${gstin}", GSTIN);
            replacements.put("${mno}", M_NO);
            replacements.put("${companyTitle}", COMPANY_TITLE);
            replacements.put("${companyAddress}", COMPANY_ADDRESS);


            // Replace placeholders in all paragraphs
            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                replaceInParagraph(paragraph, replacements);
            }
            // Replace placeholders in all tables
            for (org.apache.poi.xwpf.usermodel.XWPFTable table : doc.getTables()) {
                for (org.apache.poi.xwpf.usermodel.XWPFTableRow row : table.getRows()) {
                    for (org.apache.poi.xwpf.usermodel.XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            replaceInParagraph(paragraph, replacements);
                        }
                    }
                }
            }

            // Create output directory if it doesn't exist
            Path outputPath = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }

            // Save the document
            FileOutputStream out = new FileOutputStream(outputFileName);
            doc.write(out);
            out.close();
            doc.close();

            // Return the generated file
            Resource resource = new UrlResource(file.toUri());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            throw new RuntimeException("Failed to download receipt: " + e.getMessage(), e);
        }
    }

    @PostMapping("/print-latest")
    public ResponseEntity<String> printLatestReceipt() {
        File dir = new File("D:/JMI_TEST");
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".docx"));
        if (files == null || files.length == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No DOCX files found.");
        }
        File latestFile = Arrays.stream(files).max(Comparator.comparingLong(File::lastModified)).get();
        try {
            String command = String.format("cmd /c start /min winword /q /n /mFilePrintDefault /mFileExit \"%s\"", latestFile.getAbsolutePath());
            Runtime.getRuntime().exec(command);
            return ResponseEntity.ok("Print command sent for: " + latestFile.getName());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to print: " + e.getMessage());
        }
    }

    // Helper method for robust placeholder replacement
    private void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> replacements) {
        StringBuilder paragraphText = new StringBuilder();
        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.getText(0);
            if (text != null) {
                paragraphText.append(text);
            }
        }
        String replaced = paragraphText.toString();
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            replaced = replaced.replace(entry.getKey(), entry.getValue());
        }
        int numRuns = paragraph.getRuns().size();
        for (int i = numRuns - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }
        XWPFRun newRun = paragraph.createRun();
        newRun.setText(replaced, 0);
        // Set font size for companyTitle and companyAddress
        if (replaced.trim().equals(replacements.get("${companyTitle}"))) {
            newRun.setFontSize(28);
        } else if (replaced.trim().equals(replacements.get("${companyAddress}"))) {
            newRun.setFontSize(14);
        }
        // Set bold for all except company name line
        if (replaced.contains("Reliance Industries Ltd. RIL complex")) {
            newRun.setBold(false);
        } else {
            newRun.setBold(true);
        }
    }
}