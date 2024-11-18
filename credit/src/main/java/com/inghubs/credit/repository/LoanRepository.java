package com.inghubs.credit.repository;

import com.inghubs.credit.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByCustomerId(Long customerId);

    List<Loan> findByCustomerIdAndNumberOfInstallment(Long customerId, Integer numberOfInstallments);

    List<Loan> findByCustomerIdAndIsPaid(Long customerId, Boolean isPaid);

    List<Loan> findByCustomerIdAndNumberOfInstallmentAndIsPaid(Long customerId, Integer numberOfInstallments, Boolean isPaid);

}

