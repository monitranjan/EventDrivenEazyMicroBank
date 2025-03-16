package com.eazybytes.profile.query.handler;

import com.eazybytes.profile.dto.ProfileDto;
import com.eazybytes.profile.query.FindProfileQuery;
import com.eazybytes.profile.service.IProfileService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileQueryHandler {

    private final IProfileService iProfileService;

    @QueryHandler
    public ProfileDto findProfile(FindProfileQuery findProfileQuery) {
        return iProfileService.fetchProfile(findProfileQuery.getMobileNumber());
    }

}
