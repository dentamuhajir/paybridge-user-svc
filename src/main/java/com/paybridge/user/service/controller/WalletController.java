package com.paybridge.user.service.controller;


import com.paybridge.user.service.common.response.ApiResponse;
import com.paybridge.user.service.security.AppUserDetails;
import com.paybridge.user.service.service.WalletService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLOutput;

@RestController
@RequestMapping("api/v1/wallet")
public class WalletController {
    @Autowired
    private WalletService walletService;
    @GetMapping
    public ResponseEntity<?> getWallet(Authentication authentication) {
        AppUserDetails userDetails =
                (AppUserDetails) authentication.getPrincipal();

        String userId = userDetails.getId();

        ApiResponse response = walletService.getWallet(userId);
        return ResponseEntity.ok("ok");
    }

}
