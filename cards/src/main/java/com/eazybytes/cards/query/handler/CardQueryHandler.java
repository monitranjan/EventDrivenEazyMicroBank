package com.eazybytes.cards.query.handlers;

import com.eazybytes.cards.dto.CardsDto;
import com.eazybytes.cards.query.FindCardQuery;
import com.eazybytes.cards.service.ICardsService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardQueryHandler {

    private final ICardsService iCardsService;

    @QueryHandler
    public CardsDto findCard(FindCardQuery query) {
        CardsDto card = iCardsService.fetchCard(query.getMobileNumber());
        return card;
    }

}
