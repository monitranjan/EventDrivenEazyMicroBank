package com.eazybytes.common.event;

import lombok.Data;

@Data
public class LoanDataChangedEvent {

    private String mobileNumber;
    private Long loanNumber;

}
