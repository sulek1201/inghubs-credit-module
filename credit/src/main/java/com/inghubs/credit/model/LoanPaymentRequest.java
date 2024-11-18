package com.inghubs.credit.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LoanPaymentRequest {
    private Long loanId;
    private BigDecimal amount;
}

