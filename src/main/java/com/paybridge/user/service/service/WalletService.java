package com.paybridge.user.service.service;

import com.paybridge.user.service.common.response.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public interface WalletService {
    //public ApiResponse getWallet(String userId);
    public void getWallet(String userId);
}
