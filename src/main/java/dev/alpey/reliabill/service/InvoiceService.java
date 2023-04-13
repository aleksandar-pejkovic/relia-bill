package dev.alpey.reliabill.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {
        Optional<Company> optionalCompany = companyRepository.findById(invoiceDto.getCompanyId());
        Company company = optionalCompany.orElseThrow(() -> new CompanyNotFoundException("Invalid company!"));
        Invoice invoice = modelMapper.map(invoiceDto, Invoice.class);
        invoice.setInvoiceStatus(InvoiceStatus.valueOf(invoiceDto.getInvoiceStatus()));
        invoice.setDocumentType(DocumentType.valueOf(invoiceDto.getDocumentType()));
        invoice.setCompany(company);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return convertInvoiceToDto(savedInvoice);
    }

    public InvoiceDto updateInvoice(InvoiceDto invoiceDto) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceDto.getId());
        Invoice invoice = optionalInvoice.orElseThrow(() -> new InvoiceNotFoundException("Invoice not found!"));
        modelMapper.map(invoiceDto, invoice);
        invoice.setInvoiceStatus(InvoiceStatus.valueOf(invoiceDto.getInvoiceStatus()));
        invoice.setDocumentType(DocumentType.valueOf(invoiceDto.getDocumentType()));
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return convertInvoiceToDto(updatedInvoice);
    }

    public void deleteInvoice(Long id) {
        if (invoiceRepository.existsById(id)) {
            invoiceRepository.deleteById(id);
        } else {
            throw new InvoiceNotFoundException("Invoice not found!");
        }
    }

    public List<InvoiceDto> loadAllInvoicesForLoggedUser(Principal principal) {
        List<Invoice> invoices = invoiceRepository.findByUsername(principal.getName());
        if (invoices.isEmpty()) {
            return new ArrayList<>();
        }
        return convertInvoicesToDtoList(invoices);
    }

    public List<InvoiceDto> loadAllInvoicesForCompany(Long companyId) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        Company company = optionalCompany.orElseThrow(() -> new CompanyNotFoundException("Company not found!"));
        List<Invoice> invoices = invoiceRepository.findByCompany(company);
        if (invoices.isEmpty()) {
            return new ArrayList<>();
        }
        return convertInvoicesToDtoList(invoices);
    }

    private List<InvoiceDto> convertInvoicesToDtoList(List<Invoice> invoices) {
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
