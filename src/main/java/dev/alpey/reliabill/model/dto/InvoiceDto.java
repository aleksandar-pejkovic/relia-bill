package dev.alpey.reliabill.model.dto;

import java.time.LocalDate;

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
public class InvoiceDto {

    private Long id;

    private String documentType;

    private String invoiceNumber;

    private LocalDate creationDate;

    private LocalDate dueDate;

    private String invoiceStatus;
}
