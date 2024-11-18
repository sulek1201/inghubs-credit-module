package com.inghubs.credit.service.impl;

import com.inghubs.credit.entity.Loan;
import com.inghubs.credit.entity.LoanInstallment;
import com.inghubs.credit.exception.LoanNotFoundException;
import com.inghubs.credit.model.LoanInstallmentResponse;
import com.inghubs.credit.repository.LoanInstallmentRepository;
import com.inghubs.credit.repository.LoanRepository;
import com.inghubs.credit.service.LoanInstallmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.inghubs.credit.constant.ProjectConstants.*;

@Service
@RequiredArgsConstructor
public class LoanInstallmentServiceImpl implements LoanInstallmentService {

    private final LoanInstallmentRepository loanInstallmentRepository;
    private final LoanRepository loanRepository;

    public List<LoanInstallmentResponse> listInstallments(Long loanId, Long userId, String userRole) {
        Optional<Loan> loan = loanRepository.findById(loanId);
        if (loan.isEmpty()) {
            throw new LoanNotFoundException("Loan with ID " + loanId + " not found.");
        }
        if(userRole.equals(ROLE_USER) && !loan.get().getCustomer().getUserId().getId().equals(userId)){
            throw new LoanNotFoundException("Loan with ID " + loanId + " not found for customer: " + loan.get().getCustomer().getId());
        }

        List<LoanInstallment> installments = loanInstallmentRepository.findByLoanId(loanId);

        return installments.stream()
                .map(installment -> LoanInstallmentResponse.builder()
                        .amount(installment.getAmount())
                        .paidAmount(installment.getPaidAmount())
                        .dueDate(installment.getDueDate())
                        .paymentDate(installment.getPaymentDate())
                        .isPaid(installment.getIsPaid())
                        .build())
                .collect(Collectors.toList());
    }
}
