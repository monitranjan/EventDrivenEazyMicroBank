package com.eazybytes.cards.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Builder
@Data
public class UpdateCardCommand {

    @TargetAggregateIdentifier
    private final Long cardNumber;
    private final String mobileNumber;
    private final String cardType;
    private final int totalLimit;
    private final int amountUsed;
    private final int availableAmount;
    private final boolean activeSw;

}
