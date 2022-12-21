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

    private final LoadingCache<Integer, Object> otpCache;

    public OtpService() {
        final Integer EXPIRE_MIN = 5;
        otpCache = CacheBuilder.newBuilder()
                .expireAfterWrite(EXPIRE_MIN, TimeUnit.MINUTES)
                .build(new CacheLoader<Integer, Object>() {
                    @Override
                    public Long load(Integer key) throws Exception {
                        return null;
                    }
                });
    }

    public Integer generateOtp(Object value) {
        Random random = new Random();
        Integer otpValue;
        do {
            otpValue = 100000 + random.nextInt(900000);
        } while(otpCache.getIfPresent(otpValue) != null);
        otpCache.put(otpValue, value);
        log.info("Generated OTP: {}", otpValue);
        return otpValue;
    }

    public Object validateOTP(Integer otpValue) {
        Object value = otpCache.getIfPresent(otpValue);
        if (value != null) {
            otpCache.invalidate(otpValue);
            return value;
        } else {
            throw new RuntimeException("OTP không hợp lệ!");
        }
    }

}
