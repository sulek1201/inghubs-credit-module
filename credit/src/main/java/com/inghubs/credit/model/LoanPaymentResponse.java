package com.inghubs.credit.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LoanPaymentResponse {
    private int numberOfInstallmentsPaid;
    private BigDecimal totalAmountSpent;
    private boolean isLoanFullyPaid;
}
