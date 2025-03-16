package com.eazybytes.loans.mapper;

import com.eazybytes.loans.command.event.LoanUpdatedEvent;
import com.eazybytes.loans.dto.LoansDto;
import com.eazybytes.loans.entity.Loans;

public class LoansMapper {

    public static LoansDto mapToLoansDto(Loans loans, LoansDto loansDto) {
        loansDto.setLoanNumber(loans.getLoanNumber());
        loansDto.setLoanType(loans.getLoanType());
        loansDto.setMobileNumber(loans.getMobileNumber());
        loansDto.setTotalLoan(loans.getTotalLoan());
        loansDto.setAmountPaid(loans.getAmountPaid());
        loansDto.setOutstandingAmount(loans.getOutstandingAmount());
        loansDto.setActiveSw(loans.isActiveSw());
        return loansDto;
    }

    public static Loans mapToLoans(LoansDto loansDto, Loans loans) {
        loans.setLoanType(loansDto.getLoanType());
        loans.setTotalLoan(loansDto.getTotalLoan());
        loans.setAmountPaid(loansDto.getAmountPaid());
        loans.setOutstandingAmount(loansDto.getOutstandingAmount());
        return loans;
    }

    public static Loans mapEventToLoan(LoanUpdatedEvent event, Loans loan) {
        loan.setLoanType(event.getLoanType());
        loan.setTotalLoan(event.getTotalLoan());
        loan.setAmountPaid(event.getAmountPaid());
        loan.setOutstandingAmount(event.getOutstandingAmount());
        return loan;
    }

}
