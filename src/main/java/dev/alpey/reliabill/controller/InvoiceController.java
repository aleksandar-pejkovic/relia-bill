package dev.alpey.reliabill.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.alpey.reliabill.model.dto.InvoiceDto;
import dev.alpey.reliabill.service.InvoiceService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/search/{searchTerm}")
    public List<InvoiceDto> searchInvoices(@PathVariable String searchTerm, Principal principal) {
        return invoiceService.searchInvoices(searchTerm, principal);
    }

    @PostMapping
    public ResponseEntity<InvoiceDto> createInvoice(@Valid @RequestBody InvoiceDto invoiceDto) {
        InvoiceDto createdInvoice = invoiceService.createInvoice(invoiceDto);
        return new ResponseEntity<>(createdInvoice, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<InvoiceDto> updateInvoice(@Valid @RequestBody InvoiceDto invoiceDto) {
        InvoiceDto updatedInvoice = invoiceService.updateInvoice(invoiceDto);
        return new ResponseEntity<>(updatedInvoice, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public String deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return "Invoice delete!";
    }

    @GetMapping
    public List<InvoiceDto> fetchAllInvoiceForLoggedUser(Principal principal) {
        return invoiceService.loadAllInvoiceForLoggedUser(principal);
    }

    @GetMapping("/company/{companyId}")
    public List<InvoiceDto> fetchAllInvoiceForCompany(@PathVariable Long companyId) {
        return invoiceService.loadAllInvoiceForCompany(companyId);
    }
}
