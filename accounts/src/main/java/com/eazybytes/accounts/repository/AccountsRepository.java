package com.eazybytes.accounts.repository;

import com.eazybytes.accounts.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Long> {

    Optional<Accounts> findByMobileNumberAndActiveSw(String mobileNumber, boolean active);
    Optional<Accounts> findByAccountNumberAndActiveSw(long accountNumber, boolean active);
}
