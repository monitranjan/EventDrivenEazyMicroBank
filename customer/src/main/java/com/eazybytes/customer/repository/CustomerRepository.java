package com.eazybytes.customer.repository;

import com.eazybytes.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    Optional<Customer> findByMobileNumberAndActiveSw(String mobileNumber,boolean active);


}
