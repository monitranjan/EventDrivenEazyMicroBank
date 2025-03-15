package com.eazybytes.customer.controller;

import com.eazybytes.customer.constants.CustomerConstants;
import com.eazybytes.customer.dto.CustomerDto;
import com.eazybytes.customer.dto.ResponseDto;
import com.eazybytes.customer.service.ICustomerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class CustomerController {

    private final ICustomerService iCustomerService;

    public CustomerController(ICustomerService iCustomerService) {
        this.iCustomerService = iCustomerService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createCustomer(@Valid @RequestBody CustomerDto customerDto) {
        customerDto.setCustomerId(UUID.randomUUID().toString());
        iCustomerService.createCustomer(customerDto);
        return ResponseEntity
                .status(org.springframework.http.HttpStatus.CREATED)
                .body(new ResponseDto(CustomerConstants.STATUS_201, CustomerConstants.MESSAGE_201));
    }

    @GetMapping("/fetch")
    public ResponseEntity<CustomerDto> fetchCustomerDetails(@RequestParam("mobileNumber")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    String mobileNumber) {
        CustomerDto fetchedCustomer = iCustomerService.fetchCustomer(mobileNumber);
        return ResponseEntity.status(org.springframework.http.HttpStatus.OK).body(fetchedCustomer);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateCustomerDetails(@Valid @RequestBody CustomerDto customerDto) {
        boolean isUpdated = iCustomerService.updateCustomer(customerDto);
        if (isUpdated) {
            return ResponseEntity
                    .status(org.springframework.http.HttpStatus.OK)
                    .body(new ResponseDto(CustomerConstants.STATUS_200, CustomerConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(CustomerConstants.STATUS_500,
                            CustomerConstants.MESSAGE_500_UPDATE));
        }
    }

    @PatchMapping("/delete")
    public ResponseEntity<ResponseDto> deleteCustomer(@RequestParam("customerId")
    @Pattern(regexp = "(^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$)",
            message = "CustomerId is invalid") String customerId) {
        boolean isDeleted = iCustomerService.deleteCustomer(customerId);
        if (isDeleted) {
            return ResponseEntity
                    .status(org.springframework.http.HttpStatus.OK)
                    .body(new ResponseDto(CustomerConstants.STATUS_200,
                            CustomerConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(CustomerConstants.STATUS_500,
                            CustomerConstants.MESSAGE_500_DELETE));
        }
    }

}
