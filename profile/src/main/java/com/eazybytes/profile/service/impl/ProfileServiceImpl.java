package com.eazybytes.profile.service.impl;

import com.eazybytes.common.event.AccountDataChangedEvent;
import com.eazybytes.common.event.CardDataChangedEvent;
import com.eazybytes.common.event.CustomerDataChangedEvent;
import com.eazybytes.common.event.LoanDataChangedEvent;
import com.eazybytes.profile.constants.ProfileConstants;
import com.eazybytes.profile.dto.ProfileDto;
import com.eazybytes.profile.entity.Profile;
import com.eazybytes.profile.exception.ResourceNotFoundException;
import com.eazybytes.profile.mapper.ProfileMapper;
import com.eazybytes.profile.repository.ProfileRepository;
import com.eazybytes.profile.service.IProfileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProfileServiceImpl implements IProfileService {

    private ProfileRepository profileRepository;

    @Override
    public void handleCustomerDataChangedEvent(CustomerDataChangedEvent customerDataChangedEvent) {
        Profile profile = profileRepository.findByMobileNumberAndActiveSw(customerDataChangedEvent.getMobileNumber(),
                ProfileConstants.ACTIVE_SW).orElseGet(Profile::new);
        profile.setMobileNumber(customerDataChangedEvent.getMobileNumber());
        if (customerDataChangedEvent.getName() != null) {
            profile.setName(customerDataChangedEvent.getName());
        }
        profile.setActiveSw(customerDataChangedEvent.isActiveSw());
        profileRepository.save(profile);
    }

    @Override
    public void handleAccountDataChangedEvent(AccountDataChangedEvent accountDataChangedEvent) {
        Profile profile = profileRepository.findByMobileNumberAndActiveSw(accountDataChangedEvent.getMobileNumber(),
                        ProfileConstants.ACTIVE_SW)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "mobileNumber", accountDataChangedEvent.getMobileNumber()));
        profile.setAccountNumber(accountDataChangedEvent.getAccountNumber());
        profileRepository.save(profile);
    }

    @Override
    public void handleLoanDataChangedEvent(LoanDataChangedEvent loanDataChangedEvent) {
        Profile profile = profileRepository.findByMobileNumberAndActiveSw(loanDataChangedEvent.getMobileNumber(),
                        ProfileConstants.ACTIVE_SW)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "mobileNumber", loanDataChangedEvent.getMobileNumber()));
        profile.setLoanNumber(loanDataChangedEvent.getLoanNumber());
        profileRepository.save(profile);
    }

    @Override
    public void handleCardDataChangedEvent(CardDataChangedEvent customerDataChangedEvent) {
        Profile profile = profileRepository.findByMobileNumberAndActiveSw(customerDataChangedEvent.getMobileNumber(),
                        ProfileConstants.ACTIVE_SW)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "mobileNumber", customerDataChangedEvent.getMobileNumber()));
        profile.setCardNumber(customerDataChangedEvent.getCardNumber());
        profileRepository.save(profile);
    }

    @Override
    public ProfileDto fetchProfile(String mobileNumber) {
        Profile profile = profileRepository.findByMobileNumberAndActiveSw(mobileNumber, true).orElseThrow(
                () -> new ResourceNotFoundException("Profile", "mobileNumber", mobileNumber)
        );
        ProfileDto profileDto = ProfileMapper.mapToProfileDto(profile, new ProfileDto());
        return profileDto;
    }

}
