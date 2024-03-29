package dev.alpey.reliabill.model.entity;

import dev.alpey.reliabill.enums.TaxRate;
import dev.alpey.reliabill.utils.TaxCalculator;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private Double quantity;

    private String unit;

    private Double price;

    @Enumerated(EnumType.ORDINAL)
    private TaxRate taxRate;

    private Double preTax;

    private Double total;

    private Double tax;

    private Double subtotal;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    public void calculateTax() {
        TaxCalculator.calculateItemTax(this);
    }
}
