package org.mailatlas.mailatlas.service;

import jakarta.validation.constraints.Email;
import org.mailatlas.mailatlas.entity.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheService {
    // inject redis
    @Autowired
    private RedisTemplate<String, Object> cache;

    public UserData getData(@Email String email) {
        // check if there's a user data in cache
        return (UserData) cache.opsForValue().get(email);
    }

    public void setData(UserData userData) {
        // extract email
        @Email String email = userData.getEmail();
        cache.opsForSet().add(email, userData);
        // set ttl
        cache.expire(email, 60, TimeUnit.DAYS);
    }
}
