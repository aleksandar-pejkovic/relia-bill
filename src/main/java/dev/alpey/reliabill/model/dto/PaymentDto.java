package dev.alpey.reliabill.model.dto;

import java.time.LocalDateTime;

import dev.alpey.reliabill.configuration.validation.product.price.Price;
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
public class PaymentDto {

    private Long id;

    private LocalDateTime paymentDate;

    @Price
    private Double amount;

    private Long invoiceId;
}
