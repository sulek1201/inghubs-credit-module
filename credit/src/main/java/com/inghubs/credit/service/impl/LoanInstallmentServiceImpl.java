package com.inghubs.credit.service.impl;

import com.inghubs.credit.entity.Customer;
import com.inghubs.credit.entity.Loan;
import com.inghubs.credit.entity.LoanInstallment;
import com.inghubs.credit.exception.CreateLoanException;
import com.inghubs.credit.exception.LoanNotFoundException;
import com.inghubs.credit.exception.LoanPaymentException;
import com.inghubs.credit.model.LoanInstallmentResponse;
import com.inghubs.credit.model.LoanPaymentRequest;
import com.inghubs.credit.model.LoanPaymentResponse;
import com.inghubs.credit.repository.CustomerRepository;
import com.inghubs.credit.repository.LoanInstallmentRepository;
import com.inghubs.credit.repository.LoanRepository;
import com.inghubs.credit.service.LoanInstallmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.inghubs.credit.constant.ErrorMessages.*;
import static com.inghubs.credit.constant.ProjectConstants.*;

@Service
@RequiredArgsConstructor
public class LoanInstallmentServiceImpl implements LoanInstallmentService {

    private final LoanInstallmentRepository loanInstallmentRepository;
    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;

    public static final BigDecimal REWARD_RATE = BigDecimal.valueOf(0.001);
    public static final BigDecimal PENALTY_RATE = BigDecimal.valueOf(0.001);
    public static final int MAX_PAYMENT_MONTHS = 3;

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


    @Transactional
    @Override
    public LoanPaymentResponse payLoanInstallment(LoanPaymentRequest request) {
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + request.getLoanId()));

        Customer customer = loan.getCustomer();
        BigDecimal amountToPay = request.getAmount();
        int numberOfInstallmentsPaid = 0;
        BigDecimal totalAmountSpent = BigDecimal.ZERO;
        LocalDate currentDate = LocalDate.now();

        List<LoanInstallment> installments = loanInstallmentRepository
                .findAllByLoanAndIsPaidFalseOrderByDueDateAsc(loan);

        if (ChronoUnit.MONTHS.between(currentDate, installments.get(0).getDueDate()) > MAX_PAYMENT_MONTHS) {
            throw new CreateLoanException(INSTALLMENT_PAYMENT_RESTRICTION);
        }

        for (LoanInstallment installment : installments) {
            if (ChronoUnit.MONTHS.between(currentDate, installment.getDueDate()) > MAX_PAYMENT_MONTHS) {
                continue;
            }

            BigDecimal installmentAmount = installment.getAmount();
            LocalDate dueDate = installment.getDueDate();

            if (currentDate.isBefore(dueDate)) {
                long daysBefore = ChronoUnit.DAYS.between(currentDate, dueDate);
                installmentAmount = installmentAmount.subtract(
                        installmentAmount.multiply(REWARD_RATE.multiply(BigDecimal.valueOf(daysBefore)))
                );
            } else if (currentDate.isAfter(dueDate)) {
                long daysAfter = ChronoUnit.DAYS.between(dueDate, currentDate);
                installmentAmount = installmentAmount.add(
                        installmentAmount.multiply(PENALTY_RATE.multiply(BigDecimal.valueOf(daysAfter)))
                );
            }


            if (amountToPay.compareTo(installmentAmount) >= 0) {
                installment.setPaidAmount(installmentAmount);
                installment.setPaymentDate(currentDate);
                installment.setIsPaid(true);
                amountToPay = amountToPay.subtract(installmentAmount);
                totalAmountSpent = totalAmountSpent.add(installmentAmount);
                numberOfInstallmentsPaid++;

                customer.setUsedCreditLimit(customer.getUsedCreditLimit().subtract(installmentAmount));
            } else {
                if (numberOfInstallmentsPaid == 0) {
                    throw new LoanPaymentException(INSUFFICIENT_AMOUNT_FIRST_INSTALLMENT);
                }
                break;
            }
        }

        boolean isLoanFullyPaid = installments.stream().allMatch(LoanInstallment::getIsPaid);
        if (isLoanFullyPaid) {
            loan.setIsPaid(true);
        }

        loanRepository.save(loan);
        customerRepository.save(customer);

        return LoanPaymentResponse.builder()
                .numberOfInstallmentsPaid(numberOfInstallmentsPaid)
                .totalAmountSpent(totalAmountSpent)
                .isLoanFullyPaid(isLoanFullyPaid)
                .build();
    }
}
