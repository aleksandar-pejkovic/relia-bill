package dev.alpey.reliabill.model.dto;

import dev.alpey.reliabill.configuration.validation.product.description.Description;
import dev.alpey.reliabill.configuration.validation.product.name.ProductName;
import dev.alpey.reliabill.configuration.validation.product.plu.PluNumber;
import dev.alpey.reliabill.configuration.validation.product.price.Price;
import dev.alpey.reliabill.configuration.validation.product.taxrate.TaxRate;
import dev.alpey.reliabill.configuration.validation.product.unit.Unit;
import dev.alpey.reliabill.configuration.validation.user.username.Username;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductDto {

    private Long id;

    @PluNumber
    private int plu;

    @ProductName
    private String name;

    @Unit
    private String unit;

    @Description
    private String description;

    @TaxRate
    private int taxRate;

    @Price
    private Double price;

    @Username
    private String username;
}
