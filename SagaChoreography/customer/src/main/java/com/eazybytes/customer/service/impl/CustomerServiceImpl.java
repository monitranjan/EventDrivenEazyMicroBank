package com.eazybytes.customer.service.impl;

import com.eazybytes.common.dto.MobileNumberUpdateDto;
import com.eazybytes.customer.constants.CustomerConstants;
import com.eazybytes.customer.dto.CustomerDto;
import com.eazybytes.customer.entity.Customer;
import com.eazybytes.customer.exception.CustomerAlreadyExistsException;
import com.eazybytes.customer.exception.ResourceNotFoundException;
import com.eazybytes.customer.mapper.CustomerMapper;
import com.eazybytes.customer.repository.CustomerRepository;
import com.eazybytes.customer.service.ICustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CustomerServiceImpl implements ICustomerService {

    private CustomerRepository customerRepository;
    private final StreamBridge streamBridge;
    @Override
    public void createCustomer(CustomerDto customerDto) {
        customerDto.setActiveSw(CustomerConstants.ACTIVE_SW);
        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Optional<Customer> optionalCustomer = customerRepository.findByMobileNumberAndActiveSw(
                customerDto.getMobileNumber(), true);
        if (optionalCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already registered with given mobileNumber "
                    + customerDto.getMobileNumber());
        }
        Customer savedCustomer = customerRepository.save(customer);
    }

    @Override
    public CustomerDto fetchCustomer(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumberAndActiveSw(mobileNumber, true).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        return customerDto;
    }

    @Override
    public boolean updateCustomer(CustomerDto customerDto) {
        Customer customer = customerRepository.findByMobileNumberAndActiveSw(customerDto.getMobileNumber(), true)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", customerDto.getMobileNumber()));
        CustomerMapper.mapToCustomer(customerDto, customer);
        customerRepository.save(customer);
        return true;
    }

    @Override
    public boolean deleteCustomer(String customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "customerId", customerId)
        );
        customer.setActiveSw(CustomerConstants.IN_ACTIVE_SW);
        customerRepository.save(customer);
        return true;
    }
    @Override
    @Transactional
    public boolean updateMobileNumber(MobileNumberUpdateDto mobileNumberUpdateDto) {
        String currentMobileNum = mobileNumberUpdateDto.getCurrentMobileNumber();
        Customer customer = customerRepository.findByMobileNumberAndActiveSw(currentMobileNum,
                true).orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", currentMobileNum)
        );
        customer.setMobileNumber(mobileNumberUpdateDto.getNewMobileNumber());
        customerRepository.save(customer);
        // throw new RuntimeException("Some error occurred while updating mobileNumber");
        updateAccountMobileNumber(mobileNumberUpdateDto);
        return true;
    }

    private void updateAccountMobileNumber(MobileNumberUpdateDto mobileNumberUpdateDto) {
        log.info("Sending updateAccountMobileNumber request for the details: {}", mobileNumberUpdateDto);
        var result = streamBridge.send("updateAccountMobileNumber-out-0",mobileNumberUpdateDto);
        log.info("Is the updateAccountMobileNumber request successfully triggered ? : {}", result);
    }

    @Override
    public boolean rollbackMobileNumber(MobileNumberUpdateDto mobileNumberUpdateDto) {
        String newMobileNumber = mobileNumberUpdateDto.getNewMobileNumber();
        Customer customer = customerRepository.findByMobileNumberAndActiveSw(newMobileNumber,
                true).orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", newMobileNumber)
        );
        customer.setMobileNumber(mobileNumberUpdateDto.getCurrentMobileNumber());
        customerRepository.save(customer);
        return true;
    }
}
