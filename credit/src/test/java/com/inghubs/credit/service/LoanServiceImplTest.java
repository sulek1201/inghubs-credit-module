package com.inghubs.credit.service;

import com.inghubs.credit.entity.Customer;
import com.inghubs.credit.entity.Loan;
import com.inghubs.credit.exception.CreateLoanException;
import com.inghubs.credit.exception.CustomerNotFoundException;
import com.inghubs.credit.model.*;
import com.inghubs.credit.repository.CustomerRepository;
import com.inghubs.credit.repository.LoanInstallmentRepository;
import com.inghubs.credit.repository.LoanRepository;
import com.inghubs.credit.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @InjectMocks
    private LoanServiceImpl loanService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    @Test
    void createLoan_ShouldCreateLoanSuccessfully() {
        Customer customer = Customer.builder()
                .id(1L)
                .creditLimit(BigDecimal.valueOf(10000))
                .usedCreditLimit(BigDecimal.valueOf(2000))
                .build();

        CreateLoanRequest createLoanRequest = CreateLoanRequest.builder()
                .customerId(1L)
                .amount(BigDecimal.valueOf(3000))
                .interestRate(BigDecimal.valueOf(0.1))
                .numberOfInstallments(6)
                .build();

        Loan loan = Loan.builder()
                .id(1L)
                .customer(customer)
                .loanAmount(BigDecimal.valueOf(3300))
                .numberOfInstallment(6)
                .createDate(LocalDate.now())
                .isPaid(false)
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(loanRepository.save(any())).thenReturn(loan);

        BaseResponse response = loanService.createLoan(createLoanRequest);

        assertThat(response.isStatus()).isTrue();
        assertThat(response.getMsg()).isEqualTo("Requested Loan is created");

        verify(customerRepository, times(1)).findById(1L);
        verify(loanInstallmentRepository, times(1)).saveAll(anyList());
    }

    @Test
    void createLoan_ShouldThrowException_WhenCustomerNotFound() {
        CreateLoanRequest createLoanRequest = CreateLoanRequest.builder()
                .customerId(1L)
                .amount(BigDecimal.valueOf(3000))
                .interestRate(BigDecimal.valueOf(0.1))
                .numberOfInstallments(6)
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.createLoan(createLoanRequest))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer not found.");

        verify(customerRepository, times(1)).findById(1L);
        verifyNoInteractions(loanRepository);
        verifyNoInteractions(loanInstallmentRepository);
    }

    @Test
    void createLoan_ShouldThrowException_WhenInsufficientCreditLimit() {
        Customer customer = Customer.builder()
                .id(1L)
                .creditLimit(BigDecimal.valueOf(5000))
                .usedCreditLimit(BigDecimal.valueOf(4000))
                .build();

        CreateLoanRequest createLoanRequest = CreateLoanRequest.builder()
                .customerId(1L)
                .amount(BigDecimal.valueOf(2000))
                .interestRate(BigDecimal.valueOf(0.1))
                .numberOfInstallments(6)
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> loanService.createLoan(createLoanRequest))
                .isInstanceOf(CreateLoanException.class)
                .hasMessage("Customer does not have enough credit limit.");

        verify(customerRepository, times(1)).findById(1L);
        verifyNoInteractions(loanRepository);
        verifyNoInteractions(loanInstallmentRepository);
    }

    @Test
    void listLoans_ShouldReturnLoansForCustomer() {
        ListRequest listRequest = ListRequest.builder()
                .customerId(1L)
                .build();

        Loan loan1 = Loan.builder()
                .loanAmount(BigDecimal.valueOf(3000))
                .numberOfInstallment(6)
                .createDate(LocalDate.now().minusMonths(1))
                .isPaid(false)
                .build();

        Loan loan2 = Loan.builder()
                .loanAmount(BigDecimal.valueOf(5000))
                .numberOfInstallment(12)
                .createDate(LocalDate.now().minusMonths(3))
                .isPaid(true)
                .build();

        when(loanRepository.findByCustomerId(1L)).thenReturn(List.of(loan1, loan2));

        List<LoanListResponse> loans = loanService.listLoans(listRequest);

        assertThat(loans).hasSize(2);
        assertThat(loans.get(0).getLoanAmount()).isEqualTo(loan1.getLoanAmount());
        assertThat(loans.get(1).getIsPaid()).isTrue();

        verify(loanRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    void listLoans_ShouldReturnEmptyList_WhenNoLoansFound() {
        ListRequest listRequest = ListRequest.builder()
                .customerId(1L)
                .build();

        when(loanRepository.findByCustomerId(1L)).thenReturn(List.of());

        List<LoanListResponse> loans = loanService.listLoans(listRequest);

        assertThat(loans).isEmpty();
        verify(loanRepository, times(1)).findByCustomerId(1L);
    }
}

