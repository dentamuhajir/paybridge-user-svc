package com.paybridge.user.service.service.impl;

import com.paybridge.user.service.client.WalletClient;
import com.paybridge.user.service.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

public class WalletServiceImpl implements WalletService {
    @Autowired
    private WalletClient walletClient;
    public void getWallet(String userId){
        System.out.println("Hit the wallet service");
        //WalletGetResponse walletGetResponse = walletClient.getWallet(userId);
        //return ApiResponse.success("Inquiry of user" + walletGetResponse.getUserId(), walletGetResponse);
    }
}
