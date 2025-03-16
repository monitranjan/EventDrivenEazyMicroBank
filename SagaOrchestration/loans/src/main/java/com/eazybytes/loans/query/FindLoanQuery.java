package com.eazybytes.loans.query;

import lombok.Value;

@Value
public class FindLoanQuery {
    private final String mobileNumber;
}
