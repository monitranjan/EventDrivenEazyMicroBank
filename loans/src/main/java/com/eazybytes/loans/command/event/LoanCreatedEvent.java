package com.eazybytes.loans.command.event;

import lombok.Data;

@Data
public class LoanCreatedEvent {
    private Long loanNumber;
    private String mobileNumber;
    private String loanType;
    private String loanStatus;
    private int totalLoan;
    private int amountPaid;
    private int outstandingAmount;
    private boolean activeSw;
}
