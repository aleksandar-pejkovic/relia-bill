package dev.alpey.reliabill.controller.pdf;

import java.io.InputStream;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lowagie.text.DocumentException;

import dev.alpey.reliabill.model.entity.Invoice;
import dev.alpey.reliabill.repository.InvoiceRepository;
import dev.alpey.reliabill.service.email.EmailService;
import dev.alpey.reliabill.service.pdf.PdfService;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @GetMapping("/invoice/{id}")
    public ResponseEntity<InputStreamResource> createInvoicePdf(@PathVariable Long id, Principal principal)
            throws Exception {

        InputStream inputStream = pdfService.generateInvoicePdf(principal.getName(), id);
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        String invoiceNumber = invoiceRepository.findInvoiceNumberById(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(invoiceNumber + ".pdf").build());
        return ResponseEntity.ok()
                .headers(headers)
                .body(inputStreamResource);
    }

    @GetMapping("/invoice/{id}/send")
    public ResponseEntity<String> sendInvoiceToClient(@PathVariable Long id, Principal principal)
            throws DocumentException, Exception {

        InputStream inputStream = pdfService.generateInvoicePdf(principal.getName(), id);

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow();

        emailService.sendInvoiceEmailToClient(invoice, inputStream, principal);

        return ResponseEntity.ok()
                .body("Invoice sent to client.");
    }

    @GetMapping("/companies-report")
    public ResponseEntity<InputStreamResource> createCompaniesReportPdf(
            @RequestParam String sortBy,
            Principal principal) throws Exception {

        InputStream inputStream = pdfService.generateCompaniesReport(principal.getName(), sortBy);
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("companies-report.pdf").build());
        return ResponseEntity.ok()
                .headers(headers)
                .body(inputStreamResource);
    }

    @GetMapping("/products-report")
    public ResponseEntity<InputStreamResource> createProductsReportPdf(
            @RequestParam String sortBy,
            Principal principal) throws Exception {

        InputStream inputStream = pdfService.generateProductsReport(principal.getName(), sortBy);
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("products-report.pdf").build());
        return ResponseEntity.ok()
                .headers(headers)
                .body(inputStreamResource);
    }
}
