package dev.alpey.reliabill.model.dto;

import dev.alpey.reliabill.configuration.validation.company.bankaccount.BankAccount;
import dev.alpey.reliabill.configuration.validation.company.phone.Phone;
import dev.alpey.reliabill.configuration.validation.company.registrationnumber.RegistrationNumber;
import dev.alpey.reliabill.configuration.validation.company.taxnumber.TaxNumber;
import dev.alpey.reliabill.configuration.validation.company.website.Website;
import dev.alpey.reliabill.configuration.validation.company.zip.Zip;
import dev.alpey.reliabill.configuration.validation.user.name.Name;
import jakarta.validation.constraints.Email;
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
public class CompanyDto {

    private Long id;

    @Name
    private String name;

    @Name
    private String director;

    @RegistrationNumber
    private String registrationNumber;

    @TaxNumber
    private String taxNumber;

    @BankAccount
    private String bankAccount;

    private String street;

    @Zip
    private String zip;

    @Name
    private String city;

    @Phone
    private String phone;

    @Email
    private String email;

    @Website
    private String website;
}
