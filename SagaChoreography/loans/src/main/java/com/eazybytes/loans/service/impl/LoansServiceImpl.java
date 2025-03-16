package com.eazybytes.loans.service.impl;

import com.eazybytes.common.dto.MobileNumberUpdateDto;
import com.eazybytes.loans.constants.LoansConstants;
import com.eazybytes.loans.dto.LoansDto;
import com.eazybytes.loans.entity.Loans;
import com.eazybytes.loans.exception.LoanAlreadyExistsException;
import com.eazybytes.loans.exception.ResourceNotFoundException;
import com.eazybytes.loans.mapper.LoansMapper;
import com.eazybytes.loans.repository.LoansRepository;
import com.eazybytes.loans.service.ILoansService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
@Slf4j
public class LoansServiceImpl implements ILoansService {

    private final LoansRepository loansRepository;
    private final StreamBridge streamBridge;

    /**
     * @param mobileNumber - Mobile Number of the Customer
     */
    @Override
    public void createLoan(String mobileNumber) {
        Optional<Loans> optionalLoan = loansRepository.findByMobileNumberAndActiveSw(mobileNumber,
                LoansConstants.ACTIVE_SW);
        if (optionalLoan.isPresent()) {
            throw new LoanAlreadyExistsException("Loan already registered with given mobileNumber " + mobileNumber);
        }
        loansRepository.save(createNewLoan(mobileNumber));
    }

    /**
     * @param mobileNumber - Mobile Number of the Customer
     * @return the new loan details
     */
    private Loans createNewLoan(String mobileNumber) {
        Loans newLoan = new Loans();
        long randomLoanNumber = 1000000000L + new Random().nextInt(900000000);
        newLoan.setLoanNumber(randomLoanNumber);
        newLoan.setMobileNumber(mobileNumber);
        newLoan.setLoanType(LoansConstants.HOME_LOAN);
        newLoan.setTotalLoan(LoansConstants.NEW_LOAN_LIMIT);
        newLoan.setAmountPaid(0);
        newLoan.setOutstandingAmount(LoansConstants.NEW_LOAN_LIMIT);
        newLoan.setActiveSw(LoansConstants.ACTIVE_SW);
        return newLoan;
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
        return LoansMapper.mapToLoansDto(loan, new LoansDto());
    }

    /**
     * @param loansDto - LoansDto Object
     * @return boolean indicating if the update of loan details is successful or not
     */
    @Override
    public boolean updateLoan(LoansDto loansDto) {
        Loans loan = loansRepository.findByMobileNumberAndActiveSw(loansDto.getMobileNumber(),
                LoansConstants.ACTIVE_SW).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "LoanNumber", loansDto.getLoanNumber().toString()));
        LoansMapper.mapToLoans(loansDto, loan);
        loansRepository.save(loan);
        return true;
    }

    /**
     * @param loanNumber - Input Loan Number
     * @return boolean indicating if the delete of loan details is successful or not
     */
    @Override
    public boolean deleteLoan(Long loanNumber) {
        Loans loan = loansRepository.findById(loanNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "loanNumber", loanNumber.toString())
                );
        loan.setActiveSw(LoansConstants.IN_ACTIVE_SW);
        loansRepository.save(loan);
        return true;
    }

    @Override
    @Transactional
    public boolean updateMobileNumber(MobileNumberUpdateDto mobileNumberUpdateDto) {
        boolean result = false;
        try {
            String currentMobileNum = mobileNumberUpdateDto.getCurrentMobileNumber();
            Loans loans = loansRepository.findByMobileNumberAndActiveSw(currentMobileNum,
                    true).orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", currentMobileNum)
            );
            loans.setMobileNumber(mobileNumberUpdateDto.getNewMobileNumber());
            loansRepository.save(loans);
//             throw new RuntimeException("Some error occurred while updating mobileNumber");
            updateMobileNumberStatus(mobileNumberUpdateDto);
        } catch (Exception exception) {
            log.error("Error occurred while updating mobile number", exception);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            rollbackCardMobileNumber(mobileNumberUpdateDto);
        }
        return result;
    }

    private void updateMobileNumberStatus(MobileNumberUpdateDto mobileNumberUpdateDto) {
        log.info("Sending updateMobileNumberStatus request for the details: {}", mobileNumberUpdateDto);
        var result = streamBridge.send("updateMobileNumberStatus-out-0", mobileNumberUpdateDto);
        log.info("Is the updateMobileNumberStatus request successfully triggered ? : {}", result);
    }

    private void rollbackCardMobileNumber(MobileNumberUpdateDto mobileNumberUpdateDto) {
        log.info("Sending rollbackCardMobileNumber request for the details: {}", mobileNumberUpdateDto);
        var result = streamBridge.send("rollbackCardMobileNumber-out-0",mobileNumberUpdateDto);
        log.info("Is the rollbackCardMobileNumber request successfully triggered ? : {}", result);
    }

}
