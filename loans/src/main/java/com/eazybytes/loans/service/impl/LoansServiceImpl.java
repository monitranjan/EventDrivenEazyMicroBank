package com.eazybytes.loans.service.impl;

import com.eazybytes.common.event.LoanDataChangedEvent;
import com.eazybytes.loans.command.event.LoanUpdatedEvent;
import com.eazybytes.loans.constants.LoansConstants;
import com.eazybytes.loans.dto.LoansDto;
import com.eazybytes.loans.entity.Loans;
import com.eazybytes.loans.exception.LoanAlreadyExistsException;
import com.eazybytes.loans.exception.ResourceNotFoundException;
import com.eazybytes.loans.mapper.LoansMapper;
import com.eazybytes.loans.repository.LoansRepository;
import com.eazybytes.loans.service.ILoansService;
import lombok.AllArgsConstructor;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class LoansServiceImpl implements ILoansService {

    private LoansRepository loansRepository;

    private EventGateway eventGateway;

    /**
     * @param loan - Loans object
     */
    @Override
    public void createLoan(Loans loan) {
        Optional<Loans> optionalLoans = loansRepository.findByMobileNumberAndActiveSw(loan.getMobileNumber(),
                LoansConstants.ACTIVE_SW);
        if (optionalLoans.isPresent()) {
            throw new LoanAlreadyExistsException("Loan already registered with given mobileNumber " + loan.getMobileNumber());
        }
        loansRepository.save(loan);
    }

    /**
     * @param mobileNumber - Input mobile Number
     * @return Loan Details based on a given mobileNumber
     */
    @Override
    public LoansDto fetchLoan(String mobileNumber) {
        Loans loan = loansRepository.findByMobileNumberAndActiveSw(mobileNumber, LoansConstants.ACTIVE_SW)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "mobileNumber", mobileNumber)
                );
        LoansDto loansDto = LoansMapper.mapToLoansDto(loan, new LoansDto());
        return loansDto;
    }

    /**
     * @param event - LoanUpdatedEvent Object
     * @return boolean indicating if the update of loan details is successful or not
     */
    @Override
    public boolean updateLoan(LoanUpdatedEvent event) {
        Loans loan = loansRepository.findByMobileNumberAndActiveSw(event.getMobileNumber(),
                LoansConstants.ACTIVE_SW).orElseThrow(() -> new ResourceNotFoundException("Loan", "mobileNumber",
                event.getMobileNumber()));
        LoansMapper.mapEventToLoan(event, loan);
        loansRepository.save(loan);
        return true;
    }

    /**
     * @param loanNumber - Input Loan Number
     * @return boolean indicating if the delete of loan details is successful or not
     */
    @Override
    public boolean deleteLoan(Long loanNumber) {
        Loans loan = loansRepository.findById(loanNumber).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "loanNumber", loanNumber.toString())
        );
        loan.setActiveSw(LoansConstants.IN_ACTIVE_SW);
        loansRepository.save(loan);
        LoanDataChangedEvent loanDataChangedEvent = new LoanDataChangedEvent();
        loanDataChangedEvent.setMobileNumber(loan.getMobileNumber());
        loanDataChangedEvent.setLoanNumber(0L);
        eventGateway.publish(loanDataChangedEvent);
        return true;
    }


}
