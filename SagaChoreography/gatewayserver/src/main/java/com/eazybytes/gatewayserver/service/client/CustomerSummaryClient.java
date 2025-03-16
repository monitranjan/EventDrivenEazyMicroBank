package com.eazybytes.gatewayserver.service.client;

import com.eazybytes.gatewayserver.dto.AccountsDto;
import com.eazybytes.gatewayserver.dto.CardsDto;
import com.eazybytes.gatewayserver.dto.CustomerDto;
import com.eazybytes.gatewayserver.dto.LoansDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import reactor.core.publisher.Mono;

public interface CustomerSummaryClient {

    @GetExchange(value= "/eazybank/customer/api/fetch", accept = "application/json")
    Mono<ResponseEntity<CustomerDto>> fetchCustomerDetails(@RequestParam("mobileNumber") String mobileNumber);

    @GetExchange(value= "/eazybank/accounts/api/fetch", accept = "application/json")
    Mono<ResponseEntity<AccountsDto>> fetchAccountDetails(@RequestParam("mobileNumber") String mobileNumber);

    @GetExchange(value= "/eazybank/loans/api/fetch", accept = "application/json")
    Mono<ResponseEntity<LoansDto>> fetchLoanDetails(@RequestParam("mobileNumber") String mobileNumber);

    @GetExchange(value= "/eazybank/cards/api/fetch", accept = "application/json")
    Mono<ResponseEntity<CardsDto>> fetchCardDetails(@RequestParam("mobileNumber") String mobileNumber);

}
