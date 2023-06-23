package dev.alpey.reliabill.model.dto.finance;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InvoiceTaxDetails {

    private Double tax;
    private Double subtotal;

    private List<TaxDetails> taxDetailsList;
}
