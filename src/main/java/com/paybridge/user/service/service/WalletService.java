package com.paybridge.user.service.service;

import com.paybridge.user.service.common.response.ApiResponse;
import org.springframework.stereotype.Service;

public interface WalletService {
    //public ApiResponse getWallet(String userId);
    public ApiResponse getWallet(String userId);
}
