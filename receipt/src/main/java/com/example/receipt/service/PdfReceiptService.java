package com.example.receipt.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.example.receipt.model.ReceiptData;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PdfReceiptService {
    
    private static final String TEMPLATE_PATH = "templates/Jamanamaiya_Ice_Receipt.pdf";
    private static final String VENDOR_CODE = "0003818642";
    private static final String HSN_CODE = "22019010";
    private static final String GSTIN = "GSTIN - 24AABFJ5334M1ZC";
    private static final String M_NO = "M.No: 9227499500/8000102030";
    private static final String COMPANY_ADDRESS = "244, G.I.D.C., Porbandar - 360 577";
    private static final String COMPANY_NAME = "Reliance Industries Ltd. RIL complex (polysilicon factory) vill: Meghpar, P.O Motikhavdi, Jamnagar";
    
    public byte[] generateReceipt(String challanNo, ReceiptData receiptData) throws IOException {
        System.out.println("Starting PDF generation...");
        
        // Load the template
        ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH);
        PDDocument document = PDDocument.load(resource.getInputStream());
        PDPage page = document.getPage(0);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
            // Set default font and size
            contentStream.setFont(PDType1Font.TIMES_BOLD, 12);

            // Add GSTIN (top left)
            addText(contentStream, GSTIN, 60, 750);

            // Add M.No (top right)
            addText(contentStream, M_NO, 450, 750);

            // Add Company Name (centered)
            addText(contentStream, "Jamanamaiya Ice", 250, 700);

            // Add Address (centered)
            addText(contentStream, COMPANY_ADDRESS, 200, 680);

            // Add Challan No
            addText(contentStream, "Chalan No. " + challanNo, 60, 620);

            // Add Date (right aligned)
            String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            addText(contentStream, "Date: " + formattedDate, 450, 620);

            // Add Name
            addText(contentStream, "Name: " + COMPANY_NAME, 60, 580);

            // Add Vendor Code and HSN Code
            addText(contentStream, "Vendor Code: " + VENDOR_CODE, 60, 540);
            addText(contentStream, "HSN Code: " + HSN_CODE, 450, 540);

            // Add Vehicle No and Ice Slabs
            addText(contentStream, "Vehicle No.: " + receiptData.getVehicleNo(), 60, 460);
            addText(contentStream, "Ice Slabs: " + receiptData.getIceSlabs(), 450, 460);

            // Add Order No
            if (receiptData.getOrderNo() != null && !receiptData.getOrderNo().isEmpty()) {
                addText(contentStream, "Order No. (P.O.): " + receiptData.getOrderNo(), 60, 420);
            }

            // Add stability note in italic
            contentStream.setFont(PDType1Font.TIMES_ITALIC, 12);
            addText(contentStream, "The above goods is found in good stability.", 60, 380);

            // Add From text right-aligned
            contentStream.setFont(PDType1Font.TIMES_BOLD, 12);
            String fromText = "From : Jamanamaiya Ice";
            float pageWidth = page.getMediaBox().getWidth();
            float textWidth = PDType1Font.TIMES_BOLD.getStringWidth(fromText) / 1000 * 12;
            addText(contentStream, fromText, pageWidth - textWidth - 60, 380);

            // Add Receiver's Signature
            addText(contentStream, "Receiver's Signature: _____________", 60, 300);
        }

        // Save to byte array
        try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
            document.save(baos);
            document.close();
            return baos.toByteArray();
        }
    }

    private void addText(PDPageContentStream contentStream, String text, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }
} 