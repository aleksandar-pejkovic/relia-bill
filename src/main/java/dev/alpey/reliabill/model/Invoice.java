package dev.alpey.reliabill.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
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
@Table(name = "documents")
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

    private String documentNumber;

    private LocalDate creationDate;

    private LocalDate dueDate;

    private BigDecimal total;

    private BigDecimal tax;

    private BigDecimal subtotal;

    private BigDecimal taxRate20total;

    private BigDecimal taxRate20tax;

    private BigDecimal taxRate20subtotal;

    private BigDecimal taxRate10total;

    private BigDecimal taxRate10tax;

    private BigDecimal taxRate10subtotal;

    private BigDecimal paidAmount;

    private BigDecimal remainingDebt;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus invoiceStatus;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "invoice")
    private Set<Item> items = new HashSet<>();

    @OneToMany(mappedBy = "invoice")
    private Set<Payment> payments = new HashSet<>();
}
