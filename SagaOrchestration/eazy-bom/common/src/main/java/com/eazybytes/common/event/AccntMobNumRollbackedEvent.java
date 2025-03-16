package com.eazybytes.common.event;

import lombok.Data;

@Data
public class AccntMobNumRollbackedEvent {
    private String customerId;
    private Long accountNumber;
    private String mobileNumber;
    private String newMobileNumber;
    private String errorMsg;
}
