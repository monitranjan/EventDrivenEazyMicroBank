package com.eazybytes.loans.service;

import com.eazybytes.loans.command.event.LoanUpdatedEvent;
import com.eazybytes.loans.dto.LoansDto;
import com.eazybytes.loans.entity.Loans;

public interface ILoansService {

    /**
     * @param loan - Loans object
     */
    void createLoan(Loans loan);

    /**
     * @param mobileNumber - Input mobile Number
     * @return Loan Details based on a given mobileNumber
     */
    LoansDto fetchLoan(String mobileNumber);

    /**
     * @param event - LoanUpdatedEvent Object
     * @return boolean indicating if the update of card details is successful or not
     */
    boolean updateLoan(LoanUpdatedEvent event);

    /**
     * @param loanNumber - Input Loan Number
     * @return boolean indicating if the delete of loan details is successful or not
     */
    boolean deleteLoan(Long loanNumber);

    /**
     * @param oldMobileNumber - Old mobile number of Loan
     *  @param newMobileNumber - New mobile number of Loan
     * @return boolean indicating if the update of mobile number is successful or not
     */
    boolean updateMobileNumber(String oldMobileNumber, String newMobileNumber);

}
