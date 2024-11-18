package com.inghubs.credit.service;


import com.inghubs.credit.model.BaseResponse;
import com.inghubs.credit.model.CreateLoanRequest;
import com.inghubs.credit.model.ListRequest;
import com.inghubs.credit.model.LoanListResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LoanService {

    BaseResponse createLoan(CreateLoanRequest createLoanRequest);

    List<LoanListResponse> listLoans(ListRequest listRequest);
}
