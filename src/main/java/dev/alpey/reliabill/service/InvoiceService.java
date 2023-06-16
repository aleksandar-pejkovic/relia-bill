package dev.alpey.reliabill.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        Optional<Company> optionalCompany = companyRepository.findById(invoiceDto.getCompanyId());
        Company company = optionalCompany.orElseThrow(() -> new CompanyNotFoundException("Invalid company!"));
        Invoice invoice = modelMapper.map(invoiceDto, Invoice.class);
        invoice.setInvoiceStatus(InvoiceStatus.valueOf(invoiceDto.getInvoiceStatus()));
        invoice.setDocumentType(DocumentType.valueOf(invoiceDto.getDocumentType()));
        invoice.setCompany(company);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return convertInvoiceToDto(savedInvoice);
    }

    @CacheEvict(value = "invoicesByUser", key = "#principal.getName()")
    public InvoiceDto updateInvoice(InvoiceDto invoiceDto, Principal principal) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceDto.getId());
        Invoice invoice = optionalInvoice.orElseThrow(() -> new InvoiceNotFoundException("Invoice not found!"));
        modelMapper.map(invoiceDto, invoice);
        invoice.setInvoiceStatus(InvoiceStatus.valueOf(invoiceDto.getInvoiceStatus()));
        invoice.setDocumentType(DocumentType.valueOf(invoiceDto.getDocumentType()));
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return convertInvoiceToDto(updatedInvoice);
    }

    @CacheEvict(value = "invoicesByUser", key = "#principal.getName()")
    public void deleteInvoice(Long id, Principal principal) {
        if (invoiceRepository.existsById(id)) {
            Invoice invoice = invoiceRepository.findById(id).orElseThrow();
            invoiceRepository.delete(invoice);
        } else {
            throw new InvoiceNotFoundException("Invoice not found!");
        }
    }

    @Cacheable(value = "invoicesByUser", key = "#principal.getName()")
    public List<InvoiceDto> loadAllInvoicesForLoggedUser(Principal principal) {
        List<Invoice> invoices = invoiceRepository.findByUsername(principal.getName());
        return convertInvoicesToDtoList(invoices);
    }

    public List<InvoiceDto> loadAllInvoicesForCompany(Long companyId) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        Company company = optionalCompany.orElseThrow(() -> new CompanyNotFoundException("Company not found!"));
        List<Invoice> invoices = invoiceRepository.findByCompany(company);
        return convertInvoicesToDtoList(invoices);
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
        invoiceDto.setCompanyId(invoice.getCompany().getId());
        return invoiceDto;
    }
}
