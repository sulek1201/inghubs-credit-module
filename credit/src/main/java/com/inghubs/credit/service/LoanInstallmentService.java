package com.inghubs.credit.service;


import com.inghubs.credit.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LoanInstallmentService {
    List<LoanInstallmentResponse> listInstallments(Long loanId, Long userId, String userRole);

    LoanPaymentResponse payLoanInstallment(LoanPaymentRequest request);
}
