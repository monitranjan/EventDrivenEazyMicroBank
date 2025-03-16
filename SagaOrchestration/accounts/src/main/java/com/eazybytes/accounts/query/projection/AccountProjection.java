package com.eazybytes.accounts.query.projection;

import com.eazybytes.accounts.command.event.AccountCreatedEvent;
import com.eazybytes.accounts.command.event.AccountDeletedEvent;
import com.eazybytes.accounts.command.event.AccountUpdatedEvent;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.service.IAccountsService;
import com.eazybytes.common.event.AccntMobNumRollbackedEvent;
import com.eazybytes.common.event.AccntMobileNumUpdatedEvent;
import com.eazybytes.common.event.CusMobNumUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("account-group")
public class AccountProjection {

    private final IAccountsService iAccountsService;

    @EventHandler
    public void on(AccountCreatedEvent event) {
        Accounts accountEntity = new Accounts();
        BeanUtils.copyProperties(event, accountEntity);
        iAccountsService.createAccount(accountEntity);
    }

    @EventHandler
    public void on(AccountUpdatedEvent event) {
        iAccountsService.updateAccount(event);
    }

    @EventHandler
    public void on(AccountDeletedEvent event) {
        iAccountsService.deleteAccount(event.getAccountNumber());
    }

    @EventHandler
    public void on(AccntMobileNumUpdatedEvent accntMobileNumUpdatedEvent) {
        iAccountsService.updateMobileNumber(accntMobileNumUpdatedEvent.getMobileNumber(), accntMobileNumUpdatedEvent.getNewMobileNumber());
    }

    @EventHandler
    public void on(AccntMobNumRollbackedEvent accntMobNumRollbackedEvent) {
        iAccountsService.updateMobileNumber(accntMobNumRollbackedEvent.getNewMobileNumber(), accntMobNumRollbackedEvent.getMobileNumber());
    }

}