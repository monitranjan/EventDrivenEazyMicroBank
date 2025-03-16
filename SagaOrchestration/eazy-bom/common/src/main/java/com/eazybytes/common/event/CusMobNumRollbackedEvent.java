package com.eazybytes.common.event;

import lombok.Data;

@Data
public class CusMobNumRollbackedEvent {
    private String customerId;
    private String mobileNumber;
    private String newMobileNumber;
    private String errorMsg;
}
