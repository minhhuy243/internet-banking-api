package com.internetbanking.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final LoadingCache<String, Integer> otpCache;

    public OtpService() {
        final Integer EXPIRE_MIN = 30;
        otpCache = CacheBuilder.newBuilder()
                .expireAfterWrite(EXPIRE_MIN, TimeUnit.SECONDS)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String s) throws Exception {
                        return 0;
                    }
                });
    }

    public Integer generateOtp(String key)
    {
        // generate otp
        Random random = new Random();
        int otpValue = 100000 + random.nextInt(900000);
        otpCache.put(key, otpValue);
        log.info("Generated OTP: {}", otpValue);

        return otpValue;
    }

    public Boolean validateOTP(String key, Integer otpNumber)
    {
        // get OTP from cache
        Integer cacheOTP = otpCache.getIfPresent(key);
        if (cacheOTP != null && cacheOTP.equals(otpNumber))
        {
            otpCache.invalidate(key);
            return true;
        }
        return false;
    }
}
