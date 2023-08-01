package dev.alpey.reliabill.service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import dev.alpey.reliabill.configuration.exceptions.invoice.InvoiceNotFoundException;
import dev.alpey.reliabill.enums.InvoiceStatus;
import dev.alpey.reliabill.model.dto.PaymentDto;
import dev.alpey.reliabill.model.entity.Company;
import dev.alpey.reliabill.model.entity.Invoice;
import dev.alpey.reliabill.model.entity.Payment;
import dev.alpey.reliabill.repository.CompanyRepository;
import dev.alpey.reliabill.repository.InvoiceRepository;
import dev.alpey.reliabill.repository.PaymentRepository;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CompanyRepository companyRepository;

    @CacheEvict(value = "invoicesByUser", key = "#principal.getName()")
    public PaymentDto createPayment(PaymentDto paymentDto, Principal principal) {
        Payment payment = modelMapper.map(paymentDto, Payment.class);
        Invoice invoice = invoiceRepository.findById(paymentDto.getInvoiceId())
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found!"));
        double totalPayments = calculatePaymentsAmountByInvoice(invoice.getId()) + payment.getAmount();
        InvoiceStatus invoiceStatus = obtainInvoiceStatus(totalPayments, invoice.getTotal());
        invoice.setInvoiceStatus(invoiceStatus);
        payment.setInvoice(invoice);
        payment.setPaymentDate(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);
        return convertPaymentToDto(savedPayment);
    }

    @CacheEvict(value = "invoicesByUser", key = "#principal.getName()")
    public void deletePayment(Long paymentId, Principal principal) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow();
        paymentRepository.delete(payment);
        Invoice invoice = invoiceRepository.findById(payment.getInvoice().getId())
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found!"));
        double totalPayments = calculatePaymentsAmountByInvoice(invoice.getId());
        InvoiceStatus invoiceStatus = obtainInvoiceStatus(totalPayments, invoice.getTotal());
        invoice.setInvoiceStatus(invoiceStatus);
        invoiceRepository.save(invoice);
    }

    private InvoiceStatus obtainInvoiceStatus(double totalPayments, double invoiceTotal) {
        if (totalPayments >= invoiceTotal) {
            return InvoiceStatus.PAID;
        } else if (totalPayments > 0.0) {
            return InvoiceStatus.PARTIALLY_PAID;
        } else {
            return InvoiceStatus.PENDING;
        }
    }

    public PaymentDto loadPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow();
        return convertPaymentToDto(payment);
    }

    public List<PaymentDto> loadPaymentByInvoiceId(Long invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId).stream()
                .map(this::convertPaymentToDto)
                .collect(Collectors.toList());
    }

    public List<PaymentDto> loadPaymentsByCompanyId(Long companyId) {
        List<Payment> payments = new ArrayList<>();
        companyRepository.findById(companyId)
                .ifPresent(company -> {
                    company.getInvoices()
                            .forEach(invoice -> {
                                payments.addAll(invoice.getPayments());
                            });
                });
        return payments.stream()
                .map(this::convertPaymentToDto)
                .toList();
    }

    public Double calculatePaymentsAmountByCompany(Company company) {
        List<Payment> payments = new ArrayList<>();
        company.getInvoices()
                .forEach(invoice -> {
                    payments.addAll(invoice.getPayments());
                });
        return payments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    private double calculatePaymentsAmountByInvoice(Long invoiceId) {
        List<Payment> payments = paymentRepository.findByInvoiceId(invoiceId);
        return payments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    private PaymentDto convertPaymentToDto(Payment payment) {
        return modelMapper.map(payment, PaymentDto.class);
    }
}
