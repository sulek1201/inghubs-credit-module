package com.inghubs.credit.repository;

import com.inghubs.credit.entity.Loan;
import com.inghubs.credit.entity.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {

    List<LoanInstallment> findByLoanId(Long loanId);
    List<LoanInstallment> findAllByLoanAndIsPaidFalseOrderByDueDateAsc(Loan loan);

}

