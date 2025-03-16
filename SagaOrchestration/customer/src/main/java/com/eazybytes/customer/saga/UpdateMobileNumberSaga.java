package com.eazybytes.customer.saga;

import com.eazybytes.common.command.*;
import com.eazybytes.common.event.*;
import com.eazybytes.customer.constants.CustomerConstants;
import com.eazybytes.customer.dto.ResponseDto;
import com.eazybytes.customer.query.FindCustomerQuery;
import com.eazybytes.customer.query.FindUpdateMobileSagaQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;

@Saga
@Slf4j
public class UpdateMobileNumberSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryUpdateEmitter queryUpdateEmitter;

    @StartSaga
    @SagaEventHandler(associationProperty = "customerId")
    public void handle(CusMobNumUpdatedEvent event) {
        log.info("Saga Event 1 [Start] : Received CusMobNumUpdatedEvent for customerId: {}", event.getCustomerId());
        UpdateAccntMobileNumCommand command = UpdateAccntMobileNumCommand.builder()
                .accountNumber(event.getAccountNumber())
                .cardNumber(event.getCardNumber())
                .loanNumber(event.getLoanNumber())
                .customerId(event.getCustomerId())
                .mobileNumber(event.getMobileNumber())
                .newMobileNumber(event.getNewMobileNumber()).build();
        commandGateway.send(command, new CommandCallback<>() {  //dispatch to account service
            @Override
            public void onResult(@Nonnull CommandMessage<? extends UpdateAccntMobileNumCommand> commandMessage,
                    @Nonnull CommandResultMessage<?> commandResultMessage) {
                if (commandResultMessage.isExceptional()) {
                    RollbackCusMobNumCommand rollbackCusMobNumCommand = RollbackCusMobNumCommand.builder()
                            .customerId(event.getCustomerId())
                            .mobileNumber(event.getMobileNumber())
                            .newMobileNumber(event.getNewMobileNumber())
                            .errorMsg(commandResultMessage.exceptionResult().getMessage()).build();
                    commandGateway.sendAndWait(rollbackCusMobNumCommand);
                }
            }
        });

    }

    @SagaEventHandler(associationProperty = "customerId")
    public void handle(AccntMobileNumUpdatedEvent event) {
        log.info("Saga Event 2 : Received AccntMobileNumUpdatedEvent for accountNumber: {}", event.getAccountNumber());
        UpdateCardMobileNumCommand command = UpdateCardMobileNumCommand.builder()
                .accountNumber(event.getAccountNumber())
                .cardNumber(event.getCardNumber())
                .loanNumber(event.getLoanNumber())
                .customerId(event.getCustomerId())
                .mobileNumber(event.getMobileNumber())
                .newMobileNumber(event.getNewMobileNumber()).build();
        commandGateway.send(command, new CommandCallback<>() {
            @Override
            public void onResult(@Nonnull CommandMessage<? extends UpdateCardMobileNumCommand> commandMessage,
                    @Nonnull CommandResultMessage<?> commandResultMessage) {
                if (commandResultMessage.isExceptional()) {
                    RollbackAccntMobNumCommand rollbackAccntMobNumCommand = RollbackAccntMobNumCommand.builder()
                            .accountNumber(event.getAccountNumber())
                            .customerId(event.getCustomerId())
                            .mobileNumber(event.getMobileNumber())
                            .newMobileNumber(event.getNewMobileNumber())
                            .errorMsg(commandResultMessage.exceptionResult().getMessage()).build();
                    commandGateway.sendAndWait(rollbackAccntMobNumCommand);
                }
            }
        });
    }

    @SagaEventHandler(associationProperty = "customerId")
    public void handle(CardMobileNumUpdatedEvent event) {
        log.info("Saga Event 3 : Received CardMobileNumUpdatedEvent for cardNumber: {}", event.getCardNumber());
        UpdateLoanMobileNumCommand command = UpdateLoanMobileNumCommand.builder()
                .accountNumber(event.getAccountNumber())
                .cardNumber(event.getCardNumber())
                .loanNumber(event.getLoanNumber())
                .customerId(event.getCustomerId())
                .mobileNumber(event.getMobileNumber())
                .newMobileNumber(event.getNewMobileNumber()).build();
        commandGateway.send(command, new CommandCallback<>() {
            @Override
            public void onResult(@Nonnull CommandMessage<? extends UpdateLoanMobileNumCommand> commandMessage,
                    @Nonnull CommandResultMessage<?> commandResultMessage) {
                if (commandResultMessage.isExceptional()) {
                    RollbackCardMobNumCommand rollbackCardMobNumCommand = RollbackCardMobNumCommand.builder()
                            .cardNumber(event.getCardNumber())
                            .accountNumber(event.getAccountNumber())
                            .customerId(event.getCustomerId())
                            .mobileNumber(event.getMobileNumber())
                            .newMobileNumber(event.getNewMobileNumber())
                            .errorMsg(commandResultMessage.exceptionResult().getMessage()).build();
                    commandGateway.sendAndWait(rollbackCardMobNumCommand);
                }
            }
        });
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "customerId")
    public void handle(LoanMobileNumUpdatedEvent event) {
        log.info("Saga Event 4 [END] : Received LoanMobileNumUpdatedEvent for loanNumber: {}", event.getLoanNumber());
        queryUpdateEmitter.emit(FindCustomerQuery.class, query -> true,
                new ResponseDto(CustomerConstants.STATUS_200, CustomerConstants.MOBILE_UPD_SUCCESS_MESSAGE));
    }

    @SagaEventHandler(associationProperty = "customerId")
    public void handle(CardMobNumRollbackedEvent event) {
        log.info("Saga Compensation Event : Received CardMobNumRollbackedEvent for cardNumber: {}", event.getCardNumber());
        RollbackAccntMobNumCommand rollbackAccntMobNumCommand = RollbackAccntMobNumCommand.builder()
                .accountNumber(event.getAccountNumber())
                .customerId(event.getCustomerId())
                .mobileNumber(event.getMobileNumber())
                .newMobileNumber(event.getNewMobileNumber())
                .errorMsg(event.getErrorMsg()).build();
        commandGateway.send(rollbackAccntMobNumCommand);
    }

    @SagaEventHandler(associationProperty = "customerId")
    public void handle(AccntMobNumRollbackedEvent event) {
        log.info("Saga Compensation Event : Received AccntMobNumRollbackedEvent for accountNumber: {}", event.getAccountNumber());
        RollbackCusMobNumCommand rollbackCusMobNumCommand = RollbackCusMobNumCommand.builder()
                .customerId(event.getCustomerId())
                .mobileNumber(event.getMobileNumber())
                .newMobileNumber(event.getNewMobileNumber())
                .errorMsg(event.getErrorMsg()).build();
        commandGateway.send(rollbackCusMobNumCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "customerId")
    public void handle(CusMobNumRollbackedEvent event) {
        log.info("Saga Compensation Event [END] : Received CusMobNumRollbackedEvent for customerId: {}",
                event.getCustomerId());
        queryUpdateEmitter.emit(FindCustomerQuery.class, query -> true,
                new ResponseDto(CustomerConstants.STATUS_500, CustomerConstants.MOBILE_UPD_FAILURE_MESSAGE));
    }

}
