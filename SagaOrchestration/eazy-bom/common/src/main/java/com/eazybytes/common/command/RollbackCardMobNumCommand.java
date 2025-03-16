package com.eazybytes.common.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class RollbackCardMobNumCommand {

    @TargetAggregateIdentifier
    private final Long cardNumber;
    private final Long accountNumber;
    private final String customerId;
    private final String mobileNumber;
    private final String newMobileNumber;
    private final String errorMsg;

}
