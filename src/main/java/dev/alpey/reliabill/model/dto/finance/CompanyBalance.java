package dev.alpey.reliabill.model.dto.finance;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CompanyBalance {

    private String name;

    private double revenue;

    private double payments;

    private double debt;
}
