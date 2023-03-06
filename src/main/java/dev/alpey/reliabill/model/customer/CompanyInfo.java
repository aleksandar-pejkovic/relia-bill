package dev.alpey.reliabill.model.customer;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyInfo {

    private String name;

    private String director;

    private String registrationNumber;

    private String taxNumber;

    private String bankAccount;
}
