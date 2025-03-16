package com.eazybytes.common.event;

import lombok.Data;

@Data
public class AccountDataChangedEvent {

    private String mobileNumber;
    private Long accountNumber;

}