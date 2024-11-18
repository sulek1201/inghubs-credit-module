package com.inghubs.credit.controller;

import com.inghubs.credit.model.BaseResponse;
import com.inghubs.credit.model.CreateLoanRequest;
import com.inghubs.credit.model.ListRequest;
import com.inghubs.credit.model.LoanListResponse;
import com.inghubs.credit.service.impl.LoanServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;


@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanServiceImpl loanServiceImpl;

    @PostMapping("/create")
    public ResponseEntity<BaseResponse> createLoan(@RequestBody CreateLoanRequest createLoanRequest) {
        return ResponseEntity.ok(loanServiceImpl.createLoan(createLoanRequest));
    }

    @GetMapping("/list")
    public ResponseEntity<List<LoanListResponse>> listLoans(@RequestBody ListRequest listRequest) {
        return ResponseEntity.ok(loanServiceImpl.listLoans(listRequest));
    }
}
