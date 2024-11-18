package com.inghubs.credit.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListRequest {
    private Long customerId;
    private Integer numberOfInstallments;
    private Boolean isPaid;
}
