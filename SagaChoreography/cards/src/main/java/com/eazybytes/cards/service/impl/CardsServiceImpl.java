package com.eazybytes.cards.service.impl;

import com.eazybytes.cards.constants.CardsConstants;
import com.eazybytes.cards.dto.CardsDto;
import com.eazybytes.cards.entity.Cards;
import com.eazybytes.cards.exception.CardAlreadyExistsException;
import com.eazybytes.cards.exception.ResourceNotFoundException;
import com.eazybytes.cards.mapper.CardsMapper;
import com.eazybytes.cards.repository.CardsRepository;
import com.eazybytes.cards.service.ICardsService;
import com.eazybytes.common.dto.MobileNumberUpdateDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
@Slf4j
public class CardsServiceImpl implements ICardsService {

    private final CardsRepository cardsRepository;
    private final StreamBridge streamBridge;

    /**
     * @param mobileNumber - Mobile Number of the Customer
     */
    @Override
    public void createCard(String mobileNumber) {
        Optional<Cards> optionalCard = cardsRepository.findByMobileNumberAndActiveSw(mobileNumber,
                CardsConstants.ACTIVE_SW);
        if (optionalCard.isPresent()) {
            throw new CardAlreadyExistsException("Card already registered with given mobileNumber " + mobileNumber);
        }
        cardsRepository.save(createNewCard(mobileNumber));
    }

    /**
     * @param mobileNumber - Mobile Number of the Customer
     * @return the new card details
     */
    private Cards createNewCard(String mobileNumber) {
        Cards newCard = new Cards();
        long randomCardNumber = 100000000000L + new Random().nextInt(900000000);
        newCard.setCardNumber(randomCardNumber);
        newCard.setMobileNumber(mobileNumber);
        newCard.setCardType(CardsConstants.CREDIT_CARD);
        newCard.setTotalLimit(CardsConstants.NEW_CARD_LIMIT);
        newCard.setAmountUsed(0);
        newCard.setAvailableAmount(CardsConstants.NEW_CARD_LIMIT);
        newCard.setActiveSw(CardsConstants.ACTIVE_SW);
        return newCard;
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
     * @param cardsDto - CardsDto Object
     * @return boolean indicating if the update of card details is successful or not
     */
    @Override
    public boolean updateCard(CardsDto cardsDto) {
        Cards card = cardsRepository.findByMobileNumberAndActiveSw(cardsDto.getMobileNumber(),
                CardsConstants.ACTIVE_SW).orElseThrow(() -> new ResourceNotFoundException("Card", "CardNumber",
                cardsDto.getCardNumber().toString()));
        CardsMapper.mapToCards(cardsDto, card);
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
    @Transactional
    public boolean updateMobileNumber(MobileNumberUpdateDto mobileNumberUpdateDto) {
        boolean result = false;
        try {
            String currentMobileNum = mobileNumberUpdateDto.getCurrentMobileNumber();
            Cards cards = cardsRepository.findByMobileNumberAndActiveSw(currentMobileNum,
                    true).orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", currentMobileNum)
            );
            cards.setMobileNumber(mobileNumberUpdateDto.getNewMobileNumber());
            cardsRepository.save(cards);
            updateLoanMobileNumber(mobileNumberUpdateDto);
        }catch (Exception exception) {
            log.error("Error occurred while updating mobile number", exception);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            rollbackAccountMobileNumber(mobileNumberUpdateDto);
        }
        return result;
    }

    private void updateLoanMobileNumber(MobileNumberUpdateDto mobileNumberUpdateDto) {
        log.info("Sending updateLoanMobileNumber request for the details: {}", mobileNumberUpdateDto);
        var result = streamBridge.send("updateLoanMobileNumber-out-0",mobileNumberUpdateDto);
        log.info("Is the updateLoanMobileNumber request successfully triggered ? : {}", result);
    }

    private void rollbackAccountMobileNumber(MobileNumberUpdateDto mobileNumberUpdateDto) {
        log.info("Sending rollbackAccountMobileNumber request for the details: {}", mobileNumberUpdateDto);
        var result = streamBridge.send("rollbackAccountMobileNumber-out-0",mobileNumberUpdateDto);
        log.info("Is the rollbackAccountMobileNumber request successfully triggered ? : {}", result);
    }

    @Override
    public boolean rollbackMobileNumber(MobileNumberUpdateDto mobileNumberUpdateDto) {
        String newMobileNumber = mobileNumberUpdateDto.getNewMobileNumber();
        Cards cards = cardsRepository.findByMobileNumberAndActiveSw(newMobileNumber,
                true).orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", newMobileNumber)
        );
        cards.setMobileNumber(mobileNumberUpdateDto.getCurrentMobileNumber());
        cardsRepository.save(cards);
        rollbackAccountMobileNumber(mobileNumberUpdateDto);
        return true;
    }


}
