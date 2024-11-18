package com.inghubs.credit.service.impl;

import com.inghubs.credit.entity.Customer;
import com.inghubs.credit.entity.Loan;
import com.inghubs.credit.entity.LoanInstallment;
import com.inghubs.credit.exception.CreateLoanException;
import com.inghubs.credit.exception.CustomerNotFoundException;
import com.inghubs.credit.model.BaseResponse;
import com.inghubs.credit.model.CreateLoanRequest;
import com.inghubs.credit.model.ListRequest;
import com.inghubs.credit.model.LoanListResponse;
import com.inghubs.credit.repository.CustomerRepository;
import com.inghubs.credit.repository.LoanInstallmentRepository;
import com.inghubs.credit.repository.LoanRepository;
import com.inghubs.credit.service.LoanService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.inghubs.credit.constant.ErrorMessages.*;
import static com.inghubs.credit.constant.ProjectConstants.*;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanInstallmentRepository loanInstallmentRepository;

    private final static BigDecimal MIN_INTEREST_RATE = BigDecimal.valueOf(0.1);
    private final static BigDecimal MAX_INTEREST_RATE = BigDecimal.valueOf(0.5);
    private static final List<Integer> ALLOWED_INSTALLMENT_COUNTS = List.of(3, 6, 9, 12, 24);


    @Transactional
    public BaseResponse createLoan(CreateLoanRequest createLoanRequest) {
        Customer customer = customerRepository.findById(createLoanRequest.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(CUSTOMER_NOT_FOUND));

        BigDecimal totalLoanAmount = createLoanRequest.getAmount().multiply(createLoanRequest.getInterestRate().add(BigDecimal.ONE));

        BigDecimal availableLimit = customer.getCreditLimit().subtract(customer.getUsedCreditLimit());
        if (availableLimit.compareTo(totalLoanAmount) < 0) {
            throw new CreateLoanException(INSUFFICIENT_CREDIT_LIMIT);
        }
        if (!ALLOWED_INSTALLMENT_COUNTS.contains(createLoanRequest.getNumberOfInstallments())) {
            throw new CreateLoanException(INVALID_INSTALLMENT_COUNT);
        }

        if (createLoanRequest.getInterestRate().compareTo(MIN_INTEREST_RATE) < 0 || createLoanRequest.getInterestRate().compareTo(MAX_INTEREST_RATE) > 0) {
            throw new CreateLoanException(INVALID_INTEREST_RATE);
        }

        try {
            Loan loan = Loan.builder()
                    .customer(customer)
                    .loanAmount(totalLoanAmount)
                    .numberOfInstallment(createLoanRequest.getNumberOfInstallments())
                    .createDate(LocalDate.now())
                    .isPaid(false)
                    .build();

            loan = loanRepository.save(loan);

            BigDecimal installmentAmount = totalLoanAmount.divide(new BigDecimal(createLoanRequest.getNumberOfInstallments()), RoundingMode.FLOOR);
            List<LoanInstallment> installments = new ArrayList<>();

            for (int i = 1; i <= createLoanRequest.getNumberOfInstallments(); i++) {
                LoanInstallment installment = LoanInstallment.builder()
                        .loan(loan)
                        .amount(installmentAmount)
                        .paidAmount(BigDecimal.ZERO)
                        .isPaid(false)
                        .dueDate(LocalDate.now().plusMonths(i).withDayOfMonth(1))
                        .build();

                installments.add(installment);
            }

            loanInstallmentRepository.saveAll(installments);

            customer.setUsedCreditLimit(customer.getUsedCreditLimit().add(totalLoanAmount));
            customerRepository.save(customer);

            return new BaseResponse(true, SUCCESS_LOAN_MSG);
        } catch (Exception e) {
            throw new CreateLoanException(e.getMessage());
        }
    }


    public List<LoanListResponse> listLoans(ListRequest listRequest) {
        List<Loan> loans;

        if (listRequest.getNumberOfInstallments() != null && listRequest.getIsPaid() != null) {
            loans = loanRepository.findByCustomerIdAndNumberOfInstallmentAndIsPaid(
                    listRequest.getCustomerId(),
                    listRequest.getNumberOfInstallments(),
                    listRequest.getIsPaid());
        } else if (listRequest.getNumberOfInstallments() != null) {
            loans = loanRepository.findByCustomerIdAndNumberOfInstallment(
                    listRequest.getCustomerId(),
                    listRequest.getNumberOfInstallments());
        } else if (listRequest.getIsPaid() != null) {
            loans = loanRepository.findByCustomerIdAndIsPaid(
                    listRequest.getCustomerId(),
                    listRequest.getIsPaid());
        } else {
            loans = loanRepository.findByCustomerId(listRequest.getCustomerId());
        }

        return loans.stream()
                .map(this::mapToLoanListResponse).collect(Collectors.toList());
    }


    private LoanListResponse mapToLoanListResponse(Loan loan) {
        return LoanListResponse.builder()
                .loanAmount(loan.getLoanAmount())
                .numberOfInstallment(loan.getNumberOfInstallment())
                .createDate(loan.getCreateDate())
                .isPaid(loan.getIsPaid())
                .build();
    }
}
