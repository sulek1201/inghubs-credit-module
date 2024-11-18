package com.inghubs.credit.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_no",referencedColumnName = "id", nullable = false)
    private Customer customer;

    private BigDecimal loanAmount;

    private Integer numberOfInstallment;

    private LocalDate createDate;

    private Boolean isPaid;

}

