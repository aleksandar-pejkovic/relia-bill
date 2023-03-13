package dev.alpey.reliabill.dto;

import dev.alpey.reliabill.configuration.validation.product.description.Description;
import dev.alpey.reliabill.configuration.validation.product.name.ProductName;
import dev.alpey.reliabill.configuration.validation.product.plu.PluNumber;
import dev.alpey.reliabill.configuration.validation.product.price.Price;
import dev.alpey.reliabill.configuration.validation.product.taxrate.TaxRate;
import dev.alpey.reliabill.configuration.validation.product.unit.Unit;

public class ProductDto {

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
}
