package dev.alpey.reliabill.model.dto.finance;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InvoiceTaxDetails {

    private Double tax;

    private Double subtotal;

    private Double totalFor20;

    private Double taxFor20;

    private Double subtotalFor20;

    private Double totalFor10;

    private Double taxFor10;

    private Double subtotalFor10;

    private Double totalFor0;
}
