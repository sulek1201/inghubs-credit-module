package com.inghubs.credit.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CreateLoanRequest {
    private Long customerId;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private Integer numberOfInstallments;
}

