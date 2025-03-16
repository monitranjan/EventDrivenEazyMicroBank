package com.eazybytes.cards.command.aggregate;

import com.eazybytes.cards.command.CreateCardCommand;
import com.eazybytes.cards.command.DeleteCardCommand;
import com.eazybytes.cards.command.UpdateCardCommand;
import com.eazybytes.cards.command.event.CardCreatedEvent;
import com.eazybytes.cards.command.event.CardDeletedEvent;
import com.eazybytes.cards.command.event.CardUpdatedEvent;
import com.eazybytes.common.command.RollbackCardMobNumCommand;
import com.eazybytes.common.command.UpdateCardMobileNumCommand;
import com.eazybytes.common.event.CardMobNumRollbackedEvent;
import com.eazybytes.common.event.CardMobileNumUpdatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
public class CardAggregate {

    @AggregateIdentifier
    private Long cardNumber;
    private String mobileNumber;
    private String cardType;
    private int totalLimit;
    private int amountUsed;
    private int availableAmount;
    private boolean activeSw;
    private String errorMsg;

    public CardAggregate() {
    }

    @CommandHandler
    public CardAggregate(CreateCardCommand createCommand) {
        CardCreatedEvent cardCreatedEvent = new CardCreatedEvent();
        BeanUtils.copyProperties(createCommand, cardCreatedEvent);
        AggregateLifecycle.apply(cardCreatedEvent);
    }

    @EventSourcingHandler
    public void on(CardCreatedEvent cardCreatedEvent) {
        this.cardNumber = cardCreatedEvent.getCardNumber();
        this.mobileNumber = cardCreatedEvent.getMobileNumber();
        this.cardType = cardCreatedEvent.getCardType();
        this.totalLimit = cardCreatedEvent.getTotalLimit();
        this.amountUsed = cardCreatedEvent.getAmountUsed();
        this.availableAmount = cardCreatedEvent.getAvailableAmount();
        this.activeSw = cardCreatedEvent.isActiveSw();
    }

    @CommandHandler
    public void handle(UpdateCardCommand updateCommand) {
        CardUpdatedEvent cardUpdatedEvent = new CardUpdatedEvent();
        BeanUtils.copyProperties(updateCommand, cardUpdatedEvent);
        AggregateLifecycle.apply(cardUpdatedEvent);
    }

    @EventSourcingHandler
    public void on(CardUpdatedEvent cardUpdatedEvent) {
        this.cardType = cardUpdatedEvent.getCardType();
        this.totalLimit = cardUpdatedEvent.getTotalLimit();
        this.amountUsed = cardUpdatedEvent.getAmountUsed();
        this.availableAmount = cardUpdatedEvent.getAvailableAmount();
    }

    @CommandHandler
    public void handle(DeleteCardCommand deleteCommand) {
        CardDeletedEvent cardDeletedEvent = new CardDeletedEvent();
        BeanUtils.copyProperties(deleteCommand, cardDeletedEvent);
        AggregateLifecycle.apply(cardDeletedEvent);
    }

    @EventSourcingHandler
    public void on(CardDeletedEvent cardDeletedEvent) {
        this.activeSw = cardDeletedEvent.isActiveSw();
    }

    @CommandHandler
    public void handle(UpdateCardMobileNumCommand updateCardMobileNumCommand) {
        CardMobileNumUpdatedEvent cardMobileNumUpdatedEvent = new CardMobileNumUpdatedEvent();
        BeanUtils.copyProperties(updateCardMobileNumCommand, cardMobileNumUpdatedEvent);
        AggregateLifecycle.apply(cardMobileNumUpdatedEvent);
    }

    @EventSourcingHandler
    public void on(CardMobileNumUpdatedEvent cardMobileNumUpdatedEvent) {
        this.mobileNumber = cardMobileNumUpdatedEvent.getNewMobileNumber();
    }

    @CommandHandler
    public void handle(RollbackCardMobNumCommand rollbackCardMobNumCommand) {
        CardMobNumRollbackedEvent cardMobNumRollbackedEvent = new CardMobNumRollbackedEvent();
        BeanUtils.copyProperties(rollbackCardMobNumCommand, cardMobNumRollbackedEvent);
        AggregateLifecycle.apply(cardMobNumRollbackedEvent);
    }

    @EventSourcingHandler
    public void on(CardMobNumRollbackedEvent cardMobNumRollbackedEvent) {
        this.mobileNumber = cardMobNumRollbackedEvent.getMobileNumber();
        this.errorMsg = cardMobNumRollbackedEvent.getErrorMsg();
    }

}
