package com.inghubs.credit.controller;

import com.inghubs.credit.model.LoanInstallmentResponse;
import com.inghubs.credit.security.JwtTokenUtil;
import com.inghubs.credit.service.LoanInstallmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/installments")
@RequiredArgsConstructor
public class LoanInstallmentController {

    private final LoanInstallmentService loanInstallmentService;

    @GetMapping("/{loanId}")
    public ResponseEntity<List<LoanInstallmentResponse>> listInstallments(@PathVariable Long loanId, @RequestHeader("Authorization") String jwtToken) {
        List<LoanInstallmentResponse> response = loanInstallmentService.listInstallments(loanId, JwtTokenUtil.parseUserIdFromJwt(jwtToken), JwtTokenUtil.parseUserRoleFromJwt(jwtToken));
        return ResponseEntity.ok(response);
    }
}
