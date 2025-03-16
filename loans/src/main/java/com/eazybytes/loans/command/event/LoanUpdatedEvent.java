package com.eazybytes.loans.command.event;

import lombok.Data;

@Data
public class LoanUpdatedEvent {

    private Long loanNumber;
    private String mobileNumber;
    private String loanType;
    private int totalLoan;
    private int amountPaid;
    private int outstandingAmount;
    private boolean activeSw;

}
