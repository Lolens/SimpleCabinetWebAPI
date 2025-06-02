package com.gravitlauncher.simplecabinet.web.service.captcha;

import jakarta.annotation.Priority;
import org.springframework.stereotype.Service;

@Service
@Priority(value = 1)
public class NoCaptchaService implements CaptchaService {
    @Override
    public boolean verify(String captchaResponse) {
        return true;
    }
}
