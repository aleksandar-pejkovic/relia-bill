package dev.alpey.reliabill.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.alpey.reliabill.configuration.exceptions.invoice.InvoiceNotFoundException;
import dev.alpey.reliabill.model.dto.PaymentDto;
import dev.alpey.reliabill.model.entity.Invoice;
import dev.alpey.reliabill.model.entity.Payment;
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

    public PaymentDto createPayment(PaymentDto paymentDto) {
        Payment payment = modelMapper.map(paymentDto, Payment.class);
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(paymentDto.getInvoiceId());
        if (optionalInvoice.isEmpty()) {
            throw new InvoiceNotFoundException("Invoice not found!");
        }
        payment.setInvoice(optionalInvoice.get());
        Payment savedPayment = paymentRepository.save(payment);
        return convertPaymentToDto(savedPayment);
    }

    public PaymentDto loadPaymentById(Long id) {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (optionalPayment.isEmpty()) {
            throw new NoSuchElementException("Payment not found!");
        }
        Payment payment = optionalPayment.get();
        return convertPaymentToDto(payment);
    }

    public List<PaymentDto> loadPaymentByInvoiceId(Long invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId).stream()
                .map(this::convertPaymentToDto)
                .collect(Collectors.toList());
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
