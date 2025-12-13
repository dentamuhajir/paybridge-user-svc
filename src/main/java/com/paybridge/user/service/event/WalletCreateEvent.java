package com.paybridge.user.service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletCreateEvent {
    private String user;
    private String Currency;
}
