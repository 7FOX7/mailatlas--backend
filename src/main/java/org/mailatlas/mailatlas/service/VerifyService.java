package org.mailatlas.mailatlas.service;

import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.mailatlas.mailatlas.entity.UserData;
import org.mailatlas.mailatlas.repository.UserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VerifyService {
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
            // add to cache
            cacheService.setData(userData);
            return userData;
        }
        // check external APIs
        userData = dnsService.getData(email);
        if (userData != null) {
            // cache data so we don't call the API again
            cacheService.setData(userData);
            // add data to the db
            userDataRepository.save(userData);
            log.info("Just added email " + email + " to cache and db.");
            return userData;
        }
        return null;
    }
}