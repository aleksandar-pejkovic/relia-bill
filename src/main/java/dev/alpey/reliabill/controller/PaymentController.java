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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.alpey.reliabill.model.dto.PaymentDto;
import dev.alpey.reliabill.service.PaymentService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(@Valid @RequestBody PaymentDto paymentDto, Principal principal) {
        PaymentDto createdPayment = paymentService.createPayment(paymentDto, principal);
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public PaymentDto fetchPaymentsById(@PathVariable Long id) {
        return paymentService.loadPaymentById(id);
    }

    @GetMapping("/invoiceId/{invoiceId}")
    public List<PaymentDto> fetchPaymentsByInvoiceId(@PathVariable Long invoiceId) {
        return paymentService.loadPaymentByInvoiceId(invoiceId);
    }

    @GetMapping("/companyId/{companyId}")
    public List<PaymentDto> fetchPaymentsByCompanyId(@PathVariable Long companyId) {
        return paymentService.loadPaymentsByCompanyId(companyId);
    }

    @DeleteMapping("/{paymentId}")
    public String deletePayment(@PathVariable Long paymentId, Principal principal) {
        paymentService.deletePayment(paymentId, principal);
        return "Payment deleted!";
    }
}
