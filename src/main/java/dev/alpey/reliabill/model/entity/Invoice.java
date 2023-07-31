package dev.alpey.reliabill.model.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.alpey.reliabill.enums.DocumentType;
import dev.alpey.reliabill.enums.InvoiceStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private String invoiceNumber;

    private LocalDate creationDate;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus invoiceStatus;

    private Double total;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "invoice")
    private List<Item> items = new ArrayList<>();

    @OneToMany(mappedBy = "invoice")
    private Set<Payment> payments = new HashSet<>();

    public void increaseTotal(Double amount) {
        this.total += amount;
    }

    public void decreaseTotal(Double amount) {
        this.total -= amount;
    }

    public void mapDocumentType(String documentTypeString) {
        if (documentTypeString.equalsIgnoreCase("Faktura")) {
            this.documentType = DocumentType.INVOICE;
        } else if (documentTypeString.equalsIgnoreCase("Profaktura")) {
            this.documentType = DocumentType.ESTIMATE;
        } else {
            this.documentType = null;
        }
    }

    public void mapInvoiceStatus(String invoiceStatusString) {
        if (invoiceStatusString.equalsIgnoreCase("Neizmireno")) {
            this.invoiceStatus = InvoiceStatus.PENDING;
        } else if (invoiceStatusString.equalsIgnoreCase("Delimično izmireno")) {
            this.invoiceStatus = InvoiceStatus.PARTIALLY_PAID;
        } else if (invoiceStatusString.equalsIgnoreCase("Plaćeno")) {
            this.invoiceStatus = InvoiceStatus.PAID;
        } else {
            this.invoiceStatus = null;
        }
    }
}
