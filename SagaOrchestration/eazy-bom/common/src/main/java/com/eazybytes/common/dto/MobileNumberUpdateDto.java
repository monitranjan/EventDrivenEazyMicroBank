package com.eazybytes.common.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MobileNumberUpdateDto {

    @NotEmpty(message = "Customer ID cannot be empty")
    private String customerId;

    private Long accountNumber;

    private Long loanNumber;

    private Long cardNumber;

    @NotEmpty(message = "Current mobile number cannot be empty")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    private String currentMobileNumber;

    @NotEmpty(message = "New mobile number cannot be empty")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    private String newMobileNumber;

}
