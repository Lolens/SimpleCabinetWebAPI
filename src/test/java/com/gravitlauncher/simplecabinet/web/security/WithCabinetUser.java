package com.gravitlauncher.simplecabinet.web.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCabinetUserSecurityContextFactory.class)
public @interface WithCabinetUser {
    long userId() default -1;

    String username() default "";
}
