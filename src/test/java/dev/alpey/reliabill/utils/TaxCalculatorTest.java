package dev.alpey.reliabill.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import dev.alpey.reliabill.enums.TaxRate;
import dev.alpey.reliabill.model.dto.finance.InvoiceTaxDetails;
import dev.alpey.reliabill.model.entity.Invoice;
import dev.alpey.reliabill.model.entity.Item;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TaxCalculator.class})
class TaxCalculatorTest {

    @Test
    void testCalculateItemTax() {

        Item item = Mockito.mock(Item.class);
        TaxRate taxRate = TaxRate.RATE_20;

        int taxRateValue = 20; // Example tax rate value
        double price = 120.0; // Example item price
        double quantity = 2.0; // Example item quantity
        double expectedPreTax = 100.0; // Expected pre-tax value
        double expectedTotal = 240.0; // Expected total value
        double expectedTax = 40.0; // Expected tax value
        double expectedSubtotal = 200.0; // Expected subtotal value

        Mockito.when(item.getTaxRate()).thenReturn(taxRate);
        Mockito.when(item.getPrice()).thenReturn(price);
        Mockito.when(item.getQuantity()).thenReturn(quantity);

        TaxCalculator.calculateItemTax(item);

        Mockito.verify(item).setPreTax(expectedPreTax);
        Mockito.verify(item).setTotal(expectedTotal);
        Mockito.verify(item).setTax(expectedTax);
        Mockito.verify(item).setSubtotal(expectedSubtotal);
    }

    @Test
    void testGetInvoiceTaxDetails() {

        Invoice invoice = Mockito.mock(Invoice.class);
        List<Item> items = new ArrayList<>();

        double item1Tax = 2.0;
        double item2Tax = 3.0;
        double expectedTax = item1Tax + item2Tax;
        double total = 15.0;
        double expectedSubtotal = total - expectedTax;

        Item item1 = Mockito.mock(Item.class);
        Mockito.when(item1.getTax()).thenReturn(item1Tax);
        Item item2 = Mockito.mock(Item.class);
        Mockito.when(item2.getTax()).thenReturn(item2Tax);
        items.add(item1);
        items.add(item2);

        Mockito.when(invoice.getItems()).thenReturn(items);
        Mockito.when(invoice.getTotal()).thenReturn(total);

        InvoiceTaxDetails invoiceTaxDetails = TaxCalculator.getInvoiceTaxDetails(invoice);

        assertEquals(expectedTax, invoiceTaxDetails.getTax());
        assertEquals(expectedSubtotal, invoiceTaxDetails.getSubtotal());
    }
}