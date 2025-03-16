package com.eazybytes.accounts.command.interceptor;

import com.eazybytes.accounts.command.CreateAccountCommand;
import com.eazybytes.accounts.command.DeleteAccountCommand;
import com.eazybytes.accounts.command.UpdateAccountCommand;
import com.eazybytes.accounts.constants.AccountsConstants;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.exception.AccountAlreadyExistsException;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.repository.AccountsRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class AccountsCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private final AccountsRepository accountsRepository;

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull List<? extends
            CommandMessage<?>> messages) {
        return (index, command) -> {
            if (CreateAccountCommand.class.equals(command.getPayloadType())) {
                CreateAccountCommand createAccountCommand = (CreateAccountCommand) command.getPayload();
                Optional<Accounts> optionalAccounts = accountsRepository.findByMobileNumberAndActiveSw(
                        createAccountCommand.getMobileNumber(), true);
                if (optionalAccounts.isPresent()) {
                    throw new AccountAlreadyExistsException("Account already created with given mobileNumber "
                            + createAccountCommand.getMobileNumber());
                }
            } else if (UpdateAccountCommand.class.equals(command.getPayloadType())) {
                UpdateAccountCommand updateAccountCommand = (UpdateAccountCommand) command.getPayload();
                Accounts account = accountsRepository.findByMobileNumberAndActiveSw
                        (updateAccountCommand.getMobileNumber(), true).orElseThrow(() ->
                        new ResourceNotFoundException("Account", "mobileNumber", updateAccountCommand.getMobileNumber()));
            } else if (DeleteAccountCommand.class.equals(command.getPayloadType())) {
                DeleteAccountCommand deleteAccountCommand = (DeleteAccountCommand) command.getPayload();
                Accounts account = accountsRepository.findByAccountNumberAndActiveSw(deleteAccountCommand.getAccountNumber(),
                        AccountsConstants.ACTIVE_SW).orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber",
                        deleteAccountCommand.getAccountNumber().toString()));
            }
            return command;
        };
    }

}
