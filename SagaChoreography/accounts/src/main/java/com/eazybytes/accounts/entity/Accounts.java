package com.eazybytes.accounts.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Accounts extends BaseEntity {

    @Id
    private Long accountNumber;
    private String accountType;
    private String branchAddress;
    private String mobileNumber;
    private boolean activeSw;

}
