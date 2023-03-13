package dev.alpey.reliabill.model.dto;

import dev.alpey.reliabill.configuration.validation.invoice.invoicenumber.InvoiceNumber;
import dev.alpey.reliabill.configuration.validation.product.name.ProductName;
import dev.alpey.reliabill.configuration.validation.product.price.Price;
import dev.alpey.reliabill.configuration.validation.product.quantity.Quantity;
import dev.alpey.reliabill.configuration.validation.product.taxrate.TaxRate;
import dev.alpey.reliabill.configuration.validation.product.unit.Unit;
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
public class ItemDto {

    private Long id;

    @ProductName
    private String productName;

    @Quantity
    private Double quantity;

    @Unit
    private String unit;

    @Price
    private Double price;

    @TaxRate
    private Integer taxRate;

    @InvoiceNumber
    private String invoiceNumber;
}
