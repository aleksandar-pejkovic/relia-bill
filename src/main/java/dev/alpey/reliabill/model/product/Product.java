package dev.alpey.reliabill.model.product;

import java.util.HashSet;
import java.util.Set;

import dev.alpey.reliabill.model.document.Item;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

    private ProductDetails productDetails;

    @OneToMany(mappedBy = "product")
    private Set<Item> items = new HashSet<>();
}