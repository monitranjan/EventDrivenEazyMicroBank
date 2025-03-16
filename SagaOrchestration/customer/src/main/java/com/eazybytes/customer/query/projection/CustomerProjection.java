package com.eazybytes.customer.query.projection;

import com.eazybytes.common.event.CusMobNumRollbackedEvent;
import com.eazybytes.common.event.CusMobNumUpdatedEvent;
import com.eazybytes.customer.command.event.CustomerCreatedEvent;
import com.eazybytes.customer.command.event.CustomerDeletedEvent;
import com.eazybytes.customer.command.event.CustomerUpdatedEvent;
import com.eazybytes.customer.entity.Customer;
import com.eazybytes.customer.service.ICustomerService;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("customer-group")
public class CustomerProjection {

    private final ICustomerService iCustomerService;

    @EventHandler
    public void on(CustomerCreatedEvent customerCreatedEvent) {
        Customer customerEntity = new Customer();
        BeanUtils.copyProperties(customerCreatedEvent,customerEntity);
        iCustomerService.createCustomer(customerEntity);
    }

    @EventHandler
    public void on(CustomerUpdatedEvent customerUpdatedEvent) {
        iCustomerService.updateCustomer(customerUpdatedEvent);
    }

    @EventHandler
    public void on(CustomerDeletedEvent customerDeletedEvent) {
        iCustomerService.deleteCustomer(customerDeletedEvent.getCustomerId());
    }

    @EventHandler
    public void on(CusMobNumUpdatedEvent cusMobNumUpdatedEvent) {
        iCustomerService.updateMobileNumber(cusMobNumUpdatedEvent.getMobileNumber(), cusMobNumUpdatedEvent.getNewMobileNumber());
    }

    @EventHandler
    public void on(CusMobNumRollbackedEvent cusMobNumRollbackedEvent) {
        iCustomerService.updateMobileNumber(cusMobNumRollbackedEvent.getNewMobileNumber(), cusMobNumRollbackedEvent.getMobileNumber());
    }

}
