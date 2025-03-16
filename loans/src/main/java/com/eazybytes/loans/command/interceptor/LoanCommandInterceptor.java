package com.eazybytes.loans.command.interceptor;

import com.eazybytes.loans.command.CreateLoanCommand;
import com.eazybytes.loans.command.DeleteLoanCommand;
import com.eazybytes.loans.command.UpdateLoanCommand;
import com.eazybytes.loans.constants.LoansConstants;
import com.eazybytes.loans.entity.Loans;
import com.eazybytes.loans.exception.LoanAlreadyExistsException;
import com.eazybytes.loans.exception.ResourceNotFoundException;
import com.eazybytes.loans.repository.LoansRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class LoanCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private final LoansRepository loansRepository;

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull List<? extends
            CommandMessage<?>> messages) {
        return (index, command) -> {
            if (CreateLoanCommand.class.equals(command.getPayloadType())) {
                CreateLoanCommand createLoanCommand = (CreateLoanCommand) command.getPayload();
                Optional<Loans> optionalLoans = loansRepository.findByMobileNumberAndActiveSw(
                        createLoanCommand.getMobileNumber(), true);
                if (optionalLoans.isPresent()) {
                    throw new LoanAlreadyExistsException("Loan already created with given mobileNumber "
                            + createLoanCommand.getMobileNumber());
                }
            } else if (UpdateLoanCommand.class.equals(command.getPayloadType())) {
                UpdateLoanCommand updateLoanCommand = (UpdateLoanCommand) command.getPayload();
                Loans loan = loansRepository.findByMobileNumberAndActiveSw
                        (updateLoanCommand.getMobileNumber(), true).orElseThrow(() ->
                        new ResourceNotFoundException("Loan", "mobileNumber", updateLoanCommand.getMobileNumber()));
            } else if (DeleteLoanCommand.class.equals(command.getPayloadType())) {
                DeleteLoanCommand deleteLoanCommand = (DeleteLoanCommand) command.getPayload();
                Loans loan = loansRepository.findByLoanNumberAndActiveSw(deleteLoanCommand.getLoanNumber(),
                        LoansConstants.ACTIVE_SW).orElseThrow(() -> new ResourceNotFoundException("Loan", "loanNumber",
                        deleteLoanCommand.getLoanNumber().toString()));
            }
            return command;
        };
    }
}
