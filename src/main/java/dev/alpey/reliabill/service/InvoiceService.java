package dev.alpey.reliabill.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import dev.alpey.reliabill.configuration.exceptions.company.CompanyNotFoundException;
import dev.alpey.reliabill.configuration.exceptions.invoice.InvoiceNotFoundException;
import dev.alpey.reliabill.enums.DocumentType;
import dev.alpey.reliabill.enums.InvoiceStatus;
import dev.alpey.reliabill.model.dto.InvoiceDto;
import dev.alpey.reliabill.model.entity.Company;
import dev.alpey.reliabill.model.entity.Invoice;
import dev.alpey.reliabill.repository.CompanyRepository;
import dev.alpey.reliabill.repository.InvoiceRepository;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<InvoiceDto> searchInvoices(String searchTerm, Principal principal) {
        return invoiceRepository.searchByInvoiceNumber(searchTerm).stream()
                .filter(invoice -> principal.getName().equals(invoice.getCompany().getUser().getUsername()))
                .map(this::convertInvoiceToDto)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "invoicesByUser", key = "#principal.getName()")
    public InvoiceDto createInvoice(InvoiceDto invoiceDto, Principal principal) {
        Company company = obtainClientCompany(invoiceDto.getCompanyId());
        Invoice invoice = modelMapper.map(invoiceDto, Invoice.class);
        invoice.setInvoiceStatus(InvoiceStatus.valueOf(invoiceDto.getInvoiceStatus()));
        invoice.setDocumentType(DocumentType.valueOf(invoiceDto.getDocumentType()));
        invoice.setCompany(company);
        invoice.setTotal(0.0);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return convertInvoiceToDto(savedInvoice);
    }

    @CacheEvict(value = "invoicesByUser", key = "#principal.getName()")
    public InvoiceDto updateInvoice(InvoiceDto invoiceDto, Principal principal) {
        Invoice invoice = obtainInvoice(invoiceDto.getId());
        modelMapper.map(invoiceDto, invoice);
        invoice.setInvoiceStatus(InvoiceStatus.valueOf(invoiceDto.getInvoiceStatus()));
        invoice.setDocumentType(DocumentType.valueOf(invoiceDto.getDocumentType()));
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return convertInvoiceToDto(updatedInvoice);
    }

    @CacheEvict(value = "invoicesByUser", key = "#principal.getName()")
    public void deleteInvoice(Long id, Principal principal) {
        Invoice invoice = obtainInvoice(id);
        invoiceRepository.delete(invoice);
    }

    @Cacheable(value = "invoicesByUser", key = "#principal.getName()")
    public List<InvoiceDto> loadAllInvoicesForLoggedUser(Principal principal) {
        List<Invoice> invoices = invoiceRepository.findByUsername(principal.getName());
        return convertInvoicesToDtoList(invoices);
    }

    public List<InvoiceDto> loadAllInvoicesForCompany(Long companyId) {
        Company company = obtainClientCompany(companyId);
        List<Invoice> invoices = invoiceRepository.findByCompany(company);
        return convertInvoicesToDtoList(invoices);
    }

    private Company obtainClientCompany(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found!"));
    }

    private Invoice obtainInvoice(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found!"));
    }

    private List<InvoiceDto> convertInvoicesToDtoList(List<Invoice> invoices) {
        if (invoices.isEmpty()) {
            return new ArrayList<>();
        }
        return invoices.stream()
                .map(this::convertInvoiceToDto)
                .collect(Collectors.toList());
    }

    private InvoiceDto convertInvoiceToDto(Invoice invoice) {
        InvoiceDto invoiceDto = modelMapper.map(invoice, InvoiceDto.class);
        invoiceDto.setInvoiceStatus(invoice.getInvoiceStatus().name());
        invoiceDto.setDocumentType(invoice.getDocumentType().name());
        invoiceDto.setCompanyName(invoice.getCompany().getName());
        invoiceDto.setCompanyId(invoice.getCompany().getId());
        return invoiceDto;
    }
}
