package com.eazybytes.cards.service;

import com.eazybytes.cards.dto.CardsDto;
import com.eazybytes.common.dto.MobileNumberUpdateDto;

public interface ICardsService {

    /**
     *
     * @param mobileNumber - Mobile Number of the Customer
     */
    void createCard(String mobileNumber);

    /**
     *
     * @param mobileNumber - Input mobile Number
     *  @return Card Details based on a given mobileNumber
     */
    CardsDto fetchCard(String mobileNumber);

    /**
     *
     * @param cardsDto - CardsDto Object
     * @return boolean indicating if the update of card details is successful or not
     */
    boolean updateCard(CardsDto cardsDto);

    /**
     *
     * @param cardNumber - Input Card Number
     * @return boolean indicating if the delete of card details is successful or not
     */
    boolean deleteCard(Long cardNumber);

    /**
     * @param mobileNumberUpdateDto - MobileNumberUpdateDto object
     * @return boolean indicating if the update of mobile number is successful or not
     */
    boolean updateMobileNumber(MobileNumberUpdateDto mobileNumberUpdateDto);

    /**
     * @param mobileNumberUpdateDto - MobileNumberUpdateDto object
     * @return boolean indicating if the update of mobile number is successful or not
     */
    boolean rollbackMobileNumber(MobileNumberUpdateDto mobileNumberUpdateDto);

}
