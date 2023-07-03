package dev.alpey.reliabill.model.entity;

import dev.alpey.reliabill.enums.TaxRate;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer plu;

    private String name;

    private String unit;

    private String description;

    @Enumerated(EnumType.ORDINAL)
    private TaxRate taxRate;

    private Double price;

    private Double inStock = 0.0;

    private Double unitsSold = 0.0;

    private Double revenue = 0.0;

    private String username;

    public void registerSale(double quantity, double total) {
        this.inStock -= quantity;
        this.unitsSold += quantity;
        this.revenue += total;
    }

    public void discardSale(double quantity, double total) {
        this.inStock += quantity;
        this.unitsSold -= quantity;
        this.revenue -= total;
    }
}
