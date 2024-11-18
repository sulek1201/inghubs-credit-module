package com.inghubs.credit.model;

import lombok.Data;

@Data
public class ListRequest {
    private Long customerId;
    private Integer numberOfInstallments;
    private Boolean isPaid;
}
