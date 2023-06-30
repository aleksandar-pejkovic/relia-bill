package dev.alpey.reliabill.model.dto.finance;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CompanyBalance {

    private String companyName;

    private double totalRevenue;

    private double totalPayments;

    private double totalDebt;
}
