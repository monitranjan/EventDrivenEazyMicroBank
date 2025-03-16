package com.eazybytes.loans.command.event;

import lombok.Data;

@Data
public class LoanDeletedEvent {

    private Long loanNumber;
    private boolean activeSw;

}
