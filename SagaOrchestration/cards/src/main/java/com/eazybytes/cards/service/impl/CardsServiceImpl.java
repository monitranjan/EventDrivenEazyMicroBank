package com.eazybytes.cards.service.impl;

import com.eazybytes.cards.command.event.CardUpdatedEvent;
import com.eazybytes.cards.constants.CardsConstants;
import com.eazybytes.cards.dto.CardsDto;
import com.eazybytes.cards.entity.Cards;
import com.eazybytes.cards.exception.CardAlreadyExistsException;
import com.eazybytes.cards.exception.ResourceNotFoundException;
import com.eazybytes.cards.mapper.CardsMapper;
import com.eazybytes.cards.repository.CardsRepository;
import com.eazybytes.cards.service.ICardsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CardsServiceImpl implements ICardsService {

    private CardsRepository cardsRepository;

    /**
     * @param card - Cards object
     */
    @Override
    public void createCard(Cards card) {
        Optional<Cards> optionalCard = cardsRepository.findByMobileNumberAndActiveSw(card.getMobileNumber(),
                CardsConstants.ACTIVE_SW);
        if (optionalCard.isPresent()) {
            throw new CardAlreadyExistsException("Card already registered with given mobileNumber " + card.getMobileNumber());
        }
        cardsRepository.save(card);
    }

    /**
     * @param mobileNumber - Input mobile Number
     * @return Card Details based on a given mobileNumber
     */
    @Override
    public CardsDto fetchCard(String mobileNumber) {
        Cards card = cardsRepository.findByMobileNumberAndActiveSw(mobileNumber, CardsConstants.ACTIVE_SW)
                .orElseThrow(() -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber)
                );
        return CardsMapper.mapToCardsDto(card, new CardsDto());
    }

    /**
     * @param event - CardUpdatedEvent Object
     * @return boolean indicating if the update of card details is successful or not
     */
    @Override
    public boolean updateCard(CardUpdatedEvent event) {
        Cards card = cardsRepository.findByMobileNumberAndActiveSw(event.getMobileNumber(),
                CardsConstants.ACTIVE_SW).orElseThrow(() -> new ResourceNotFoundException("Card", "CardNumber",
                event.getMobileNumber()));
        CardsMapper.mapEventToCard(event, card);
        cardsRepository.save(card);
        return true;
    }

    /**
     * @param cardNumber - Input Card Number
     * @return boolean indicating if the delete of card details is successful or not
     */
    @Override
    public boolean deleteCard(Long cardNumber) {
        Cards card = cardsRepository.findById(cardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Card", "cardNumber", cardNumber.toString())
                );
        card.setActiveSw(CardsConstants.IN_ACTIVE_SW);
        cardsRepository.save(card);
        return true;
    }

    @Override
    public boolean updateMobileNumber(String oldMobileNumber, String newMobileNumber) {
        Cards card = cardsRepository.findByMobileNumberAndActiveSw(oldMobileNumber,
                CardsConstants.ACTIVE_SW).orElseThrow(() -> new ResourceNotFoundException("Card", "mobileNumber", oldMobileNumber));
        card.setMobileNumber(newMobileNumber);
        cardsRepository.save(card);
        return true;
    }


}
