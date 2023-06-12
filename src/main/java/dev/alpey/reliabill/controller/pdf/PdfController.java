package dev.alpey.reliabill.controller.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;

import dev.alpey.reliabill.repository.InvoiceRepository;
import dev.alpey.reliabill.service.pdf.PdfService;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @GetMapping("/invoice/{id}")
    public ResponseEntity<InputStreamResource> getInvoicePdf(@PathVariable Long id, Principal principal)
            throws IOException, DocumentException {
        // Create input stream from PDF document
        InputStream inputStream = pdfService.generateInvoicePdf(principal.getName(), id);
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        String invoiceNumber = invoiceRepository.findInvoiceNumberById(id);
        // Return input stream as a response
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + invoiceNumber + ".pdf");
        return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
    }
}
