package dev.alpey.reliabill.model.dto.finance;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TaxDetails {

    private int taxRate;
    private Double subtotal;
    private Double tax;
    private Double total;
}
