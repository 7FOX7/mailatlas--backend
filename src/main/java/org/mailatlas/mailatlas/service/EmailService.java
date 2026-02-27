package org.mailatlas.mailatlas.service;

import jakarta.validation.constraints.Email;
import org.mailatlas.mailatlas.entity.UserData;
import org.mailatlas.mailatlas.repository.UserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    // inject redis
    @Autowired
    private CacheService cacheService;
    // inject database
    @Autowired
    private UserDataRepository userDataRepository;
    // inject DNS
    @Autowired
    private DNSService dnsService;

    public UserData getData(@Email String email) {
        UserData userData;
        // check cache first
        userData = cacheService.getData(email);
        if (userData != null) {
            return userData;
        }
        // check database
        userData = userDataRepository.findByEmail(email);
        if (userData != null) {
            return userData;
        }
        // check external APIs
        userData = dnsService.getData(email);
        if (userData != null) {
            // cache data so we don't call the API again
            cacheService.setData(userData);
            // add data to the db
            userDataRepository.save(userData);
            return userData;
        }
        return null;
    }
}

/*
TODO:
1. Verify the email format - DONE;

We're verifying the email when:
- .getData(email) from EmailService expects 'email' param to be a valid email (used in the Controller)
- .getData(email) from CacheService expects 'email' param to be a valid email (used in the EmailService)
- .findByEmail(email) from UserDataRepository expects 'email' param to be a valid email (used in the EmailService)

2. Verify the Host name using checking its MX records in the DNS -
Steps:
- Send a request to the API which allows to check if MX records exist in the DNS
- Create a DNSService with methods to lookup MX records for the domain, extract the domain from the email,

3. Verify the email is reachable (after verifying its MX records exist in the DNS) using STMP
*/