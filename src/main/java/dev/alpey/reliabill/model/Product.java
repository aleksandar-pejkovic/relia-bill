package dev.alpey.reliabill.model;

import dev.alpey.reliabill.enums.TaxRate;
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
