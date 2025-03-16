# CQRS and Event Sourcing setup in customer

### 1. Add the following maven dependency inside **customer/pom.xml**

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
      customer-group:
        mode: subscribing
  axonserver:
    servers: localhost:8124
```

### 3. Create the following subpackages

- com.eazybytes.customer.command
    - aggregate
    - controller
    - event
    - interceptor
- com.eazybytes.customer.query
    - controller
    - handler
    - projection

### 4. Create the following classes under the respective packages

For the actual source code, please refer to the GitHub repo,

- com.eazybytes.customer.command
    - CreateCustomerCommand
    - DeleteCustomerCommand
    - UpdateCustomerCommand
- com.eazybytes.customer.command.event
    - CustomerCreatedEvent
    - CustomerDeletedEvent
    - CustomerUpdatedEvent
- com.eazybytes.customer.command.aggregate
    - CustomerAggregate
- com.eazybytes.customer.command.controller
    - CustomerCommandController
- com.eazybytes.customer.command.interceptor
    - CustomerCommandInterceptor
- com.eazybytes.customer.query
    - FindCustomerQuery
- com.eazybytes.customer.query.projection
    - CustomerProjection
- com.eazybytes.customer.query.handler
    - CustomersQueryHandler
- com.eazybytes.customer.query.controller
    - CustomerQueryController

### 4. Create the following method in CustomerRepository

```java
Optional<Customer> findByCustomerIdAndActiveSw(String customerId, boolean active);
```

### 4. Create the following method in CustomerMapper

```java
public static Customer mapEventToCustomer(CustomerUpdatedEvent event, Customer customer) {
    customer.setName(event.getName());
    customer.setEmail(event.getEmail());
    return customer;
}
```

### 5. Update the ICustomerService with the below abstract methods

Once the interface is updated, update the CustomerServiceImpl class as well with the code present in the repository

```java
public interface ICustomerService {

    /**
     * @param customer - Customer Object
     */
    void createCustomer(Customer customer);

    /**
     * @param mobileNumber - Input Mobile Number
     * @return Customer Details based on a given mobileNumber
     */
    CustomerDto fetchCustomer(String mobileNumber);

    /**
     * @param event - CustomerUpdatedEvent Object
     * @return boolean indicating if the update of Customer details is successful or not
     */
    boolean updateCustomer(CustomerUpdatedEvent event);

    /**
     * @param customerId - Input Customer ID
     * @return boolean indicating if the delete of Customer details is successful or not
     */
    boolean deleteCustomer(String customerId);
}
```

### 6. Delete the CustomerController class & it's package as we separated our APIs in to Commands and Queries

### 7. Add the below method inside the GlobalExceptionHandler class

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

### 8. Inside the CustomersApplication class, make the following changes

```java
package com.eazybytes.customer;

import com.eazybytes.common.config.AxonConfig;
import com.eazybytes.customer.command.interceptor.CustomerCommandInterceptor;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.PropagatingErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@Import({AxonConfig.class})
public class CustomersApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomersApplication.class, args);
    }

    @Autowired
    public void registerCustomerCommandInterceptor(ApplicationContext context,
            CommandBus commandBus) {
        commandBus.registerDispatchInterceptor(context.getBean(CustomerCommandInterceptor.class));
    }

    @Autowired
    public void configure(EventProcessingConfigurer config) {
        config.registerListenerInvocationErrorHandler("customer-group",
                conf -> PropagatingErrorHandler.instance());
    }

}
```

---