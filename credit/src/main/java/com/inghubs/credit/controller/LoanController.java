package com.inghubs.credit.controller;

import com.inghubs.credit.model.BaseResponse;
import com.inghubs.credit.model.CreateLoanRequest;
import com.inghubs.credit.model.ListRequest;
import com.inghubs.credit.model.LoanListResponse;
import com.inghubs.credit.security.JwtTokenUtil;
import com.inghubs.credit.service.CustomerService;
import com.inghubs.credit.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

import static com.inghubs.credit.constant.ProjectConstants.*;


@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<BaseResponse> createLoan(@RequestBody CreateLoanRequest createLoanRequest, @RequestHeader("Authorization") String jwtToken) {
        if(JwtTokenUtil.parseUserRoleFromJwt(jwtToken).equals(ROLE_USER)){
            createLoanRequest.setCustomerId(customerService.getCustomerIdFromUserId(JwtTokenUtil.parseUserIdFromJwt(jwtToken)));
        }
        return ResponseEntity.ok(loanService.createLoan(createLoanRequest));
    }

    @GetMapping("/list")
    public ResponseEntity<List<LoanListResponse>> listLoans(@RequestBody ListRequest listRequest,  @RequestHeader("Authorization") String jwtToken) {
        if(JwtTokenUtil.parseUserRoleFromJwt(jwtToken).equals(ROLE_USER)){
            listRequest.setCustomerId(customerService.getCustomerIdFromUserId(JwtTokenUtil.parseUserIdFromJwt(jwtToken)));
        }
        return ResponseEntity.ok(loanService.listLoans(listRequest));
    }
}
