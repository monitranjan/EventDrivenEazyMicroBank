package com.eazybytes.cards.query.projection;

import com.eazybytes.cards.command.event.CardCreatedEvent;
import com.eazybytes.cards.command.event.CardDeletedEvent;
import com.eazybytes.cards.command.event.CardUpdatedEvent;
import com.eazybytes.cards.entity.Cards;
import com.eazybytes.cards.service.ICardsService;
import com.eazybytes.common.event.CardMobNumRollbackedEvent;
import com.eazybytes.common.event.CardMobileNumUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("card-group")
public class CardProjection {

    private final ICardsService iCardsService;

    @EventHandler
    public void on(CardCreatedEvent event) {
        Cards cardEntity = new Cards();
        BeanUtils.copyProperties(event, cardEntity);
        iCardsService.createCard(cardEntity);
    }

    @EventHandler
    public void on(CardUpdatedEvent event) {
        iCardsService.updateCard(event);
    }

    @EventHandler
    public void on(CardDeletedEvent event) {
        iCardsService.deleteCard(event.getCardNumber());
    }

    @EventHandler
    public void on(CardMobileNumUpdatedEvent event) {
        iCardsService.updateMobileNumber(event.getMobileNumber(), event.getNewMobileNumber());
    }

    @EventHandler
    public void on(CardMobNumRollbackedEvent event) {
        iCardsService.updateMobileNumber(event.getNewMobileNumber(), event.getMobileNumber());
    }

}
