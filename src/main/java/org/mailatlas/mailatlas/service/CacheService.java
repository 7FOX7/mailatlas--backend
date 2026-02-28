package org.mailatlas.mailatlas.service;

import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.mailatlas.mailatlas.entity.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CacheService {
    // inject redis
    @Autowired
    private RedisTemplate<String, Object> cache;
    private final Integer DEFAULT_TTL = 30;
    // provide a default message
    private StringBuilder messageBuilder = new StringBuilder("(Redis): ");

    public UserData getData(@Email String email) {
        if (!cache.hasKey(email)) {
            String message = messageBuilder.append("key " + email + " doesn't exist. Make sure you're adding it.").toString();
            log.warn(message);
            return null;
        }

        Object cached = cache.opsForHash().get(email, email);
        // use object mapper to convert cached data to user data
        return (new ObjectMapper()).convertValue(cached, UserData.class);
    }

    public void setData(UserData userData) {
        // extract email
        @Email String email = userData.getEmail();

        if (cache.hasKey(email)) {
            String message = messageBuilder.append("key " + email + " already existed for some reason. Deletion will be performed first before putting email.").toString();
            log.warn(message);
            cache.delete(email);
        }
        cache.opsForHash().put(email, email, userData);
        // set ttl
        cache.expire(email, DEFAULT_TTL, TimeUnit.DAYS);
    }
}
