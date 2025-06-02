package com.gravitlauncher.simplecabinet.web.service.captcha;

public interface CaptchaService {
    boolean verify(String captchaResponse);
}
