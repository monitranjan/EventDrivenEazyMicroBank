package com.eazybytes.customer.query.handler;

import com.eazybytes.customer.dto.CustomerDto;
import com.eazybytes.customer.query.FindCustomerQuery;
import com.eazybytes.customer.service.ICustomerService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerQueryHandler {

    private final ICustomerService iCustomerService;

    @QueryHandler
    public CustomerDto findCustomer(FindCustomerQuery findCustomerQuery) {
        return iCustomerService.fetchCustomer(findCustomerQuery.getMobileNumber());
    }

}
