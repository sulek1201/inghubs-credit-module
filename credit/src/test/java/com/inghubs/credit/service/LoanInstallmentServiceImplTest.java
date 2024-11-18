package com.inghubs.credit.service;

import com.inghubs.credit.entity.Customer;
import com.inghubs.credit.entity.Loan;
import com.inghubs.credit.entity.LoanInstallment;
import com.inghubs.credit.entity.User;
import com.inghubs.credit.exception.LoanNotFoundException;
import com.inghubs.credit.exception.LoanPaymentException;
import com.inghubs.credit.model.LoanInstallmentResponse;
import com.inghubs.credit.model.LoanPaymentRequest;
import com.inghubs.credit.model.LoanPaymentResponse;
import com.inghubs.credit.repository.CustomerRepository;
import com.inghubs.credit.repository.LoanInstallmentRepository;
import com.inghubs.credit.repository.LoanRepository;
import com.inghubs.credit.service.impl.LoanInstallmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.inghubs.credit.constant.ProjectConstants.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LoanInstallmentServiceImplTest {

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private LoanInstallmentServiceImpl loanInstallmentService;

    private Loan loan;
    private Customer customer;
    private LoanInstallment installment1, installment2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = Customer.builder()
                .id(1L)
                .name("Test Customer")
                .usedCreditLimit(BigDecimal.valueOf(5000))
                .build();

        loan = Loan.builder()
                .id(1L)
                .customer(customer)
                .loanAmount(BigDecimal.valueOf(10000))
                .isPaid(false)
                .build();

        installment1 = LoanInstallment.builder()
                .id(1L)
                .loan(loan)
                .amount(BigDecimal.valueOf(1000))
                .dueDate(LocalDate.now())
                .isPaid(false)
                .build();

        installment2 = LoanInstallment.builder()
                .id(2L)
                .loan(loan)
                .amount(BigDecimal.valueOf(1000))
                .dueDate(LocalDate.now().plusMonths(1))
                .isPaid(false)
                .build();
    }

    @Test
    void testListInstallments_ValidLoan() {
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .build();

        Customer customer = Customer.builder()
                .id(1L)
                .userId(user)
                .build();

        Loan loan = Loan.builder()
                .id(1L)
                .customer(customer)
                .isPaid(false)
                .build();

        LoanInstallment installment1 = LoanInstallment.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(1000))
                .dueDate(LocalDate.now().plusMonths(1))
                .isPaid(false)
                .build();

        LoanInstallment installment2 = LoanInstallment.builder()
                .id(2L)
                .amount(BigDecimal.valueOf(500))
                .dueDate(LocalDate.now().plusMonths(2))
                .isPaid(false)
                .build();

        when(loanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findByLoanId(loan.getId())).thenReturn(List.of(installment1, installment2));

        List<LoanInstallmentResponse> response = loanInstallmentService.listInstallments(loan.getId(), user.getId(), ROLE_USER);

        assertThat(response).hasSize(2);
        assertThat(response.get(0).getAmount()).isEqualTo(installment1.getAmount());
        assertThat(response.get(1).getDueDate()).isEqualTo(installment2.getDueDate());

        verify(loanRepository, times(1)).findById(loan.getId());
        verify(loanInstallmentRepository, times(1)).findByLoanId(loan.getId());
    }


    @Test
    void testListInstallments_LoanNotFound() {
        when(loanRepository.findById(loan.getId())).thenReturn(Optional.empty());

        assertThrows(LoanNotFoundException.class,
                () -> loanInstallmentService.listInstallments(loan.getId(), 1L, ROLE_USER));

        verify(loanRepository, times(1)).findById(loan.getId());
    }

    @Test
    void testPayLoanInstallment_SuccessfulPayment() {
        LoanPaymentRequest request = LoanPaymentRequest.builder()
                .loanId(loan.getId())
                .amount(BigDecimal.valueOf(2000))
                .build();

        when(loanRepository.findById(request.getLoanId())).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findAllByLoanAndIsPaidFalseOrderByDueDateAsc(loan))
                .thenReturn(List.of(installment1, installment2));

        LoanPaymentResponse response = loanInstallmentService.payLoanInstallment(request);

        assertThat(response.getNumberOfInstallmentsPaid()).isEqualTo(2);
        assertThat(response.getTotalAmountSpent().compareTo(BigDecimal.valueOf(1970.0))).isEqualTo(0);
        assertThat(response.isLoanFullyPaid()).isTrue();
    }

    @Test
    void testPayLoanInstallment_InsufficientAmountForFirstInstallment() {
        LoanPaymentRequest request = LoanPaymentRequest.builder()
                .loanId(loan.getId())
                .amount(BigDecimal.valueOf(500))
                .build();

        when(loanRepository.findById(request.getLoanId())).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findAllByLoanAndIsPaidFalseOrderByDueDateAsc(loan))
                .thenReturn(List.of(installment1));

        assertThrows(LoanPaymentException.class,
                () -> loanInstallmentService.payLoanInstallment(request));

        verify(loanInstallmentRepository, never()).save(any(LoanInstallment.class));
    }

    @Test
    void testPayLoanInstallment_LoanNotFound() {
        LoanPaymentRequest request = LoanPaymentRequest.builder()
                .loanId(loan.getId())
                .amount(BigDecimal.valueOf(1000))
                .build();

        when(loanRepository.findById(request.getLoanId())).thenReturn(Optional.empty());

        assertThrows(LoanNotFoundException.class,
                () -> loanInstallmentService.payLoanInstallment(request));

        verify(loanRepository, times(1)).findById(request.getLoanId());
        verify(loanInstallmentRepository, never()).save(any(LoanInstallment.class));
    }
}
