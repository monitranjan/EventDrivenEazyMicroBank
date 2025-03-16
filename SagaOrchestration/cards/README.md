# CQRS and Event Sourcing setup in cards

### 1. Add the following maven dependency inside **cards/pom.xml**

```
<dependency>
    <groupId>org.axonframework</groupId>
    <artifactId>axon-spring-boot-starter</artifactId>
</dependency>
```

### 2. Add the following property inside application.yml

```yaml
axon:
  eventhandling:
    processors:
      card-group:
        mode: subscribing
  axonserver:
    servers: localhost:8124
```

### 3. Create the following subpackages

- com.eazybytes.cards.command
    - aggregate
    - controller
    - event
    - interceptor
- com.eazybytes.cards.query
    - controller
    - handler
    - projection

### 4. Create the following classes under the respective packages

For the actual source code, please refer to the GitHub repo,

- com.eazybytes.cards.command
    - CreateCardCommand
    - DeleteCardCommand
    - UpdateCardCommand
- com.eazybytes.cards.command.event
    - CardCreatedEvent
    - CardDeletedEvent
    - CardUpdatedEvent
- com.eazybytes.cards.command.aggregate
    - CardAggregate
- com.eazybytes.cards.command.controller
    - CardCommandController
- com.eazybytes.cards.command.interceptor
    - CardCommandInterceptor
- com.eazybytes.cards.query
    - FindCardQuery
- com.eazybytes.cards.query.projection
    - CardProjection
- com.eazybytes.cards.query.handler
    - CardQueryHandler
- com.eazybytes.cards.query.controller
    - CardQueryController

### 5. Create the following method in CardsRepository

```java
Optional<Cards> findByCardNumberAndActiveSw(Long cardNumber, boolean activeSw);
```

### 6. Create the following method in CardMapper

```java
public static Cards mapEventToCard(CardUpdatedEvent event, Cards card) {
  card.setCardType(event.getCardType());
  card.setTotalLimit(event.getTotalLimit());
  card.setAmountUsed(event.getAmountUsed());
  card.setAvailableAmount(event.getAvailableAmount());
  return card;
}
```

### 7. Update the ICardsService with the below abstract methods

Once the interface is updated, update the CardsServiceImpl class as well with the code present in the repository

```java
public interface ICardsService {

  /**
   *
   * @param card - Cards Object
   */
  void createCard(Cards card);

  /**
   *
   * @param mobileNumber - Input mobile Number
   *  @return Card Details based on a given mobileNumber
   */
  CardsDto fetchCard(String mobileNumber);

  /**
   *
   * @param event - CardUpdatedEvent Object
   * @return boolean indicating if the update of card details is successful or not
   */
  boolean updateCard(CardUpdatedEvent event);

  /**
   *
   * @param cardNumber - Input Card Number
   * @return boolean indicating if the delete of card details is successful or not
   */
  boolean deleteCard(Long cardNumber);

}
```

### 8. Delete the CardsController class & it's package as we separated our APIs in to Commands and Queries

### 9. Add the below method inside the GlobalExceptionHandler class

```java

@ExceptionHandler(CommandExecutionException.class)
public ResponseEntity<ErrorResponseDto> handleGlobalException(CommandExecutionException exception,
        WebRequest webRequest) {
  ErrorResponseDto errorResponseDTO = new ErrorResponseDto(
          webRequest.getDescription(false),
          HttpStatus.INTERNAL_SERVER_ERROR,
          "CommandExecutionException occurred due to: "+exception.getMessage(),
          LocalDateTime.now()
  );
  return new ResponseEntity<>(errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
}
```

### 10. Inside the CardsApplication class, make the following changes

```java
package com.eazybytes.cards;

import com.eazybytes.cards.command.interceptor.CardCommandInterceptor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.PropagatingErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class CardsApplication {

  public static void main(String[] args) {
    SpringApplication.run(CardsApplication.class, args);
  }

  @Autowired
  public void registerCustomerCommandInterceptor(ApplicationContext context, CommandGateway commandGateway) {
    commandGateway.registerDispatchInterceptor(context.getBean(CardCommandInterceptor.class));
  }

  @Autowired
  public void configure(EventProcessingConfigurer config) {
    config.registerListenerInvocationErrorHandler("card-group",
            conf -> PropagatingErrorHandler.instance());
  }

}
```

---