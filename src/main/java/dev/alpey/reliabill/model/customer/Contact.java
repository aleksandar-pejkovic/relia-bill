package dev.alpey.reliabill.model.customer;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {

    private String phone;

    private String email;

    private String website;
}
