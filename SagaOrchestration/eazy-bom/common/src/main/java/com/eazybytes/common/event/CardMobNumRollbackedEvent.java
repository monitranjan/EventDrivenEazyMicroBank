package com.eazybytes.common.event;

import lombok.Data;

@Data
public class CardMobNumRollbackedEvent {
    private String customerId;
    private Long accountNumber;
    private Long cardNumber;
    private String mobileNumber;
    private String newMobileNumber;
    private String errorMsg;
}
