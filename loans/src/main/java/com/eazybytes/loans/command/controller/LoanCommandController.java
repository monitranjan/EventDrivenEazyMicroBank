package com.eazybytes.loans.command.controller;


import com.eazybytes.loans.command.CreateLoanCommand;
import com.eazybytes.loans.command.DeleteLoanCommand;
import com.eazybytes.loans.command.UpdateLoanCommand;
import com.eazybytes.loans.constants.LoansConstants;
import com.eazybytes.loans.dto.LoansDto;
import com.eazybytes.loans.dto.ResponseDto;
import com.eazybytes.loans.service.ILoansService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

/**
 * Rest controller to handle loan commands. The commands are handled by axon
 * framework and are used to create, update and delete loans.
 */
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
@RequiredArgsConstructor
public class LoanCommandController {

    private final ILoansService iLoansService;
    private final CommandGateway commandGateway;


    /**
     * Create a new loan with given mobile number.
     *
     * @param mobileNumber mobile¯ number of the customer. It must be a 10 digit number.
     * @return response with status 201 and a success message.
     */
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createLoan(@RequestParam("mobileNumber")
                                                  @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits") String mobileNumber) {
        long randomLoanNumber = 1000000000L + new Random().nextInt(900000000);
        CreateLoanCommand createCommand = CreateLoanCommand.builder()
                .loanNumber(randomLoanNumber).mobileNumber(mobileNumber)
                .loanType(LoansConstants.HOME_LOAN).totalLoan(LoansConstants.NEW_LOAN_LIMIT)
                .amountPaid(0).outstandingAmount(LoansConstants.NEW_LOAN_LIMIT)
                .activeSw(LoansConstants.ACTIVE_SW).build();
        commandGateway.sendAndWait(createCommand);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(LoansConstants.STATUS_201, LoansConstants.MESSAGE_201));
    }

    /**
     * Update a loan with given loan number. The loan must already be created.
     *
     * @param loansDto the loan details to update.
     * @return response with status 200 and a success message.
     */
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateLoanDetails(@Valid @RequestBody LoansDto loansDto) {
        UpdateLoanCommand updateCommand = UpdateLoanCommand.builder()
                .loanNumber(loansDto.getLoanNumber()).mobileNumber(loansDto.getMobileNumber())
                .loanType(LoansConstants.HOME_LOAN).totalLoan(loansDto.getTotalLoan())
                .amountPaid(loansDto.getAmountPaid()).outstandingAmount(loansDto.getOutstandingAmount())
                .activeSw(LoansConstants.ACTIVE_SW).build();
        commandGateway.sendAndWait(updateCommand);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(LoansConstants.STATUS_200, LoansConstants.MESSAGE_200));
    }

    /**
     * Delete a loan with given loan number. The loan must already be created.
     *
     * @param loanNumber the loan number to delete.
     * @return response with status 200 and a success message.
     */
    @PatchMapping("/delete")
    public ResponseEntity<ResponseDto> deleteLoanDetails(@RequestParam("loanNumber") Long loanNumber) {
        DeleteLoanCommand deleteCommand = DeleteLoanCommand.builder()
                .loanNumber(loanNumber).activeSw(LoansConstants.IN_ACTIVE_SW).build();
        commandGateway.sendAndWait(deleteCommand);
        return ResponseEntity
                .status(org.springframework.http.HttpStatus.OK)
                .body(new ResponseDto(LoansConstants.STATUS_200, LoansConstants.MESSAGE_200));
    }

}
