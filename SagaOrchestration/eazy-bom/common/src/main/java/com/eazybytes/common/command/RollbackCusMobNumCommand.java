package com.eazybytes.common.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class RollbackCusMobNumCommand {

    @TargetAggregateIdentifier
    private final String customerId;
    private final String mobileNumber;
    private final String newMobileNumber;
    private final String errorMsg;

}
