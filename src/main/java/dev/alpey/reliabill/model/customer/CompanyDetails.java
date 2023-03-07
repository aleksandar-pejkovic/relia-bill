package dev.alpey.reliabill.model.customer;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDetails {

    private String name;

    private String director;

    private String registrationNumber;

    private String taxNumber;

    private String bankAccount;
}
