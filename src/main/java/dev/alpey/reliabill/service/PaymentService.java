package dev.alpey.reliabill.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    public PaymentDto createPayment(PaymentDto paymentDto) {
        Payment payment = modelMapper.map(paymentDto, Payment.class);
        Invoice invoice = invoiceRepository.findById(paymentDto.getInvoiceId())
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found!"));
        InvoiceStatus invoiceStatus = checkInvoiceStatus(invoice, payment);
        invoice.setInvoiceStatus(invoiceStatus);
        payment.setInvoice(invoice);
        payment.setPaymentDate(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);
        return convertPaymentToDto(savedPayment);
    }

    private InvoiceStatus checkInvoiceStatus(Invoice invoice, Payment payment) {
        if (payment.getAmount() >= invoice.getTotal()) {
            return InvoiceStatus.PAID;
        } else {
            return InvoiceStatus.PARTIALLY_PAID;
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

    public void deletePayment(Long paymentId) {
        if (paymentRepository.existsById(paymentId)) {
            paymentRepository.deleteById(paymentId);
        } else {
            throw new NoSuchElementException("Payment not found!");
        }
    }

    private PaymentDto convertPaymentToDto(Payment payment) {
        return modelMapper.map(payment, PaymentDto.class);
    }
}
